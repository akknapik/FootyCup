import { GroupTeam } from "./group-team.model";

export interface Group {
    id: number;
    name: string;
    groupTeams: GroupTeam[];
}