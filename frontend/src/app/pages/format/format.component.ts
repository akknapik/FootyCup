import { Component, HostListener } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { FormatService } from '../../services/format.service';
import { AuthService } from '../../services/auth.service';
import { TeamService } from '../../services/team.service';
import { NotificationService } from '../../services/notification.service';
import { AssignTeamToSlotRequest } from '../../models/format/group/assign-team-to-slot.request';

@Component({
  selector: 'app-format',
  standalone: false,
  templateUrl: './format.component.html',
  styleUrl: './format.component.css'
})
export class FormatComponent {
  tournamentId!: number;
  structureExists = false;
  loading = false;

  groups: any[] = [];
  bracket: any[] = [];

  editedSlot: any = null;
  availableTeams: any[] = [];

  editedNode: any = null;
  editedSide: 'home' | 'away' | null = null;

  groupCount: number = 2;
  teamsPerGroup: number = 4;

  bracketSize: number = 8;

  mixedGroupCount: number = 2;
  mixedTeamsPerGroup: number = 4;
  mixedAdvancing: number = 4;

  slotMenuOpenForId: number | null = null;
  teamSearch = '';

  nodeMenuOpenForId: number | null = null;
  nodeMenuSide: 'home' | 'away' | null = null;
  nodeTeamSearch = '';
  nodeAvailableTeams: { id: number; name: string }[] = [];

  constructor(private route: ActivatedRoute, public router: Router, private formatService: FormatService, private teamService: TeamService, public auth: AuthService, private notification: NotificationService) {}

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.checkIfStructureExists();
  }

  checkIfStructureExists() {
    this.formatService.structureExists(this.tournamentId).subscribe({
      next: (exists) => { this.structureExists = exists;
        if (exists) {
          this.loadStructure();
        }
      },
      error: () => this.notification.showError('Error checking structure existence!')
    });
  }

  generateGroup() {
  this.loading = true;
  this.formatService.generateGroupFormat(this.tournamentId, {
    groupCount: this.groupCount,
    teamsPerGroup: this.teamsPerGroup
  }).subscribe({
    next: () => {
      this.structureExists = true;
      this.loading = false;
      this.loadStructure();
    },
    error: () => {
      this.notification.showError('Error generating group structure!');
      this.loading = false;
    }
  });
}

generateBracket() {
  this.loading = true;
  this.formatService.generateBracketFormat(this.tournamentId, {
    totalTeams: this.bracketSize
  }).subscribe({
    next: () => {
      this.structureExists = true;
      this.loading = false;
      this.loadStructure();
    },
    error: () => {
      this.notification.showError('Error generating bracket structure!');
      this.loading = false;
    }
  });
}

generateMixed() {
  this.loading = true;
  this.formatService.generateMixedFormat(this.tournamentId, {
    groupCount: this.mixedGroupCount,
    teamsPerGroup: this.mixedTeamsPerGroup,
    advancing: this.mixedAdvancing
  }).subscribe({
    next: () => {
      this.structureExists = true;
      this.loading = false;
      this.loadStructure();
    },
    error: () => {
      this.notification.showError('Error generating mixed structure!');
      this.loading = false;
    }
  });
}

  loadStructure() {
    this.formatService.getGroups(this.tournamentId).subscribe({
      next: (data) => this.groups = data,
      error: () => this.notification.showError('Error loading groups!')
    });

    this.formatService.getBracket(this.tournamentId).subscribe({
      next: (data) => this.bracket = data,
      error: () => this.notification.showError('Error loading bracket!')
    });
  }

  deleteStructures(tournamentId: number) {
    this.notification.confirm('Are you sure you want to delete the structures?').subscribe(confirmed => {
      if (confirmed) {
      this.formatService.deleteStructures(this.tournamentId).subscribe({
        next: () => {
          this.notification.showSuccess('Structures deleted!');
          this.loadStructure();
          this.structureExists = false;
          this.groups = [];
          this.bracket = [];
        },
        error: () => this.notification.showError('Error deleting structures!')
      });
    }
    });
  }

  assignTeamsRandomly(tournamentId: number) {
    this.formatService.assignTeamsRandomly(tournamentId).subscribe(() => {
      this.loadStructure(); 
    });
  }

editSlot(slot: any) {
  this.editedSlot = slot;

  this.teamService.getTeams(this.tournamentId).subscribe(teams => {
    const assignedIds = this.groups
      .flatMap(g => g.groupTeams)
      .filter(gt => gt.id !== slot.id)
      .map(gt => gt.team?.id)
      .filter((id: number | undefined) => !!id) as number[];

    this.availableTeams = teams.filter(t =>
      !assignedIds.includes(t.id) || t.id === slot.team?.id
    );
  });
}

assignTeamToSlotById(slot: any, teamId: number | null) {
  if (teamId == null) return; 

  const body: AssignTeamToSlotRequest = {
    slotId: slot.id,
    teamId, 
  };

  this.formatService.assignTeamToSlot(this.tournamentId, body).subscribe({
    next: () => {
      const picked = this.availableTeams.find(t => t.id === teamId);
      slot.team = picked ? { id: picked.id, name: picked.name } : { id: teamId, name: '' };
      this.editedSlot = null;
    },
    error: () => this.notification.showError('Error assigning team to slot!')
  });
}

 editBracketTeam(node: any, side: 'home' | 'away', event: MouseEvent) {
  event.stopPropagation();
  if (node.round !== 1) return;

  this.editedNode = node;
  this.editedSide = side;

  this.teamService.getTeams(this.tournamentId).subscribe(teams => {
    const usedTeamIds = this.bracket
      .filter(n => n.round === 1)
      .flatMap(n => [n.match?.teamHome?.id, n.match?.teamAway?.id])
      .filter(id => !!id && id !== (side === 'home' ? node.match?.teamHome?.id : node.match?.teamAway?.id));

    this.availableTeams = teams.filter(t => !usedTeamIds.includes(t.id));
  });
}

assignTeamToBracketById(node: any, side: 'home' | 'away', teamId: number) {
  if (!teamId || !node) return;

  const body = { nodeId: node.id, teamId, homeTeam: side === 'home' };

  this.formatService.assignTeamToBracketNode(this.tournamentId, body).subscribe({
    next: () => {
      const teamName = this.availableTeams.find(t => t.id === teamId)?.name ?? '';

      if (!node.matchRef) {
        node.matchRef = { id: null, name: node?.name ?? '', teamHomeId: null, teamAwayId: null, teamHomeName: '', teamAwayName: '' };
      }

      if (side === 'home') {
        node.matchRef.teamHomeId = teamId;
        node.matchRef.teamHomeName = teamName;
      } else {
        node.matchRef.teamAwayId = teamId;
        node.matchRef.teamAwayName = teamName;
      }

      this.editedNode = null;
      this.editedSide = null;
    },
    error: () => this.notification.showError('Error assigning team to bracket!')
  });
}

openSlotMenu(slot: any, event: MouseEvent) {
  event.stopPropagation();
  this.editedSlot = slot;
  this.slotMenuOpenForId = slot.id;
  this.teamSearch = '';

  // wczytanie listy dostępnych drużyn jak wcześniej
  this.teamService.getTeams(this.tournamentId).subscribe(teams => {
    const assignedTeamIds = this.groups
      .flatMap(g => g.groupTeams)
      .map(gt => gt.team?.id)
      .filter(id => !!id);

    // pozwól wybrać aktualnie przypisaną drużynę także
    this.availableTeams = teams.filter(
      t => !assignedTeamIds.includes(t.id) || t.id === slot.team?.id
    );
  });
}

// Zatwierdzenie wyboru z dropdownu (zostawiamy brak „czyszczenia” – można dodać opcję)
pickTeamForSlot(slot: any, team: any) {
  const body = { slotId: slot.id, teamId: team.id };
  this.formatService.assignTeamToSlot(this.tournamentId, body).subscribe({
    next: () => {
      // optymistycznie aktualizujemy UI
      slot.team = { id: team.id, name: team.name };
      this.closeAllInlineMenus();
    },
    error: () => this.notification.showError('Error assigning team to slot!')
  });
}

closeAllInlineMenus() {
  this.editedSlot = null;
  this.slotMenuOpenForId = null;
  this.editedNode = null;
  this.editedSide = null;
}

// Zamknij panel po kliknięciu poza
@HostListener('document:click', ['$event'])
onClickOutside(event: MouseEvent) {
  const el = event.target as HTMLElement;
  if (!el.closest('.inline-picker') && !el.closest('.inline-picker-menu')) {
    this.closeAllInlineMenus();
  }
}

// (opcjonalnie) filtrowanie listy
get filteredAvailableTeams() {
  const q = this.teamSearch.trim().toLowerCase();
  if (!q) return this.availableTeams;
  return this.availableTeams.filter(t => t.name.toLowerCase().includes(q));
}

get filteredNodeTeams() {
  const q = this.nodeTeamSearch.trim().toLowerCase();
  return q
    ? this.nodeAvailableTeams.filter(t => t.name.toLowerCase().includes(q))
    : this.nodeAvailableTeams;
}

openNodeMenu(node: any, side: 'home' | 'away', ev: MouseEvent) {
  ev.stopPropagation();
  if (node.round !== 1) return; // tylko pierwsza runda edytowalna

  this.nodeMenuOpenForId = node.id;
  this.nodeMenuSide = side;
  this.nodeTeamSearch = '';

  // pobierz drużyny i odfiltruj te użyte w R1 (poza aktualnym slotem)
  this.teamService.getTeams(this.tournamentId).subscribe(teams => {
    const usedIds = this.bracket
      .filter((n: any) => n.round === 1)
      .flatMap((n: any) => [
        n.matchRef?.teamHomeId,
        n.matchRef?.teamAwayId
      ])
      .filter((id: any) => !!id);

    const currentId = side === 'home'
      ? node.matchRef?.teamHomeId
      : node.matchRef?.teamAwayId;

    this.nodeAvailableTeams = teams.filter((t: any) =>
      !usedIds.includes(t.id) || t.id === currentId
    );
  });
}

pickTeamForNode(node: any, side: 'home' | 'away', team: { id: number; name: string }) {
  const payload = { nodeId: node.id, teamId: team.id, homeTeam: side === 'home' };

  this.formatService.assignTeamToBracketNode(this.tournamentId, payload).subscribe({
    next: () => {
      // **natychmiast** aktualizujemy lokalny widok (bez czekania na ponowne ładowanie)
      if (!node.matchRef) node.matchRef = { id: node.id, name: node.match?.name, teamHomeId: 0, teamAwayId: 0, teamHomeName: '', teamAwayName: '' };

      if (side === 'home') {
        node.matchRef.teamHomeId = team.id;
        node.matchRef.teamHomeName = team.name;
      } else {
        node.matchRef.teamAwayId = team.id;
        node.matchRef.teamAwayName = team.name;
      }

      // zamknij menu
      this.nodeMenuOpenForId = null;
      this.nodeMenuSide = null;
    },
    error: () => this.notification.showError('Error assigning team to bracket!')
  });
}

// zamykanie na klik poza dropdownem
@HostListener('document:click')
closeNodeMenus() {
  this.nodeMenuOpenForId = null;
  this.nodeMenuSide = null;
}

    logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']); 
    });
  }
}
