import { TacticsToken } from './tactics-token.model';

export interface TacticsLayer {
  id: string;
  name: string;
  tokens: TacticsToken[];
  notes?: string;
  createdAt: string;
  updatedAt: string;
}