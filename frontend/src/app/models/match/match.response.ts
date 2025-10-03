import { MatchEventRef } from "../common/match-event-ref.model";
import { MatchItemResponse } from "./match-item.response";

export interface MatchResponse extends MatchItemResponse {
    matchDate: string;
    matchTime: string;
    durationInMin: number;
    homeScore: number | null;
    awayScore: number | null;
    groupId?: number | null;
    events: MatchEventRef[];
}
