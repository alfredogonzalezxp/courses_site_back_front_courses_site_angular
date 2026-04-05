import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable } from 'rxjs';
import { User } from './types';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api'; // Adjust based on your backend routes

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient
  ) { }

  private getAuthHeaders() {
    let token = '';
    if (isPlatformBrowser(this.platformId)) {
      token = localStorage.getItem('accessToken') || '';
    }
    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    };
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/users`, this.getAuthHeaders());
  }

  /*
  Notes removed for brevity as HttpClient natively handles Observable returning.
  */
  get(id: any): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`, this.getAuthHeaders());
  }

  signup(user: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/signup`, user);
  }

  update(id: any, data: User): Observable<any> {
    return this.http.put(`${this.apiUrl}/users/${id}`, data, this.getAuthHeaders());
  }

  delete(id: any): Observable<any> {
    return this.http.delete(`${this.apiUrl}/users/${id}`, this.getAuthHeaders());
  }
}