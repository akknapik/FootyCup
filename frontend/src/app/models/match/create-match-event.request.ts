export interface CreateMatchEventRequest {
  playerId?: number | null;
  teamId: number;
  eventType: 'GOAL' | 'YELLOW_CARD' | 'RED_CARD';
  minute: number;
}