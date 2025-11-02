import { UserRef } from "../common/user-ref.model";
import { TournamentItemResponse } from "./tournament-item.response";

export interface TournamentResponse extends TournamentItemResponse {
    referees: UserRef[];
    scoringRules: Record<string, number>;
    qrCodeGenerated: boolean;
}