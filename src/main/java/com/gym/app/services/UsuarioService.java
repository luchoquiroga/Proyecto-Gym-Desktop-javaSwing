package com.gym.app.services;

import com.gym.app.models.Usuario;

public class UsuarioService {

    /**
     * Registra un nuevo usuario. El backend restringe este endpoint a rol ADMIN
     * (ver SecurityConfig de la API), así que solo debe invocarse con un admin logueado.
     */
    public Usuario crearUsuario(Usuario usuario) throws Exception {
        return ApiClient.post("/usuarios", usuario, Usuario.class);
    }
}
