# CustomUserDetails Documentation

This document explains the features, functions, and methods provided by the `CustomUserDetails` class in your Spring Security configuration.

The `UserDetails` interface acts as the "bridge" between your custom `User` database model and Spring Security's internal authentication system. Because Spring Security doesn't know your database schema, you must implement this interface to map your data over.

---

## 1. Core Authentication Methods (Interface overrides)

These methods provide Spring Security with the the minimum information required to log a user in.

| Method | Return Type | Description |
| :--- | :--- | :--- |
| `getUsername()` | `String` | **Crucial:** Even though it's called "getUsername", your code maps this to `user.getEmail()`. This configures Spring to use emails for authentication instead of standard usernames. |
| `getPassword()` | `String` | Returns the hashed password directly from your `User` database object. Spring compares this hash against the password typed in the login form. |

---

## 2. Authorization Method

This method determines what the user is allowed to do after logging in.

| Method | Return Type | Description |
| :--- | :--- | :--- |
| `getAuthorities()` | `Collection<? extends GrantedAuthority>` | Returns the roles or privileges for the user. Your code takes the role from the DB, prefixes it with `"ROL_"`, and wraps it in a `SimpleGrantedAuthority`. Example: `"ROL_ADMIN"` |

---

## 3. Account Status Checks

Spring Security automatically runs these boolean checks during login. If **any** of them return `false`, the login is blocked. Currently, an active application logic has not been implemented for these, so they are all set to return `true`.

| Method | Return Type | Description |
| :--- | :--- | :--- |
| `isEnabled()` | `boolean` | Checks if the account is active. Could be used for email verification workflows. (Returns `true`) |
| `isAccountNonLocked()` | `boolean` | Checks if the account has been suspended or locked (e.g., after 5 failed login attempts). (Returns `true`) |
| `isAccountNonExpired()` | `boolean` | Used to automatically deactivate accounts on a certain date (like an expired subscription). (Returns `true`) |
| `isCredentialsNonExpired()` | `boolean` | Used to enforce policies like "passwords must be changed every 90 days". (Returns `true`) |

---

## 4. Custom Features (Specific to your implementation)

Because `CustomUserDetails` wraps your actual `User` database object, you added extra getters to easily access custom data directly from the security context without needing to hit the database again.

| Method | Return Type | Description |
| :--- | :--- | :--- |
| `getNombre()` | `String` | Returns the user's name representing the `nombre` field from the DB. |
| `getId()` | `Long` | Returns the user's primary database ID representing the `id` field from the DB. |
