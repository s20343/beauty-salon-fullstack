import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  protected auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef); //bugfix

  loading = false;
  hidePassword = true;
  activeTabIndex = 0;

  loginForm = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  registerForm = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(100)]],
  });

  fillDemo(role: 'admin' | 'user'): void {
    const creds =
      role === 'admin'
        ? { username: 'admin', password: 'admin123' }
        : { username: 'user', password: 'user123' };

    this.loginForm.patchValue(creds);
  }

  onLogin(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.cdr.detectChanges(); // Update UI to show loading

    const { username, password } = this.loginForm.getRawValue();

    this.auth.login({ username: username!, password: password! }).subscribe({
      next: () => {
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/salons';
        this.router.navigateByUrl(returnUrl);
      },
      error: (err) => {
        this.loading = false;
        this.cdr.detectChanges(); // <-- YOUR CDR FIX!

        // THIS IS THE MOST IMPORTANT LINE:
        alert('LOGIN FAILED! Error: ' + err.message);
        console.error('Backend Error Details:', err);
      },
      complete: () => {
        this.loading = false;
        this.cdr.detectChanges();
      },
    });
  }

  onRegister(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.cdr.detectChanges();

    const { username, password } = this.registerForm.getRawValue();

    this.auth.register({ username: username!, password: password! }).subscribe({
      next: () => {
        this.router.navigate(['/salons']);
      },
      error: (err) => {
        this.loading = false;
        this.cdr.detectChanges(); //bugfix

        alert('REGISTER FAILED! Error: ' + err.message);
        console.error('Backend Error Details:', err);
      },
      complete: () => {
        this.loading = false;
        this.cdr.detectChanges();
      },
    });
  }
}
