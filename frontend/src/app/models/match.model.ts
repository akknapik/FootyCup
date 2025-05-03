import { Team } from './team.model';

export interface Match {
    id: number;
    homeTeam: Team | null;
    awayTeam: Team | null;
    matchDate: Date;
    matchTime: string;
    matchStatus: string;
    homeScore: number | null;
    awayScore: number | null;
}