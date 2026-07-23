package com.gym.app.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class Plan {
    private Integer id;
    private String nombre;
    private BigDecimal precio;

    @SerializedName("duracion_dias")
    private Integer duracionDias;

    public Plan() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getDuracionDias() { return duracionDias; }
    public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }
}