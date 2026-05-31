import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { SalonService } from '../../service/salon.service';

@Component({
  selector: 'app-salon-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './salon-edit.component.html',
  styleUrls: ['./salon-edit.component.css'],
})
export class SalonEditComponent implements OnInit {
  salonId!: number;
  salonData: any = null;
  isLoading = true;
  isSaving = false;
  errorMessage = '';

  // DEBUG SWITCH
  disableFrontendValidation = false;
  backendErrors: { [key: string]: string[] } = {};

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
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.errorMessage = 'Failed to load salon for editing.';
          this.isLoading = false;
          this.cdr.detectChanges();
        },
      });
    });
  }

  saveChanges() {
    this.isSaving = true;
    this.errorMessage = '';
    this.backendErrors = {}; // Clear old errors

    this.salonService.updateSalon(this.salonId, this.salonData).subscribe({
      next: () => {
        this.router.navigate(['/salons', this.salonId]);

      },
      error: (err) => {
        this.isSaving = false;

        if (err.status === 400 && err.error && err.error.errors) {
          this.backendErrors = err.error.errors;
          this.errorMessage = 'Please fix the highlighted fields below.';
        } else {
          console.error(err);
          this.errorMessage = 'Failed to save changes. Check backend logs.';
        }

        this.cdr.detectChanges();
      },
    });
  }
}
