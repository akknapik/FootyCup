export interface PlayerStatisticsResponse {
  playerId: number;
  playerName: string;
  matchesPlayed: number;
  minutesPlayed: number;
  goals: number;
  yellowCards: number;
  redCards: number;
  substitutions: number;
  otherEvents: number;
  totalEvents: number;
}