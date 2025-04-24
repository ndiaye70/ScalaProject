import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LitFormComponent } from './lit-form.component';

describe('LitFormComponent', () => {
  let component: LitFormComponent;
  let fixture: ComponentFixture<LitFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LitFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LitFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
