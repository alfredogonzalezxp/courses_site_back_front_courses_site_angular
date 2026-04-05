export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignupRequest {
  nombre: string;
  email: string;
  password: string;
  rol: string;
}

export interface JwtAuthenticationResponse {
  accessToken: string;
}

export interface User {
/*
This line id?: number; means if id have value give the 
number type but if not dobt give any value.
*/
  id?: number;
  nombre: string;
  email: string;
  password?: string;
  rol: string;
}