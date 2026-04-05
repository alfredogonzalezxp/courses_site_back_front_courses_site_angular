# Frontend Architecture & Application Flow

This document details the complete end-to-end flow of the **CourseFront** Angular application. It describes how the application spins up, manages user state, requests data securely, and dynamically renders the UI.

---

## 1. Bootstrap: From Load to `<app-root>`

The application initializes through the standard Angular modern Standalone bootstrap sequence:

1. **`index.html`**: The HTML foundation loaded by the browser. It houses the `<app-root></app-root>` tag.
2. **`main.ts`**: The execution entry point. It calls `bootstrapApplication(AppComponent, appConfig)`.
3. **`app.config.ts`**: Central configuration registry. It injects the global router rules (`provideRouter(routes)`), HTTP configuration (`provideHttpClient(withFetch())`), and async optimizations.
4. **`AppComponent` (`app.ts`)**: The root shell of the SPA (Single Page Application). It is the code behind `<app-root>` and renders:
   - `<app-header>`: The global navigation bar.
   - `<router-outlet>`: The dynamic outlet where Angular swaps out the actual "pages" depending on the URL.

---

## 2. Global Routing & The Navigation Guard

Angular intercepts the browser URL and maps it to specific components using **`app.routes.ts`**:

- **Default (`''`)**: Redirects automatically to the `/login` page.
- **`/login`**: Loads the stand-alone `LoginComponent` where users input credentials.
- **Protected Routes (`/dashboard`, `/users`)**: These routes are lazy-loaded (improving performance by keeping initial bundle size smaller) but importantly, they are protected by `AuthGuard` (`auth.guard.ts`).

### The `AuthGuard` Flow:

Before the Router allows access to `/dashboard`, the `AuthGuard` intervenes:

1. It queries the browser's `localStorage` for the `accessToken`.
2. **Authorized**: If the token exists, it returns `true` and the component renders.
3. **Rejected**: If the token is missing, it cancels the navigation and forcibly redirects the user back to `/login`.

---

## 3. The Authentication Lifecycle

Authentication is coordinated strictly through **`AuthService.ts`**:

1. **User Action**: The user inputs credentials via the Reactive Form in `LoginComponent` and clicks "Sign in".
2. **API Call**: `LoginComponent` invokes `this.authService.login(credentials)`. The service executes an Axios `POST` to the Spring Boot backend (`/api/signin`).
3. **Token Management**: On a `200 OK` response, the service extracts the JWT `accessToken` from the backend and persists it directly into `localStorage`.
4. **Reactivity**: Crucially, upon success, the service emits a `true` signal through its `isLoggedInSubject` (`BehaviorSubject`).
5. **Redirection**: The login component's `.subscribe(next)` triggers, redirecting the user to `/dashboard` now that they are authenticated.

---

## 4. Subscriptions & Component Dynamics

Because Angular uses Reactive Programming (RxJS), the system reacts dynamically to state changes without hard refreshes.

### The Header (`HeaderComponent`):

The Header visually changes based on authentication logic.

- It subscribes directly to `authService.isLoggedIn$`.
- **Logged out:** Shows "Sign In" link.
- **Logged in:** Shows "Dashboard", "Users", and "Sign Out" links.

When a user clicks "Sign Out", the `AuthService.logout()` deletes the `localStorage` payload, emits a `false` signal (updating the Header instantly), and the Router navigates to `/login`.

---

## 5. Fetching Secure Data

When the user enters a protected page (like the User Management table), the frontend needs to retrieve data securely.

### The Data Layer Flow (`user.service.ts`):

1. **The Request**: A component calls a method, e.g., `UserService.getUsers()`.
2. **Credential Linking**: Before finalizing the Axios configuration, the service calls its helper method `getAuthHeaders()`.
3. **Retrieving Token**: It pulls the JWT from `localStorage`.
4. **Header Injection**: It attaches `Authorization: Bearer <token>` to the HTTP headers.
5. **RxJS Wrapping**: The service wraps the Axios Promise inside `from(...)` to return an Observable that fits perfectly with Angular's template binding flow.

---

## E2E Visual Summary Flow

1. **User opens App** ➡️ `URL: /` ➡️ Redirects to `/login`
2. **Fills Out Form** ➡️ Reactive Forms validate the specific inputs.
3. **Submits Form** ➡️ `AuthService` sends POST to the backend with email/password.
4. **Backend Approves** ➡️ JWT Token is saved strictly in `localStorage`.
5. **Global State Updates** ➡️ `Header` links magically appear via Subscription.
6. **Navigation** ➡️ User is routed to `/dashboard`.
7. **Interacting with DB** ➡️ User goes to `/users`. `UserService` grabs the cached Token, attaches it to the HTTP Headers, and requests user data. Complete list is displayed on the screen.
