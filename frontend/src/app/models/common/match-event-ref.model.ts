import { PlayerRef } from "./player-ref.model";
import { TeamRef } from "./team-ref.model";

export interface MatchEventRef {
    id: number;
    team: TeamRef;
    player: PlayerRef | null;
    eventType: 'GOAL' | 'YELLOW_CARD' | 'RED_CARD';
    minute: number;
}