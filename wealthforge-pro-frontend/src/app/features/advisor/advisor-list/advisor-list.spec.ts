import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdvisorList } from './advisor-list';

describe('AdvisorList', () => {
  let component: AdvisorList;
  let fixture: ComponentFixture<AdvisorList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdvisorList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdvisorList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
