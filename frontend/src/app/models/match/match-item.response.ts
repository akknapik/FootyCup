import { TeamRef } from "../common/team-ref.model";
import { UserRef } from "../common/user-ref.model";

export interface MatchItemResponse {
    id: number;
    name: string;
    status: 'SCHEDULED' | 'NOT_SCHEDULED' | 'COMPLETED';
    teamHome: TeamRef;
    teamAway: TeamRef;
    referee: UserRef | null;
}