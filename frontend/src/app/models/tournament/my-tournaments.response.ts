import { TournamentItemResponse } from './tournament-item.response';

export interface MyTournamentsResponse {
  organized: TournamentItemResponse[];
  refereeing: TournamentItemResponse[];
  coaching: TournamentItemResponse[];
  observing: TournamentItemResponse[];
}