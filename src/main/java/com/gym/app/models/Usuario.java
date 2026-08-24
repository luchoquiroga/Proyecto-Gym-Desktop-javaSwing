package com.gym.app.models;

public class Usuario {
    public enum Rol {
        ADMIN, GERENCIA
    }
    private Integer id;
    private String nombre;
    private String contrasena;
    private Rol rol;

    public Usuario() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
