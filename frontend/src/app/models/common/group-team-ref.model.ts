import { TeamRef } from "./team-ref.model";

export interface GroupTeamRef {
    id: number;
    team?: TeamRef | null;
    position: number;
    points: number;
    goalsFor: number;
    goalsAgainst: number;
}