import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule] // Import ReactiveFormsModule and CommonModule

  /*
  ReactiveFormsModule
  his module provides the tools for building reactive forms in 
  Angular. Reactive forms are a powerful way to manage form 
  state, where you define the form's structure and validation 
  rules in your component's TypeScript code.
  
  CommonModule
   Your login.component.html template relies heavily on structural 
   directives from CommonModule to conditionally show and hide elements. Specifically, you are using *ngIf:
  html
  <!-- Used to show a general login error -->
  <div *ngIf="errorMessage" ...>
  
  <!-- Used to show validation errors for the email input -->
  <div *ngIf="loginForm.get('email')?.invalid && ..." ...>
  
  */

})

/*
FormGroup: This is a fundamental class from Angular's 
ReactiveFormsModule.

In your HTML (.html) file: You link the HTML <form> element to this 
property using the directive [formGroup]="loginForm". This 
binding allows Angular to track the values, validation status 
(is it valid or invalid?), and user interactions (has it been 
touched or changed?) for all the inputs inside the form.

<form class="space-y-6" \[formGroup\]="loginForm"

In your HTML (.html) file: You use the *ngIf="errorMessage" 
directive on a <div>. This is a conditional check.
*/
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string = '';
  shouldAnimate = false;
  /*
  Of course! That constructor is the heart of Angular's Dependency 
  Injection system. It's how your LoginComponent gets the tools it 
  needs to do its job.
  
  The constructor is a special method that runs exactly once when 
  a new instance of LoginComponent is created. Its main purpose 
  here isn't to run complex logic, but to ask for dependencies. 
  You are essentially telling Angular:
  
  "To create a LoginComponent, I will need three things: 
  a FormBuilder, an AuthService, and a Router. Please find these 
  services and provide (or 'inject') them for me."
  
  */

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    /*
    this.fb: This is the instance of the FormBuilder service that you 
    injected into the component's constructor. It's a helper service 
    that makes creating forms more concise and readable.
    
    .group({...}): This is a method on the FormBuilder that creates a 
    new FormGroup. A FormGroup is an object that tracks the value and 
    validation status of a group of related form controls. The object 
    you pass to it ({...}) defines the controls that will be in this 
    group.
    
    email: [...]: This creates a form control named email. This name 
    is what you use in your HTML template with the 
    formControlName="email"
    
    password: [...]: This creates a second form control named 
    password, which is linked to the password input field via 
    formControlName="password".
    
    This has an initial value of '' and types of validations
    */
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      // Small delay to ensure view is ready for animation
      setTimeout(() => {
        this.shouldAnimate = true;
      }, 50);
    }
  }

  /*
  The onSubmit(): void syntax is a method declaration in TypeScript.
  
  Unlike Java or C# (where you'd write: void onSubmit()), 
  in TypeScript the type comes AFTER the name with a colon (:).
  
  Also, inside a class, you don't use the 'function' keyword.
  - onSubmit(): void  <-- Correct (TypeScript)
  - void onSubmit()   <-- Error (Java/C# style)
  - function onSubmit() <-- Error (Should not use inside class)
  */
  onSubmit(): void {
    if (this.loginForm.valid) {
      /*
      if login is success then
      .subscribe({
              next: () => {
                console.log('Login successful');
                // Redirect to a dashboard or home page on success
                this.router.navigate(['/dashboard']); 
      
      /*
      Inside the .subscribe({ ... }) you usually provide two main 
      instructions:
      next
      : What to do if the login succeeds (e.g., go to the 
      dashboard).
      error
      : What to do if the login fails (e.g., show an error message).
      Without calling .subscribe(), the login request would never 
      actually be sent to the server.
      */
      /*
      Calling this.authService.login triggers a chain of events:
      1. Component calls the Service with the form values (email/password).
      2. Service sends a POST request to the Backend API via Axios.
      3. Service 'taps' into the successful response to save the JWT token 
         to LocalStorage and update the global login state.
      4. Component 'subscribes' to the result:
         - next(): Runs if login succeeds (navigates to dashboard).
         - error(): Runs if login fails (shows error message).
      */
      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          console.log('Login successful');
          // Redirect to a dashboard or home page on success
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          this.errorMessage = 'Invalid email or password. Please try again.';
          console.error('Login failed', err);
        }
      });
    }
  }
}