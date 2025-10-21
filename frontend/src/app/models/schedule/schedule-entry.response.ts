import { MatchRef } from "../common/match-ref.model";

export interface ScheduleEntryResponse {
    id: number;
    type: 'MATCH' | 'BREAK';
    startDateTime: string;
    durationInMin: number;
    match: MatchRef | null;
}