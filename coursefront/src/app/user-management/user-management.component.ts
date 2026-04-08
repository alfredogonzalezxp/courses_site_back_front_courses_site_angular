import { Component, OnInit, NgZone, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../user.service';
import { User } from '../../types';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  selectedUser: User | null = null;
  isLoading = false;

  constructor(
    private userService: UserService,
    private ngZone: NgZone,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }



  loadUsers(): void {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.users = data;
          this.isLoading = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          console.error('Error loading users', err);
          this.isLoading = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  deleteUser(id?: number): void {
    if (id && confirm('Are you sure you want to delete this user?')) {
      this.userService.delete(id).subscribe({
        next: () => {
          this.ngZone.run(() => {
            this.loadUsers();
          });
        },
        error: (err) => console.error('Error deleting user', err)
      });
    }
  }

  editUser(user: User): void {
    this.selectedUser = { ...user, password: '' };
  }

  addUser(): void {
    this.selectedUser = { rol: 'user' } as User;
  }

  saveUser(): void {
    if (this.selectedUser) {
      if (this.selectedUser.id) {
        this.userService.update(this.selectedUser.id, this.selectedUser).subscribe({
          next: () => {
            this.ngZone.run(() => {
              this.selectedUser = null;
              this.loadUsers();
              this.cdr.detectChanges();
            });
          },
          error: (err) => console.error('Error updating user', err)
        });
      } else {
        this.userService.signup(this.selectedUser).subscribe({
          next: () => {
            this.ngZone.run(() => {
              this.selectedUser = null;
              this.loadUsers();
            });
          },
          error: (err) => console.error('Error creating user', err)
        });
      }
    }
  }

  cancelEdit(): void {
    this.selectedUser = null;
  }
}