import { TestBed } from '@angular/core/testing';

import { TacticsBoardService } from './tactics-board.service';

describe('TacticsBoardService', () => {
  let service: TacticsBoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TacticsBoardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
