package com.gym.app.models;

import com.google.gson.annotations.SerializedName;

public class Cliente {
    public enum Estado {
        activo, inactivo
    }

    private Integer id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;

    @SerializedName("fecha_inscripcion")
    private String fechaInscripcion; // Formato ISO devuelto por la API

    private Estado estado;

    public Cliente() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(String fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}