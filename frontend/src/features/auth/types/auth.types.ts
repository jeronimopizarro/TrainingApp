export interface LoginRequest {
  email: string;
  password: string;
}

// Respuesta tras login exitoso.
export interface AuthResponse {
  token: string;
}

// Datos que viajan adentro del token JWT (Claims)
export interface DecodedToken {
  sub: string;       // Email del usuario (Standard JWT Claim)
  role: string;      // Claim personalizado: 'GYM_ADMIN', etc.
  gymId: number;     // Claim personalizado: ID del gimnasio
  userName: string;  // Claim personalizado: Nombre del usuario
  exp: number;       // Fecha de expiración del token (Timestamp)
  iat: number;       // Fecha de emisión del token (Timestamp)
}

export interface UserSession {
  token: string;
  role: string;
  gymId: number;
  userName: string;
}
