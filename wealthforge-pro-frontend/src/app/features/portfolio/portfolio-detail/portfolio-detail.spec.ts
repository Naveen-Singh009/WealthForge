import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioDetail } from './portfolio-detail';

describe('PortfolioDetail', () => {
  let component: PortfolioDetail;
  let fixture: ComponentFixture<PortfolioDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PortfolioDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PortfolioDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
