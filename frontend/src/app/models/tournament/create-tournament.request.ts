export interface CreateTournamentRequest {
    name: string;
    startDate: string;
    endDate: string;
    location?: string | null;
}