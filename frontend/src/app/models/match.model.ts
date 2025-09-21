import { Team } from './team.model';
import { User } from './user.model';

export interface Match {
    id: number;
    name: string;
    teamHome: Team | null;
    teamAway: Team | null;
    matchDate: Date;
    matchTime: string;
    status: string;
    homeScore: number | null;
    awayScore: number | null;
    durationInMin: number;
    referee?: User | null;
}