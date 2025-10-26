export interface TeamMatchStatistics {
  teamId: number;
  teamName: string;
  goals: number;
  yellowCards: number;
  redCards: number;
  substitutions: number;
  otherEvents: number;
  totalEvents: number;
}