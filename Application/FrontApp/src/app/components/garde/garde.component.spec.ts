import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GardeComponent } from './garde.component';

describe('GardeComponent', () => {
  let component: GardeComponent;
  let fixture: ComponentFixture<GardeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GardeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GardeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
