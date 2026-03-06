import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvestorDashboard } from './investor-dashboard';

describe('InvestorDashboard', () => {
  let component: InvestorDashboard;
  let fixture: ComponentFixture<InvestorDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InvestorDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvestorDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
