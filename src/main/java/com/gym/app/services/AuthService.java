package com.gym.app.services;

import com.gym.app.models.Usuario;

public class AuthService {

    // Estructura que enviamos al servidor
    public static class LoginRequest {
        private String email;
        private String pass;

        public LoginRequest(String email, String pass) {
            this.email = email;
            this.pass = pass;
        }

        public String getEmail() { return email; }
        public String getPass() { return pass; }
    }

    // Estructura que esperamos recibir del servidor Node.js
    public static class LoginResponse {
        private String token;
        private Usuario usuario;

        public String getToken() { return token; }
        public Usuario getUsuario() { return usuario; }
    }

    /**
     * Autentica el usuario contra Node.js y guarda el Token en ApiClient
     */
    public Usuario login(String email, String pass) throws Exception {
        LoginRequest request = new LoginRequest(email, pass);

        // POST a la ruta /auth/login (Ajustá el endpoint según tu API en Node)
        LoginResponse response = ApiClient.post("/login", request, LoginResponse.class);

        if (response != null && response.getToken() != null) {
            // Guardamos el token globalmente para todas las llamadas posteriores
            ApiClient.setJwtToken(response.getToken());
            return response.getUsuario();
        } else {
            throw new Exception("Respuesta del servidor sin token de autenticación.");
        }
    }
}