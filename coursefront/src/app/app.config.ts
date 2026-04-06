import { ApplicationConfig } from '@angular/core';
import { provideRouter, withHashLocation } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';

import { routes } from './app.routes';

/*
 * HOW APP.CONFIG.TS WORKS:
 *
 * This file exports the 'appConfig' object, which acts as the global registry
 * for all application-wide services and settings in modern Standalone Angular.
 *
 * providers array:
 * - provideRouter(routes, withHashLocation()): Registers the main application routes with Hash routing enabled.
 *   This is the standard approach for hosting on platforms like GitHub Pages to avoid 404 errors.
 *
 * - provideHttpClient(withFetch()): Registers the HttpClient service globally, allowing
 *   the application to make HTTP requests (GET, POST, etc.) to your backend.
 *   'withFetch' tells Angular to use the modern browser Fetch API under the hood.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withHashLocation()), 
    provideHttpClient(withFetch())
  ]
};

