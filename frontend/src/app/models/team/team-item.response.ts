import { PlayerRef } from "../common/player-ref.model";
import { UserRef } from "../common/user-ref.model";

export interface TeamItemResponse {
    id: number;
    name: string;
    coach: UserRef;
    playersCount: number;
}
