import { Routes } from '@angular/router';
import { SalonListComponent } from './pages/salon-list/salon-list.component';
import { SalonDetailComponent } from './pages/salon-detail/salon-detail.component';
import { SalonEditComponent } from './pages/salon-edit/salon-edit.component';

export const routes: Routes = [
  { path: '', redirectTo: 'salons', pathMatch: 'full' },
  { path: 'salons', component: SalonListComponent },
  { path: 'salons/:id', component: SalonDetailComponent },
  { path: 'salons/:id/edit', component: SalonEditComponent },
];
