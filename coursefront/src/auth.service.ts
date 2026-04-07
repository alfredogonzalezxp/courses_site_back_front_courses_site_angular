import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginRequest, SignupRequest, JwtAuthenticationResponse } from './types';

/*
 * HOW AUTH.SERVICE.TS WORKS (The Global Auth Store):
 *
 * This service acts like Pinia or AuthContext. Because of the "@Injectable" decorator
 * sitting below, Angular knows this is a "Singleton" — meaning it creates exactly ONE
 * copy of this file and shares its data with every component in your app.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://springboot-backend-env.eba-vykmuaq8.us-east-2.elasticbeanstalk.com/api';

  // 1. REACTIVE STATE (The Variables)
  // BehaviorSubject: Holds the exact CURRENT value (are they logged in or not?)
  // It actively broadcasts any changes to anyone who is listening.
//  a okey then you use Bej¿haviour subject abd Observable everytime you need to change in reactvive way

  //
  // WHY A BEHAVIORSUBJECT? (The "Memory" feature)
  // A regular variable forgets its value when you change pages. A regular RxJS "Subject"
  // fires an event once and then goes silent.
  // A "BehaviorSubject" acts like a Sticky Note on your fridge. It permanently holds the
  // last value it received. If a user navigates to a new page, the app just looks at the
  // Sticky Note and instantly knows they are authenticated without asking them to login again!
  //
  // WHY THE COLON (:)?
  // The colon strictly limits this variable so it can ONLY ever hold a BehaviorSubject that
  // returns true/false (boolean). If we try to put a string inside it later, the app crashes.
  private isLoggedInSubject: BehaviorSubject<boolean>;

  // isLoggedIn$: The public, read-only stream.
  // "Read-only" means only the AuthService can change the value (via BehaviorSubject).
  // Components like the Header can watch the value change many times (true/false/true...),
  // but they can NEVER be the ones to change it. They can only listen.
  // The $ sign is an Angular naming convention meaning "this is a reactive Observable stream".
  public isLoggedIn$: Observable<boolean>;
  public currentUser: any = null;

  // 2. INITIAL SETUP (The Constructor)
  // The 'constructor' runs the exact moment Angular creates this Service.
  // - @Inject(PLATFORM_ID): We ask Angular to hand us a tool that answers one question:
  //   "Is this code running securely in a Browser (Chrome) or on a Server (Node.JS)?"
  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient
  ) {

    // a. We start up the Two-Way Walkie Talkie (BehaviorSubject).
    // What is its starting value? We run hasToken() to check localStorage.
    // If a token from yesterday is found, it instantly broadcasts: TRUE!
    this.isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());

    // b. We lock the Walkie Talkie into a One-Way Read-Only Radio (Observable).
    // WHY .asObservable()? This method creates a "clone" of the BehaviorSubject but rips
    // off its .next() method so it becomes strictly read-only. We MUST use .asObservable()
    // instead of creating a "new Observable()" because this permanently hard-wires the
    // read-only radio so it is always listening to our specific BehaviorSubject's frequency.
    this.isLoggedIn$ = this.isLoggedInSubject.asObservable();
  }

  // 3. SERVER-SAFE BROWSER CHECK
  // LocalStorage ONLY exists inside web browsers, not on servers (like Node.js).
  // We MUST check if we are in a browser first, otherwise the app will crash in SSR.
  private hasToken(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!localStorage.getItem('accessToken');
    }
    return false;
  }

  // 4. THE LOGIN FLOW
/*
Yes, the component that calls login() cannot change the
response data (it is read-only).
But the main reason we use Observable here is because the
backend response is asynchronous — it takes time to arrive,
and the Observable lets us wait for it cleanly!

*/
  login(credentials: LoginRequest): Observable<JwtAuthenticationResponse> {

    // HttpClient automatically parses the JSON response and returns it as an Observable, so we don't need 'from' or 'map'.
    return this.http.post<JwtAuthenticationResponse>(`${this.apiUrl}/signin`, credentials).pipe(

      // tap: Allows us to peek at the data and do a side-effect (like saving to storage)
      // without modifying the data passing through the stream.
      tap(response => {
        if (response.accessToken) {
          if (isPlatformBrowser(this.platformId)) {
            // Save their credentials so they stay logged in if they refresh the tab
            localStorage.setItem('accessToken', response.accessToken);
            localStorage.setItem('userName', credentials.email);
            this.currentUser = { nombre: credentials.email };
          }
          // .next(true) does two things at once:
          // 1. Updates the Sticky Note value from false to true.
          // 2. Instantly broadcasts TRUE to every component subscribed to isLoggedIn$
          //    (like the Header), causing them to redraw on the screen immediately.
          this.isLoggedInSubject.next(true);
        }
      })
    );
  }

  signup(user: SignupRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, user);
  }

  // 5. THE LOGOUT FLOW
  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      // Destroy the saved tokens from the browser
      localStorage.removeItem('accessToken');
      localStorage.removeItem('userName');
    }
    // .next(false) flips the Sticky Note back to false and broadcasts
    // to every listener: "User logged out!" The Header hides the Sign Out button.
    this.isLoggedInSubject.next(false);
  }
}
