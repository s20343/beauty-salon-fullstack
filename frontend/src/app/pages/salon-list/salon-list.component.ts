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

  // Search States
  searchText = '';
  searchDistrict = '';
  minRating: number = 0; // For the slider

  // Price Checkbox States
  priceFilters: { [key: string]: boolean } = {
    CHEAP: false,
    MODERATE: false,
    EXPENSIVE: false,
    LUXURY: false,
  };

  constructor(private salonService: SalonService) {}

  ngOnInit() {
    this.salonService.getSalons().subscribe((data) => {
      this.salons = data;
      this.filteredSalons = data;
    });
  }

  onSearch() {
    this.filteredSalons = this.salons.filter((s) => {
      // 1. Text Match
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
