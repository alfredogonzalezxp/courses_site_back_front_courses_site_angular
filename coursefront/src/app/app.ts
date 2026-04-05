import { Component } from '@angular/core';

// 1. RouterOutlet allows Angular to dynamically swap out components based on the URL
import { RouterOutlet } from '@angular/router';

// 2. We import our custom navigation bar so it can be present on all pages
import { HeaderComponent } from './header/header.component';

/*
 * HOW APP.TS WORKS (The Root Component):
 *
 * This is the parent shell of the entire application. When 'main.ts' bootstraps the app,
 * it looks for the <app-root> selector inside 'index.html' and mounts this component there.
 *
 * @Component Decorator:
 * - standalone: true -> Means this component manages its own dependencies without an NgModule.
 * - imports -> Lists the dependencies required for the template to work (Header & Router).
 * - template -> Defines the HTML structure for the root of the application.
 *
 * The Template Layout:
 * 1. <app-header>: Displays the persistent navigation bar at the top of every screen.
 * 2. <router-outlet>: Acts as a dynamic placeholder. When the URL changes (e.g., /login or /dashboard),
 *    the Router injects the requested page component directly into this spot.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  template: `<app-header></app-header><router-outlet></router-outlet>`,
})
export class AppComponent {
  // This is a local state property that can be displayed using {{ title }} in a template
  title = 'coursefront';
}

