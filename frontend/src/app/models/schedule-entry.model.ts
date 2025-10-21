import { MatchRef } from "./common/match-ref.model";
import { ScheduleResponse } from "./schedule/schedule.response";

export interface ScheduleEntry {
    id: number;
    schedule: ScheduleResponse;
    type: 'MATCH' | 'BREAK';
    match: MatchRef | null;
    startDateTime?: string;
    durationInMin: number;
}
