import { User } from "./user.model";

export interface Tournament {
    id: number;
    name: string;
    startDate: string;     
    endDate: string;
    location?: string;      
    status: 'UPCOMING' | 'ONGOING' | 'FINISHED';  
    system?: 'GROUP' | 'BRACKET' | 'MIXED'; 
    organizer: User;
  }