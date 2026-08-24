package com.gym.app.services;

import com.gym.app.models.Usuario;

public class AuthService {

    // Estructura que enviamos al servidor (POST /usuarios/login)
    public static class LoginRequest {
        private String nombre;
        private String contrasena;

        public LoginRequest(String nombre, String contrasena) {
            this.nombre = nombre;
            this.contrasena = contrasena;
        }

        public String getNombre() { return nombre; }
        public String getContrasena() { return contrasena; }
    }

    // Estructura que devuelve el backend: {mensaje, token, id, nombre, rol} (plana, sin objeto "usuario" anidado)
    public static class LoginResponse {
        private String token;
        private Integer id;
        private String nombre;
        private Usuario.Rol rol;

        public String getToken() { return token; }
        public Integer getId() { return id; }
        public String getNombre() { return nombre; }
        public Usuario.Rol getRol() { return rol; }
    }

    /**
     * Autentica el usuario contra el backend y guarda el Token en ApiClient
     */
    public Usuario login(String nombre, String contrasena) throws Exception {
        LoginRequest request = new LoginRequest(nombre, contrasena);

        LoginResponse response = ApiClient.post("/usuarios/login", request, LoginResponse.class);

        if (response != null && response.getToken() != null) {
            // Guardamos el token globalmente para todas las llamadas posteriores
            ApiClient.setJwtToken(response.getToken());

            Usuario usuario = new Usuario();
            usuario.setId(response.getId());
            usuario.setNombre(response.getNombre());
            usuario.setRol(response.getRol());
            return usuario;
        } else {
            throw new Exception("Respuesta del servidor sin token de autenticación.");
        }
    }
}
