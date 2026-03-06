import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuySell } from './buy-sell';

describe('BuySell', () => {
  let component: BuySell;
  let fixture: ComponentFixture<BuySell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BuySell]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BuySell);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
