import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditMaterielsComponent } from './edit-materiels.component';

describe('EditMaterielsComponent', () => {
  let component: EditMaterielsComponent;
  let fixture: ComponentFixture<EditMaterielsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditMaterielsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditMaterielsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
