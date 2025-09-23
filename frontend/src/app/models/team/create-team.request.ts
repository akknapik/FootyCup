export interface CreateTeamRequest {
    name: string;
    country?: string | null;
    coachEmail: string;
}