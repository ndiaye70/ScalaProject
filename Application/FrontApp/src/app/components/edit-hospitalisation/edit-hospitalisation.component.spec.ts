import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditHospitalisationComponent } from './edit-hospitalisation.component';

describe('EditHospitalisationComponent', () => {
  let component: EditHospitalisationComponent;
  let fixture: ComponentFixture<EditHospitalisationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditHospitalisationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditHospitalisationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
