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
  searchText = '';
  searchDistrict = '';

  constructor(private salonService: SalonService) {}

  ngOnInit() {
    this.salonService.getSalons().subscribe((data) => {
      this.salons = data;
      this.filteredSalons = data;
    });
  }

  onSearch() {
    this.filteredSalons = this.salons.filter((s) => {
      const matchText =
        s.name.toLowerCase().includes(this.searchText.toLowerCase()) ||
        s.district.toLowerCase().includes(this.searchText.toLowerCase());
      const matchDistrict = this.searchDistrict === '' || s.district === this.searchDistrict;
      return matchText && matchDistrict;
    });
  }
}
