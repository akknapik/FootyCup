import { MatchResponse } from './match/match.response';

export interface BracketNode {
    id: number;
    round: number;
    position: number;
    match: MatchResponse;
    parentHomeNode: BracketNode;
    parentAwayNode: BracketNode;
}