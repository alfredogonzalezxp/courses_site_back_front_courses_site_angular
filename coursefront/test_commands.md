# Test Commands Reference

This project uses **Vitest** (v4.0.15) integrated with the official **Angular CLI** test builder.

> [!IMPORTANT]
> **Purpose**: Standardize the testing pipeline.
> Always use `npm test` instead of `npx vitest`. The Angular CLI handles the compilation of HTML templates and CSS styles which vanilla Vitest cannot do alone.

---

## Running Tests

### Run All Tests (Single Pass)

```bash
npm test -- --watch=false
```

- **Purpose**: CI/CD and Final Verification.
- **Why**: Use this before committing code or in automated pipelines (GitHub Actions). It runs all tests exactly once and provides a final "Pass/Fail" result.

### Run Tests in Watch Mode (Recommended)

```bash
npm test -- --watch
```

- **Purpose**: Active Development.
- **Why**: Keeps the test runner alive. As you save files, it automatically re-runs only the tests affected by your changes, providing instant feedback while you code.

### Run a Specific Test File

```bash
npm test -- --include src/app/login.component.spec.ts
```

- **Purpose**: Targeted Debugging.
- **Why**: When you are working on a specific component, use this to ignore all other tests. It makes the execution much faster and keeps the console clean.

### Run Tests Matching a Pattern

```bash
npm test -- --filter "should login"
```

- **Purpose**: Narrow Focusing.
- **Why**: Use this to run only those specific test cases (inside any file) that match the text. Great for fixing a single failing `it()` block.

---

## Test Output Options

### Verbose Output (Show All Test Names)

```bash
npm test -- --reporters=verbose
```

- **Purpose**: Transparency.
- **Why**: By default, Vitest might summarize results. Verbose mode lists every single test name and its status (PASSED/FAILED) as they happen.

### Show Code Coverage

```bash
npm test -- --coverage
```

- **Purpose**: Quality Assurance.
- **Why**: Analyzes which lines of your source code are actually executed by tests. Use this to find "dead zones" or logic that hasn't been tested yet.

---

## Technical Fixes (DevOps)

The following mandatory dependencies were added to fix previous test runner crashes:

- **`zone.js`**: Required for Angular reactivity in all environments.
- **`@angular/platform-browser-dynamic`**: Required for `TestBed` to compile components JIT.
- **`src/init-testbed.ts`**: Automatically initializes the Angular testing platform.

---

## Existing Test Files

| File                              | What It Tests                                                 |
| --------------------------------- | ------------------------------------------------------------- |
| `src/app/app.spec.ts`             | Root AppComponent: Creation and Title checks.                 |
| `src/app/login.component.spec.ts` | LoginComponent: login success, login failure, and navigation. |

---

## Mocking Quick Reference

### Create a Mock Function

```typescript
const mockFn = vi.fn();
```

### Mock an Observable Return

```typescript
import { of, throwError } from 'rxjs';

// Success
mockLogin.mockReturnValue(of({ accessToken: 'fake-token' }));

// Failure
mockLogin.mockReturnValue(throwError(() => new Error('Invalid credentials')));
```

### Verify a Mock Was Called

```typescript
expect(mockFn).toHaveBeenCalled();
expect(mockFn).toHaveBeenCalledWith('expectedArg');
```
