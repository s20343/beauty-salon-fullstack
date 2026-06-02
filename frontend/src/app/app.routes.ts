import { Routes } from '@angular/router';
import { SalonListComponent } from './pages/salon-list/salon-list.component';
import { SalonDetailComponent } from './pages/salon-detail/salon-detail.component';
import { SalonEditComponent } from './pages/salon-edit/salon-edit.component';

import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'salons',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/auth/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'salons',
    loadComponent: () =>
      import('./pages/salon-list/salon-list.component').then((m) => m.SalonListComponent),
  },
  {
    path: 'salons/:id',
    loadComponent: () =>
      import('./pages/salon-detail/salon-detail.component').then(
        (m) => m.SalonDetailComponent,
      ),
  },
  {
    path: 'salons/:id/edit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/salon-edit/salon-edit.component').then((m) => m.SalonEditComponent),
  },
  {
    path: '**',
    redirectTo: 'salons',
  },
];
