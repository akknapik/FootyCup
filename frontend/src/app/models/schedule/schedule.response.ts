import { ScheduleEntryResponse } from "./schedule-entry.response";

export interface ScheduleResponse {
    id: number;
    startDateTime: string;
    entries: ScheduleEntryResponse[];
}