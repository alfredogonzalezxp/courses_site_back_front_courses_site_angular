import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';

/*
 * HOW AUTH.GUARD.TS WORKS:
 *
 * A "Guard" is like a bouncer at a club. Before the Angular Router lets a user
 * enter a protected route (like /dashboard), it runs this file to check their authorization.
 */
export const authGuard: CanActivateFn = (route, state) => {
  // 1. INJECT TOOLS
  // PLATFORM_ID: Helps us check if this code is running in a browser or on a server.
  // Router: Allows us to forcibly redirect the user if they are denied access.
  const platformId = inject(PLATFORM_ID);
  const router = inject(Router);

  // 2. BROWSER CHECK
  // We MUST ensure we are running inside a web browser before attempting to read
  // 'localStorage', because servers (like Node.js) do not have a 'localStorage' object!
  if (isPlatformBrowser(platformId)) {

    // 3. CHECK FOR JWT TOKEN
    // Look inside the browser's storage to see if an 'accessToken' was saved during login.
    // The !! (double exclamation) forces the result into a strict true/false boolean.
    const isAuthenticated = !!localStorage.getItem('accessToken');

    if (isAuthenticated) {
      // Access Granted! The user is allowed to view the requested page.
      return true;
    }
  }

  // 4. ACCESS DENIED
  // If they don't have a token (or if this is running on a server), block access
  // to the requested route and immediately bounce them back to the /login page.
  return router.parseUrl('/login');
};

