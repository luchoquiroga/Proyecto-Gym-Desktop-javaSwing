package com.gym.app.models;

import java.math.BigDecimal;

public class Plan {
    private Integer id;
    private String nombre;
    private BigDecimal precio;
    private Integer duracion;

    public Plan() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    @Override
    public String toString() {
        return String.format("%s - $%,.2f (%d días)", nombre, precio, duracion);
    }
}
