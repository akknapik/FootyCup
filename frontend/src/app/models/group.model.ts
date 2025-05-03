import { Team } from "./team.model";

export interface Group {
    id: number;
    name: string;
    groupTeams: Team[];
}