import { Team } from './team.model';

export interface GroupTeam {
    id: number;
    position: number;
    team: Team | null;
    points: number;
    goalsFor: number;
    goalsAgainst: number;
}