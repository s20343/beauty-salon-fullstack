import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

import { SalonService } from '../../service/salon.service';
import { SalonSummary, PriceRange } from '../../model/salon.model';

@Component({
  selector: 'app-salon-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './salon-list.component.html',
  styleUrls: ['./salon-list.component.css'],
})
export class SalonListComponent implements OnInit {
  // ✅ properly typed data
  salons: SalonSummary[] = [];
  filteredSalons: SalonSummary[] = [];

  isLoading = false;

  // Backend filters
  searchDistrict = '';
  searchServiceType = '';

  // Frontend filters
  searchText = '';
  minRating = 0;

  // ✅ strong typing using PriceRange union
  priceFilters: Record<PriceRange, boolean> = {
    CHEAP: false,
    MODERATE: false,
    EXPENSIVE: false,
    LUXURY: false,
  };

  constructor(
    private salonService: SalonService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.onBackendFilterChange();
  }

  onBackendFilterChange() {
    this.isLoading = true;

    this.salonService.getSalons(this.searchDistrict, this.searchServiceType).subscribe({
      next: (dataFromBackend: SalonSummary[]) => {
        this.salons = dataFromBackend;
        this.applyFrontendFilters();
        this.isLoading = false;

        // ⚠️ only needed because of earlier change detection issue
        this.cdr.detectChanges();
      },
      error: () => {
        this.salons = [];
        this.filteredSalons = [];
        this.isLoading = false;
      },
    });
  }

  applyFrontendFilters() {
    const selectedPrices = Object.entries(this.priceFilters)
      .filter(([_, isSelected]) => isSelected)
      .map(([price]) => price as PriceRange);

    this.filteredSalons = this.salons.filter((s: SalonSummary) => {
      const matchText = s.name.toLowerCase().includes(this.searchText.toLowerCase());

      const matchRating = (s.rating ?? 0) >= this.minRating;

      const matchPrice =
        selectedPrices.length === 0 || selectedPrices.includes(s.priceRange as PriceRange);

      return matchText && matchRating && matchPrice;
    });
  }
}
