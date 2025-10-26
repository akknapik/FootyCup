import { TeamMatchStatistics } from "./team-match-statistics.model";

export interface MatchStatisticsResponse {
  homeTeam: TeamMatchStatistics | null;
  awayTeam: TeamMatchStatistics | null;
}