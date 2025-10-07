import { TeamRef } from "../../common/team-ref.model";

export interface GroupResponse {
    id: number;
    name: string;
    slots: GroupSlotResponse[];
}

export interface GroupSlotResponse {
    id: number;
    position: number;
    teamRef: TeamRef | null;
}