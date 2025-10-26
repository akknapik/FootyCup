export type MatchEventType = 'GOAL' | 'YELLOW_CARD' | 'RED_CARD' | 'SUBSTITUTION' | 'OTHER';

export interface CreateMatchEventRequest {
  playerId?: number | null;
  secondaryPlayerId?: number | null;
  teamId: number;
  eventType: MatchEventType;
  minute: number;
  description?: string | null;
}