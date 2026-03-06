import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StocksList } from './stocks-list';

describe('StocksList', () => {
  let component: StocksList;
  let fixture: ComponentFixture<StocksList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StocksList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StocksList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
