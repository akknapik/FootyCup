import { MatchEventType } from "../match/create-match-event.request";
import { PlayerRef } from "./player-ref.model";
import { TeamRef } from "./team-ref.model";

export interface MatchEventRef {
    id: number;
    team: TeamRef;
    player: PlayerRef | null;
    secondaryPlayer: PlayerRef | null;
    eventType: MatchEventType;
    minute: number;
    description?: string | null;
}