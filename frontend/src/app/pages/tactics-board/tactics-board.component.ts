import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatchService } from '../../services/match.service';
import { MatchResponse } from '../../models/match/match.response';
import { NotificationService } from '../../services/notification.service';
import { AuthService } from '../../services/auth.service';
import { PlayerRef } from '../../models/common/player-ref.model';
import { TeamService } from '../../services/team.service';
import { TacticsBoardService } from '../../services/tactics-board.service';
import { TacticsLayer } from '../../models/tactics/tactics-layer.model';
import { TacticsToken, TacticsTokenType } from '../../models/tactics/tactics-token.model';
import { TacticsBoardState } from '../../models/tactics/tactics-board-state.request';

interface DragContext {
  tokenId: string;
  pointerId: number;
  offsetX: number;
  offsetY: number;
}

@Component({
  selector: 'app-tactics-board',
  standalone: false,
  templateUrl: './tactics-board.component.html',
  styleUrl: './tactics-board.component.css'
})
export class TacticsBoardComponent {
  tournamentId!: number;
  matchId!: number;

  match: MatchResponse | null = null;
  isLoading = true;
  homePlayers: PlayerRef[] = [];
  awayPlayers: PlayerRef[] = [];

  layers: TacticsLayer[] = [];
  activeLayerId: string | null = null;

  private dragContext: DragContext | null = null;
  private layerDirty = false;

  @ViewChild('pitch') pitchRef?: ElementRef<HTMLDivElement>;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private matchService: MatchService,
    private teamService: TeamService,
    private tacticsBoardService: TacticsBoardService,
    private notification: NotificationService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const tournamentId = params.get('tournamentId');
      const matchId = params.get('matchId');
      if (!tournamentId || !matchId) {
        this.notification.showError('Tournament or match ID is missing.');
        this.router.navigate(['/tournaments/my']);
        return;
      }
      this.tournamentId = +tournamentId;
      this.matchId = +matchId;
      this.loadMatch();
    });
  }

  get activeLayer(): TacticsLayer | null {
    return this.layers.find(layer => layer.id === this.activeLayerId) ?? null;
  }

  get layerUpdatedAt(): string | null {
    const active = this.activeLayer;
    if (!active) {
      return null;
    }
    return new Date(active.updatedAt).toLocaleString();
  }

  private loadMatch(): void {
    this.isLoading = true;
    this.matchService.getMatch(this.tournamentId, this.matchId).subscribe({
      next: match => {
        this.match = match;
        this.isLoading = false;
        this.restoreState();
        this.loadTeamPlayers(match.teamHome?.id ?? null, 'home');
        this.loadTeamPlayers(match.teamAway?.id ?? null, 'away');
      },
      error: () => {
        this.notification.showError('Failed to load match data.');
        this.router.navigate(['/tournament', this.tournamentId, 'matches']);
      }
    });
  }

  private loadTeamPlayers(teamId: number | null, type: TacticsTokenType): void {
    if (!teamId) {
      if (type === 'home') {
        this.homePlayers = [];
      } else if (type === 'away') {
        this.awayPlayers = [];
      }
      return;
    }

    this.teamService.getTeamById(this.tournamentId, teamId).subscribe({
      next: team => {
        if (type === 'home') {
          this.homePlayers = team.players ?? [];
        } else {
          this.awayPlayers = team.players ?? [];
        }
      },
      error: () => {
        this.notification.showError('Failed to load team players.');
      }
    });
  }

  private restoreState(): void {
    this.tacticsBoardService.load(this.tournamentId, this.matchId).subscribe({
      next: saved => {
        if (saved && saved.layers && saved.layers.length > 0) {
          this.layers = saved.layers.map(layer => ({
            ...layer,
            tokens: (layer.tokens ?? []).map(token => ({
              ...token,
              x: this.clampPercent(token.x),
              y: this.clampPercent(token.y)
            }))
          }));
          const exists = this.layers.some(layer => layer.id === saved.activeLayerId);
          this.activeLayerId = exists ? saved.activeLayerId : this.layers[0].id;
        } else {
          const layer = this.createLayer('Variant 1');
          this.layers = [layer];
          this.activeLayerId = layer.id;
          this.persistState();
        }
      },
      error: () => {
        this.notification.showError('Failed to load tactics board data.');
        const layer = this.createLayer('Variant 1');
        this.layers = [layer];
        this.activeLayerId = layer.id;
      }
    });
  }

  addLayer(): void {
    const layer = this.createLayer(this.generateLayerName());
    this.layers.push(layer);
    this.activeLayerId = layer.id;
    this.persistState();
  }

  duplicateLayer(layer: TacticsLayer): void {
    const copy = this.createLayer(`${layer.name} (copy)`, layer.tokens, layer.notes ?? '');
    this.layers.push(copy);
    this.activeLayerId = copy.id;
    this.notification.showSuccess('Layer duplicated successfully.');
    this.persistState();
  }

  renameLayer(layer: TacticsLayer): void {
    const newName = prompt('Rename layer', layer.name);
    if (!newName) {
      return;
    }
    const trimmed = newName.trim();
    if (!trimmed) {
      this.notification.showError('Layer name cannot be empty.');
      return;
    }
    layer.name = trimmed;
    this.persistState();
  }

  removeLayer(layer: TacticsLayer): void {
    if (this.layers.length === 1) {
      this.notification.showInfo('At least one layer must remain.');
      return;
    }

    this.notification.confirm('Are you sure you want to delete this layer?').subscribe(confirmed => {
      if (!confirmed) {
        return;
      }
      this.layers = this.layers.filter(item => item.id !== layer.id);
      if (!this.layers.some(item => item.id === this.activeLayerId)) {
        this.activeLayerId = this.layers[0]?.id ?? null;
      }
      this.persistState();
      this.notification.showSuccess('Layer removed successfully.');
    });
  }

  selectLayer(layerId: string): void {
    if (this.activeLayerId === layerId) {
      return;
    }
    const exists = this.layers.some(layer => layer.id === layerId);
    if (!exists) {
      return;
    }
    this.activeLayerId = layerId;
    this.persistState();
  }

  addPlayerToLayer(player: PlayerRef, type: TacticsTokenType): void {
    const layer = this.activeLayer;
    if (!layer) {
      return;
    }

    const tokenId = this.getPlayerTokenId(player, type);
    const alreadyExists = layer.tokens.some(token => token.id === tokenId);
    if (alreadyExists) {
      this.notification.showInfo('This player is already added to the layer.');
      return;
    }

    const defaultX = type === 'home' ? 28 : 72;
    const sameTypeCount = layer.tokens.filter(token => token.type === type).length;
    const defaultY = 15 + sameTypeCount * 10;

    layer.tokens.push({
      id: tokenId,
      type,
      label: player.number ? `#${player.number}` : player.name,
      description: player.name,
      referenceId: player.id,
      x: this.clampPercent(defaultX),
      y: this.clampPercent(defaultY),
      color: type === 'home' ? '#1a7fdb' : '#2db783'
    });

    this.touchLayer(layer);
    this.persistState();
  }

  addOpponentToken(): void {
    const layer = this.activeLayer;
    if (!layer) {
      return;
    }

    const id = `opponent-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
    layer.tokens.push({
      id,
      type: 'opponent',
      label: 'O',
      description: 'Opponent',
      x: 50,
      y: 50,
      color: '#e71f18ff'
    });
    this.touchLayer(layer);
    this.persistState();
  }

  removeToken(tokenId: string): void {
    const layer = this.activeLayer;
    if (!layer) {
      return;
    }
    const before = layer.tokens.length;
    layer.tokens = layer.tokens.filter(token => token.id !== tokenId);
    if (layer.tokens.length !== before) {
      this.touchLayer(layer);
      this.persistState();
    }
  }

  clearLayer(): void {
    const layer = this.activeLayer;
    if (!layer) {
      return;
    }
    this.notification.confirm('Remove all elements from this layer?').subscribe(confirmed => {
      if (!confirmed) {
        return;
      }
      layer.tokens = [];
      this.touchLayer(layer);
      this.persistState();
    });
  }

  onNotesChange(): void {
    const layer = this.activeLayer;
    if (!layer) {
      return;
    }
    this.touchLayer(layer);
    this.persistState();
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  startDrag(token: TacticsToken, event: PointerEvent): void {
    if (!this.pitchRef) {
      return;
    }
    const boardRect = this.pitchRef.nativeElement.getBoundingClientRect();
    const tokenX = (token.x / 100) * boardRect.width;
    const tokenY = (token.y / 100) * boardRect.height;
    const pointerX = event.clientX - boardRect.left;
    const pointerY = event.clientY - boardRect.top;

    this.dragContext = {
      tokenId: token.id,
      pointerId: event.pointerId,
      offsetX: pointerX - tokenX,
      offsetY: pointerY - tokenY
    };
    this.layerDirty = false;
    (event.target as HTMLElement)?.setPointerCapture?.(event.pointerId);
    event.preventDefault();
  }

  @HostListener('window:pointermove', ['$event'])
  onPointerMove(event: PointerEvent): void {
    if (!this.dragContext || event.pointerId !== this.dragContext.pointerId || !this.pitchRef) {
      return;
    }
    const layer = this.activeLayer;
    if (!layer) {
      return;
    }
    const boardRect = this.pitchRef.nativeElement.getBoundingClientRect();
    const pointerX = event.clientX - boardRect.left - this.dragContext.offsetX;
    const pointerY = event.clientY - boardRect.top - this.dragContext.offsetY;
    const percentX = this.clampPercent((pointerX / boardRect.width) * 100);
    const percentY = this.clampPercent((pointerY / boardRect.height) * 100);

    const token = layer.tokens.find(item => item.id === this.dragContext!.tokenId);
    if (token) {
      token.x = percentX;
      token.y = percentY;
      this.layerDirty = true;
    }
    event.preventDefault();
  }

  @HostListener('window:pointerup', ['$event'])
  @HostListener('window:pointercancel', ['$event'])
  onPointerUp(event: PointerEvent): void {
    if (!this.dragContext || event.pointerId !== this.dragContext.pointerId) {
      return;
    }
    (event.target as HTMLElement)?.releasePointerCapture?.(event.pointerId);
    this.dragContext = null;
    if (this.layerDirty) {
      this.persistState();
      this.layerDirty = false;
    }
  }

  trackToken(index: number, token: TacticsToken): string {
    return token.id;
  }

  private generateLayerName(): string {
    const base = 'Variant';
    let counter = this.layers.length + 1;
    let name = `${base} ${counter}`;
    const names = new Set(this.layers.map(layer => layer.name));
    while (names.has(name)) {
      counter += 1;
      name = `${base} ${counter}`;
    }
    return name;
  }

  private createLayer(name: string, tokens: TacticsToken[] = [], notes: string = ''): TacticsLayer {
    const now = new Date().toISOString();
    return {
      id: `layer-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
      name,
      tokens: tokens.map(token => ({ ...token })),
      notes,
      createdAt: now,
      updatedAt: now
    };
  }

  private persistState(): void {
    if (!this.tournamentId || !this.matchId) {
      return;
    }
    const timestamp = new Date().toISOString();
    this.layers.forEach(layer => {
      if (layer.id === this.activeLayerId) {
        layer.updatedAt = timestamp;
      }
    });
    const state: TacticsBoardState = {
      layers: this.layers.map(layer => ({
        ...layer,
        tokens: layer.tokens.map(token => ({ ...token }))
      })),
      activeLayerId: this.activeLayerId,
      lastUpdated: timestamp
    };
    this.tacticsBoardService.save(this.tournamentId, this.matchId, state).subscribe({
      error: () => this.notification.showError('Failed to save tactics board state.')
    });
  }

  private clampPercent(value: number): number {
    const min = 2;
    const max = 98;
    if (Number.isNaN(value)) {
      return 50;
    }
    return Math.min(max, Math.max(min, value));
  }

  private getPlayerTokenId(player: PlayerRef, type: TacticsTokenType): string {
    return `player-${type}-${player.id}`;
  }

  private touchLayer(layer: TacticsLayer): void {
    layer.updatedAt = new Date().toISOString();
  }
}