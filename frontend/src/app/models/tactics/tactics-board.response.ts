import { TacticsLayer } from './tactics-layer.model';

export interface TacticsBoardResponse {
  id: number;
  matchId: number;
  layers: TacticsLayer[];
  activeLayerId: string | null;
  lastUpdated: string;
  savedAt: string;
}