import { MatchRef } from "../../common/match-ref.model";

export interface BracketNodeResponse {
    id: number;
    round: number;
    position: number;
    parentHomeNodeId: number | null;
    parentAwayNodeId: number | null;
    matchRef: MatchRef | null;
}