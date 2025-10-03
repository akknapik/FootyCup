import { MatchResponse } from "./match/match.response";
import { Schedule } from "./schedule.model";

export interface ScheduleEntry {
    id: number;
    schedule: Schedule;
    type: 'MATCH' | 'BREAK';
    match: MatchResponse | null;
    startDateTime?: string;
    durationInMin: number;
}