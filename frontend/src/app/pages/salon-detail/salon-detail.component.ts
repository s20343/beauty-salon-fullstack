import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SalonService } from '../../service/salon.service';
import { SalonDetail } from '../../model/salon.model';

@Component({
  selector: 'app-salon-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './salon-detail.component.html',
  styleUrls: ['./salon-detail.component.css'],
})
export class SalonDetailComponent implements OnInit {

  salon: SalonDetail | null = null;

  isLoading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private salonService: SalonService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const idParam = params.get('id');

      if (!idParam) {
        this.errorMessage = 'Invalid Salon ID';
        this.isLoading = false;
        return;
      }

      const id = Number(idParam);

      if (isNaN(id)) {
        this.errorMessage = 'Invalid Salon ID format';
        this.isLoading = false;
        return;
      }

      this.isLoading = true;

      this.salonService.getSalonById(id).subscribe({
        next: (data: SalonDetail) => {
          this.salon = data;
          this.isLoading = false;
          this.cdr.detectChanges(); //bug fix
        },
        error: (err: unknown) => {
          console.error(err);
          this.errorMessage = 'Failed to load salon';
          this.isLoading = false;
          this.cdr.detectChanges();
        },
      });
    });
  }

  getGoogleMapsUrl(): string {
    if (!this.salon) {
      return '#';
    }

    const searchParts: (string | null)[] = [
      this.salon.name,
      this.salon.address,
      this.salon.district,
      'Warsaw',
    ];

    const query = searchParts
      .filter((part): part is string => !!part && part.trim().length > 0)
      .join(', ');

    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(query)}`;
  }
}
