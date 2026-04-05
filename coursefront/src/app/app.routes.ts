import { Routes } from '@angular/router';
import { LoginComponent } from './login.component';
import { authGuard } from './auth.guard';

/*
 * HOW APP.ROUTES.TS WORKS:
 *
 * This file defines the navigation map for the entire application.
 * The Router uses this array to decide which Component to render inside
 * the <router-outlet> whenever the browser's URL changes.
 */
export const routes: Routes = [
    {
        // 1. EAGER LOADING: When the user goes to '/login', immediately load the LoginComponent.
        // It is imported at the top of the file, so it's always bundled with the main app code.
        path: 'login',
        component: LoginComponent
    },
    {
        /*
The () => part
This is a Pause Button. Normally, Angular tries to run all
code immediately when your website opens. This () => tells
Angular: "Wait! Stop here. Do not run the code after this
arrow until the user actually clicks the Dashboard
navigation link."

The import('...') part
This is the Delivery Truck. When the user finally clicks
the Dashboard link, the pause button is released.
The import() tells your browser: "Hey!
Reach over the internet right now
and download the dashboard.component.ts file."

The .then(m => m.DashboardComponent) part
This is the "Unpacking" step. The Delivery Truck (import)
drops off a box (the module). This line tells Angular:
"Once you open that box, pull out the specific item
named DashboardComponent and get it ready to display."

        */
        path: 'dashboard',
        loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent),

        // 3. SECURING ROUTES: Before loading '/dashboard', run the authGuard.
        // If it returns false (no token found), the user cannot enter.
        canActivate: [authGuard]// Checks Authorization-
    },
    {
        path: 'users',
        loadComponent: () => import('./user-management/user-management.component').then(m => m.UserManagementComponent),
        canActivate: [authGuard]//i send by default (route, state)
    },
    {
        // 4. DEFAULT ROUTE (Redirect): If the user goes to the exact root ('/'),
        // immediately forward them to the '/login' page.
        // 'pathMatch: full' ensures we ONLY redirect if the path is completely empty.
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    }
];

/*
Observations
so here loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent),
1. () => stop here
2.  import  = load the dashboard.component.ts when user click
3. one you open that door is ready to display.

*/
