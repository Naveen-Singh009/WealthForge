import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioList } from './portfolio-list';

describe('PortfolioList', () => {
  let component: PortfolioList;
  let fixture: ComponentFixture<PortfolioList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PortfolioList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PortfolioList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
