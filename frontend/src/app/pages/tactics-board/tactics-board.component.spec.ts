import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TacticsBoardComponent } from './tactics-board.component';

describe('TacticsBoardComponent', () => {
  let component: TacticsBoardComponent;
  let fixture: ComponentFixture<TacticsBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TacticsBoardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TacticsBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});