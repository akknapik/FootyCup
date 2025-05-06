import { ScheduleEntry } from "./schedule-entry.model";
import { Tournament } from "./tournament.model";

export interface Schedule {
    id: number;
    tournament: Tournament;
    startDateTime: Date;
    entries: ScheduleEntry[];
}