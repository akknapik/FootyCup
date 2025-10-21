import { TeamRef } from "../common/team-ref.model";
import { UserRef } from "../common/user-ref.model";

export interface MatchItemResponse {
    id: number;
    name: string | 'Match';
    status: 'SCHEDULED' | 'NOT_SCHEDULED' | 'COMPLETED';
    teamHome: TeamRef | null;
    teamAway: TeamRef | null;
    referee: UserRef | null;
}