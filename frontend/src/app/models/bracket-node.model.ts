import { Match } from './match.model';

export interface BracketNode {
    id: number;
    round: number;
    position: number;
    match: Match;
    parentHomeNode: BracketNode;
    parentAwayNode: BracketNode;
}