import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-header', // This is used in app.ts to call this component
  //and in app.ts import HeaderComponent from './header/header.component';
  //
  standalone: true,
  /*
  In Angular standalone components, the imports array tells
  the component which other modules, components, directives,
  or pipes it is allowed to use in its HTML template.

This acts as a utility belt for standard Angular features.
Including it gives you access to:

Structural Directives: Logic for your HTML, such as *ngIf
(to show/hide elements) and *ngFor (to loop through lists).
Attribute Directives: [ngClass] and [ngStyle] for changing
CSS classes and styles dynamically.
Pipes: Formatting helpers like {{ date | date }} or
{{ text | uppercase }}.

. RouterModule
This enables navigation features in your component.
Including it gives you access to:
routerLink: The Angular equivalent of an href link.
It allows you to navigate between pages (routes) without
reloading the entire browser (e.g., <a routerLink="/login">
Login</a>).

In short: You need CommonModule to write logic
(loops/conditions) in your HTML, and RouterModule to
create clickable navigation links to other parts of
your app.
*/
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html'
})

//so when i import headercomponent in a file
//i tell in this file give me authservice and router
//and in this file i can use authservice and router
export class HeaderComponent {

  constructor(public authService: AuthService, private router: Router) { }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
