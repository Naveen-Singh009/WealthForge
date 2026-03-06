import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdvisorDashboard } from './advisor-dashboard';

describe('AdvisorDashboard', () => {
  let component: AdvisorDashboard;
  let fixture: ComponentFixture<AdvisorDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdvisorDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdvisorDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
