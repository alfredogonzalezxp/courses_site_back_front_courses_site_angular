import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import axios from 'axios';
import { Observable, from } from 'rxjs';
import { map } from 'rxjs/operators';
import { User } from './types';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api'; // Adjust based on your backend routes

  constructor(@Inject(PLATFORM_ID) private platformId: Object) { }

  private getAuthHeaders() {
    let token = '';
    if (isPlatformBrowser(this.platformId)) {
      token = localStorage.getItem('accessToken') || '';
    }
    return {
      headers: {
        Authorization: `Bearer ${token}`
      }
    };
  }

  getAllUsers(): Observable<User[]> {
    return from(axios.get<User[]>(`${this.apiUrl}/users`, this.getAuthHeaders())).pipe(
      map(response => response.data)
    );
  }

  /*
  this.getAuthHeaders())).pipe
  This code snippet is part of an HTTP request chain in your 
  UserService. It combines authentication configuration 
  with RxJS reactive programming.

 : It retrieves the JWT accessToken from localStorage and wraps it in a standard HTTP header format:
json
{
  "headers": {
    "Authorization": "Bearer eyJhbGciOi..."
  }
}

from(...)
Context: Axios returns a Promise. Angular prefers 
Observables.
Action: The from() function wraps the Axios promise and 
converts it into an Observable stream.

 .pipe(...)
What it is: This is an RxJS method used to chain 
"operators" that modify the data stream.
Usage here: You are using it to attach the map operator 
immediately after the request.
typescript
.pipe(
  map(response => response.data)
)

"Make an authenticated HTTP request using my token headers, 
convert the result into a stream (Observable), and prepare 
to modify that stream (using pipe) before sending it to the 
component."
*/
  get(id: any): Observable<User> {
    return from(axios.get<User>(`${this.apiUrl}/${id}`, this.getAuthHeaders())).pipe(
      map(response => response.data)
    );
  }

  signup(user: User): Observable<User> {
    return from(axios.post<User>(`${this.apiUrl}/signup`, user)).pipe(
      map(response => response.data)
    );
  }

  update(id: any, data: User): Observable<any> {
    return from(axios.put(`${this.apiUrl}/users/${id}`, data, this.getAuthHeaders())).pipe(
      map(response => response.data)
    );
  }

  delete(id: any): Observable<any> {
    return from(axios.delete(`${this.apiUrl}/users/${id}`, this.getAuthHeaders())).pipe(
      map(response => response.data)
    );
  }
}