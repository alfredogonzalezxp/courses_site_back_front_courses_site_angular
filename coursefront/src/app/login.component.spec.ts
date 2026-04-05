import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { of, throwError } from 'rxjs';
import { PLATFORM_ID } from '@angular/core';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: any;
  let mockRouter: any;

  beforeEach(async () => {
    // 1. Create mock objects using Vitest's vi.fn()
    mockAuthService = {
      login: vi.fn()
    };

    mockRouter = {
      navigate: vi.fn()
    };

    // 2. Configure Angular's Test Environment (TestBed)
    await TestBed.configureTestingModule({
      // LoginComponent is standalone, so we put it in imports, not declarations
      imports: [LoginComponent, ReactiveFormsModule], 
      providers: [
        FormBuilder,
        // Inject our mocks instead of the real services
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        // Safely provide the PLATFORM_ID as browser for isPlatformBrowser to work
        { provide: PLATFORM_ID, useValue: 'browser' } 
      ]
    }).compileComponents();

    // 3. Create the component instance
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    
    // Trigger initial data binding (runs ngOnInit)
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('form should be invalid when initialized empty', () => {
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should call authService.login and navigate on successful login', () => {
    // Arrange: Fill out the form with valid data
    const credentials = { email: 'test@test.com', password: 'password123' };
    component.loginForm.setValue(credentials);
    
    // 4. MOCK RETURN: Tell the mock service to return a successful Observable when called
    mockAuthService.login.mockReturnValue(of({ token: 'fake-jwt-token' }));

    // Act: Simulate form submission
    component.onSubmit();

    // Assert: Verify our mocking expectations occurred
    expect(component.loginForm.valid).toBeTruthy();
    expect(mockAuthService.login).toHaveBeenCalledWith(credentials); // Did it call the mock with the right data?
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/dashboard']); // Did it route?
  });

  it('should show an error message on failed login', () => {
    // Arrange: Fill out the form
    const credentials = { email: 'bad@test.com', password: 'wrong' };
    component.loginForm.setValue(credentials);
    
    // 4. MOCK RETURN: Tell the mock service to purposefully fail and return an Error Observable
    mockAuthService.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));

    // Act: Simulate form submission
    component.onSubmit();

    // Assert: Verify it handles failure correctly
    expect(mockAuthService.login).toHaveBeenCalledWith(credentials);
    expect(component.errorMessage).toBe('Invalid email or password. Please try again.'); // Error gets set
    expect(mockRouter.navigate).not.toHaveBeenCalled(); // We should NOT navigate
  });
});
