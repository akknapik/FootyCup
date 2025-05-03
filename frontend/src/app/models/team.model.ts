import { Tournament } from "./tournament.model";
import { User } from "./user.model";
import { Player } from "./player.model";

export interface Team {
    id: number;
    name: string;
    coach?: User;     
    country?: string;
    tournament: Tournament;      
    playerList: Player[]; 
  }