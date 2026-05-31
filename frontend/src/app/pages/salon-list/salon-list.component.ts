import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SalonService } from '../../service/salon.service';
import { SalonSummary } from '../../model/salon.model';

@Component({
  selector: 'app-salon-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './salon-list.component.html',
  styleUrls: ['./salon-list.component.css'],
})
export class SalonListComponent implements OnInit {
  salons: SalonSummary[] = [];
  filteredSalons: SalonSummary[] = [];
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

  // Frontend sort
  sortBy: 'none' | 'rating' | 'reviewCount' = 'none';

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
    const filtered = this.salons.filter((s) => {
      const matchText = s.name.toLowerCase().includes(this.searchText.toLowerCase());
      const matchRating = (s.rating || 0) >= this.minRating;
      const selectedPrices = Object.keys(this.priceFilters).filter((k) => this.priceFilters[k]);
      const matchPrice = selectedPrices.length === 0 || selectedPrices.includes(s.priceRange ?? '');
      return matchText && matchRating && matchPrice;
    });

    this.filteredSalons = this.applySort(filtered);
  }

  private applySort(salons: SalonSummary[]): SalonSummary[] {
    if (this.sortBy === 'rating') {
      return [...salons].sort((a, b) => (b.rating ?? 0) - (a.rating ?? 0));
    }
    if (this.sortBy === 'reviewCount') {
      return [...salons].sort((a, b) => (b.reviewCount ?? 0) - (a.reviewCount ?? 0));
    }
    return salons;
  }
}
