import { UserRef } from "../common/user-ref.model";

export interface TournamentItemResponse {
    id: number;
    name: string;
    startDate: string;
    endDate: string;
    location? : string | null;
    status: 'UPCOMING' | 'ONGOING' | 'FINISHED';
    system?: 'GROUP' | 'BRACKET' | 'MIXED';
    organizer: UserRef;
    publicVisible: boolean;
    followed: boolean;
}