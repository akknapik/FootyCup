export type TacticsTokenType = 'home' | 'away' | 'opponent';

export interface TacticsToken {
  id: string;
  type: TacticsTokenType;
  label: string;
  description?: string;
  x: number;
  y: number;
  color: string;
  referenceId?: number;
}