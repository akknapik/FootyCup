export interface TeamStatisticsResponse {
  teamId: number;
  teamName: string;
  matchesPlayed: number;
  minutesPlayed: number;
  goals: number;
  yellowCards: number;
  redCards: number;
  substitutions: number;
  otherEvents: number;
  totalEvents: number;
}