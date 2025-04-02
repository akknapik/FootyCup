export interface Tournament {
    id: number;
    name: string;
    startDate: string;     
    endDate: string;
    location?: string;      
    status: 'UPCOMING' | 'ONGOING' | 'FINISHED';  
    system?: 'GROUP' | 'BRACKET' | 'MIXED'; 
    organizer: {
      id: number;
      firstname: string;
      lastname: string;
      email: string;
    };
  }