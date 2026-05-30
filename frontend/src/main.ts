import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app'; // <-- Updated import

bootstrapApplication(AppComponent, appConfig) // <-- Updated class name
  .catch((err) => console.error(err));
