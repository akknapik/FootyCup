import { TeamRef } from "./team-ref.model";
import { UserRef } from "./user-ref.model";

export interface MatchRef {
    id: number;
    name: string;
    teamHome?: TeamRef;
    teamAway?: TeamRef;
    homeScore?: number;
    awayScore?: number;
    referee?: UserRef | null;
}
