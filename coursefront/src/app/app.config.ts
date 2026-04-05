import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';

import { routes } from './app.routes';

/*
 * HOW APP.CONFIG.TS WORKS:
 *
 * This file exports the 'appConfig' object, which acts as the global registry
 * for all application-wide services and settings in modern Standalone Angular.
 *
 * providers array:
 * - provideRouter(routes): Registers the main application routes, allowing Angular to
 *   navigate between different components (like /login or /dashboard) based on the URL.
 *
 * - provideHttpClient(withFetch()): Registers the HttpClient service globally, allowing
 *   the application to make HTTP requests (GET, POST, etc.) to your backend.
 *   'withFetch' tells Angular to use the modern browser Fetch API under the hood.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withFetch())
  ]
};

