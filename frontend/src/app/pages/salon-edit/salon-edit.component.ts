import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { SalonService } from '../../service/salon.service';
import { SalonDetail, SalonRequest } from '../../model/salon.model';

@Component({
  selector: 'app-salon-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './salon-edit.component.html',
  styleUrls: ['./salon-edit.component.css'],
})
export class SalonEditComponent implements OnInit {
  salonId!: number;
  salonData: SalonDetail | null = null;
  isLoading = true;
  isSaving = false;
  errorMessage = '';
  backendErrors: Record<string, string[]> = {};

  // Set to true to bypass frontend validation and test backend errors directly
  readonly disableFrontendValidation = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private salonService: SalonService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (!id) return;

      this.salonId = Number(id);
      this.isLoading = true;

      this.salonService.getSalonById(this.salonId).subscribe({
        next: (data) => {
          this.salonData = data;
          this.isLoading = false;
          this.cdr.detectChanges(); //bug fix
        },
        error: () => {
          this.errorMessage = 'Failed to load salon for editing.';
          this.isLoading = false;
          this.cdr.detectChanges(); //bug fix
        },
      });
    });
  }

  saveChanges() {
    if (!this.salonData) return;

    this.isSaving = true;
    this.errorMessage = '';
    this.backendErrors = {};

    const payload: SalonRequest = {
      name: this.salonData.name,
      address: this.salonData.address,
      district: this.salonData.district,
      phoneNumber: this.salonData.phoneNumber,
      priceRange: this.salonData.priceRange,
      description: this.salonData.description,
    };

    this.salonService.updateSalon(this.salonId, payload).subscribe({
      next: () => {
        this.router.navigate(['/salons', this.salonId]);
      },
      error: (err) => {
        this.isSaving = false;
        if (err.status === 400 && err.error?.errors) {
          this.backendErrors = err.error.errors;
          this.errorMessage = 'Please fix the highlighted fields below.';
        } else {
          this.errorMessage = 'Failed to save changes.';
        }
        this.cdr.detectChanges(); // bug fix
      },
    });
  }
}
