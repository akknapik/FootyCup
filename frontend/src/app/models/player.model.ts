import { Team } from "./team.model";

export interface Player {
    id: number;
    number : number;
    name: string;
    birthDate: string;  
    team: Team;       
  }