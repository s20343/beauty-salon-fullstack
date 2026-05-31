import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SalonService } from '../../service/salon.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-salon-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './salon-list.component.html',
  styleUrls: ['./salon-list.component.css'],
})
export class SalonListComponent implements OnInit {
  salons: any[] = [];
  filteredSalons: any[] = [];
  isLoading = false;

  // Backend filters
  searchDistrict = '';
  searchServiceType = '';

  // Frontend filters
  searchText = '';
  minRating: number = 0;
  priceFilters: { [key: string]: boolean } = {
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
      next: (dataFromBackend) => {
        this.salons = dataFromBackend;
        this.applyFrontendFilters();
        this.isLoading = false;
        this.cdr.detectChanges();//bugfix
      },
      error: () => {
        this.salons = [];
        this.filteredSalons = [];
        this.isLoading = false;
      },
    });
  }

  applyFrontendFilters() {
    this.filteredSalons = this.salons.filter((s) => {
      const matchText = s.name.toLowerCase().includes(this.searchText.toLowerCase());
      const matchRating = (s.rating || 0) >= this.minRating;
      const selectedPrices = Object.keys(this.priceFilters).filter((k) => this.priceFilters[k]);
      const matchPrice = selectedPrices.length === 0 || selectedPrices.includes(s.priceRange);

      return matchText && matchRating && matchPrice;
    });
  }
}
