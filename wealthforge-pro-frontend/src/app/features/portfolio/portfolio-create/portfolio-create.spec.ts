import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioCreate } from './portfolio-create';

describe('PortfolioCreate', () => {
  let component: PortfolioCreate;
  let fixture: ComponentFixture<PortfolioCreate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PortfolioCreate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PortfolioCreate);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
