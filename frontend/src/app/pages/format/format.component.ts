import { Component, HostListener } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormatService } from '../../services/format.service';
import { AuthService } from '../../services/auth.service';
import { TeamService } from '../../services/team.service';
import { NotificationService } from '../../services/notification.service';

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


  constructor(private route: ActivatedRoute, private formatService: FormatService, private teamService: TeamService, public auth: AuthService, private notification: NotificationService) {}

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
    const groupCount = 3;
    const teamsPerGroup = 4;
    this.loading = true;
    this.formatService.generateGroupFormat(this.tournamentId, groupCount, teamsPerGroup).subscribe({
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
    const totalTeams = 8;
    this.loading = true;
    this.formatService.generateBracketFormat(this.tournamentId, totalTeams).subscribe({
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
    const groupCount = 2;
    const teamsPerGroup = 4;
    const advancing = 4;
    this.loading = true;
    this.formatService.generateMixedFormat(this.tournamentId, groupCount, teamsPerGroup, advancing).subscribe({
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
      const assignedTeamIds = this.groups
        .flatMap(g => g.groupTeams)
        .map(gt => gt.team?.id)
        .filter(id => !!id);
      this.availableTeams = teams.filter(t => !assignedTeamIds.includes(t.id) || t.id === slot.team?.id);
    });
  }
  
  assignTeamToSlot(slot: any) {
    this.formatService.assignTeamToSlot(this.tournamentId, slot.id, slot.team.id).subscribe(() => {
      this.editedSlot = null;
      this.loadStructure();
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
        .flatMap(n => [n.teamHome?.id, n.teamAway?.id])
        .filter(id => !!id && id !== (side === 'home' ? node.teamHome?.id : node.teamAway?.id));
      this.availableTeams = teams.filter(t => !usedTeamIds.includes(t.id));
    });
  }
  
  assignTeamToBracket(node: any, side: 'home' | 'away') {
    const team = side === 'home' ? node.teamHome : node.teamAway;
    if (!team?.id) return;
  
    const homeTeam = side === 'home';
  
    this.formatService.assignTeamToBracketNode(this.tournamentId, node.id, team.id, homeTeam).subscribe({
      next: () => {
        this.editedNode = null;
        this.editedSide = null;
        this.loadStructure(); 
      },
      error: () => this.notification.showError('Error assigning team to bracket!')
    });
  }
  
  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('select') && !target.closest('td')) {
      this.editedSlot = null;
      this.editedNode = null;
      this.editedSide = null;
    }
  }
}
