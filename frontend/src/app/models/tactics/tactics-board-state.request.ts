import { TacticsLayer } from './tactics-layer.model';

export interface TacticsBoardState {
  layers: TacticsLayer[];
  activeLayerId: string | null;
  lastUpdated: string;
}