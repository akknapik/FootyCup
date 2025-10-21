import { TeamRef } from "./team-ref.model";

export interface MatchRef {
    id: number;
    name: string;
    teamHome?: TeamRef;
    teamAway?: TeamRef;
    homeScore?: number;
    awayScore?: number;
}
