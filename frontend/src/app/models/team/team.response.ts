import { PlayerRef } from "../common/player-ref.model";
import { TeamItemResponse } from "./team-item.response";

export interface TeamResponse extends TeamItemResponse {
    players: PlayerRef[];
    country?: string | null;
}