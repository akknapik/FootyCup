import { Match } from "./match.model";
import { Schedule } from "./schedule.model";

export interface ScheduleEntry {
    id: number;
    schedule: Schedule;
    type: 'MATCH' | 'BREAK';
    match: Match | null;
    startDateTime?: string;
    durationInMin: number;
}