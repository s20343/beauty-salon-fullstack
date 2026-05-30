import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SalonService } from '../../service/salon.service';

@Component({
  selector: 'app-salon-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './salon-detail.component.html',
  styleUrls: ['./salon-detail.component.css'],
})
export class SalonDetailComponent implements OnInit {
  salon: any = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private salonService: SalonService,
    private cdr: ChangeDetectorRef, //change detection bug fix
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');

      if (!id) {
        this.errorMessage = 'Invalid Salon ID';
        this.isLoading = false;
        return;
      }
      this.isLoading = true;

      this.salonService.getSalonById(Number(id)).subscribe({
        next: (data) => {
          this.salon = data;
          this.isLoading = false;
          this.cdr.detectChanges();//bug fix
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = 'Failed to load salon';
          this.isLoading = false;
          this.cdr.detectChanges(); //bug fix
        },
      });
    });
  }
}
