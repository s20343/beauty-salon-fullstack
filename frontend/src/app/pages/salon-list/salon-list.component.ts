import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SalonService } from '../../service/salon.service';

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

  isLoading = false; // FIXED

  searchText = '';
  searchDistrict = '';
  minRating: number = 0;

  priceFilters: { [key: string]: boolean } = {
    CHEAP: false,
    MODERATE: false,
    EXPENSIVE: false,
    LUXURY: false,
  };

  constructor(private salonService: SalonService) {}

  ngOnInit() {
    this.loadSalons();
  }

  loadSalons() {
    this.isLoading = true;

    this.salonService.getSalons().subscribe({
      next: (data) => {
        this.salons = data;
        this.filteredSalons = data;
        this.isLoading = false;
      },
      error: () => {
        this.salons = [];
        this.filteredSalons = [];
        this.isLoading = false;
      },
    });
  }

  onSearch() {
    this.filteredSalons = this.salons.filter((s) => {
      const matchText =
        s.name.toLowerCase().includes(this.searchText.toLowerCase()) ||
        s.district.toLowerCase().includes(this.searchText.toLowerCase());

      const matchDistrict = this.searchDistrict === '' || s.district === this.searchDistrict;

      const matchRating = (s.rating || 0) >= this.minRating;

      const selectedPrices = Object.keys(this.priceFilters).filter((k) => this.priceFilters[k]);

      const matchPrice = selectedPrices.length === 0 || selectedPrices.includes(s.priceRange);

      return matchText && matchDistrict && matchRating && matchPrice;
    });
  }
}
