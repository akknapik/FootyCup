import { GroupTeamRef } from "../../common/group-team-ref.model";

export interface GroupResponse {
    id: number;
    name: string;
    groupTeams: GroupTeamRef[];
}