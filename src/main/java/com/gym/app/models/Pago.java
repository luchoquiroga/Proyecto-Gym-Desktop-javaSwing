package com.gym.app.models;

import java.math.BigDecimal;

public class Pago {

    private Integer id;
    private Cliente cliente;
    private Plan plan;
    private BigDecimal montoAbonado;
    private String fechaPago;
    private String fechaVencimiento;

    public Pago() {}

    // --- MÉTODOS DE CONVENIENCIA (Responsabilidad del Modelo) ---

    public String getNombreCompletoCliente() {
        return cliente != null ? cliente.getNombreCompleto() : "Desconocido";
    }

    public String getPlanNombre() {
        return plan != null ? plan.getNombre() : "-";
    }

    // --- Getters y Setters estándar ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }

    public BigDecimal getMontoAbonado() { return montoAbonado; }
    public void setMontoAbonado(BigDecimal montoAbonado) { this.montoAbonado = montoAbonado; }

    public String getFechaPago() { return fechaPago; }
    public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
