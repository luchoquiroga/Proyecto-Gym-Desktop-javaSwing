package com.gym.app.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class Pago {
    private Integer id;

    @SerializedName("cliente_id")
    private Integer clienteId;

    @SerializedName("plan_id")
    private Integer planId;

    @SerializedName("monto_abonado")
    private BigDecimal montoAbonado;

    @SerializedName("fecha_pago")
    private String fechaPago;

    @SerializedName("fecha_vencimiento")
    private String fechaVencimiento;

    // Campos opcionales si la API de Node devuelve los objetos anidados con JOIN
    private Cliente cliente;
    private Plan plan;

    public Pago() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public BigDecimal getMontoAbonado() { return montoAbonado; }
    public void setMontoAbonado(BigDecimal montoAbonado) { this.montoAbonado = montoAbonado; }

    public String getFechaPago() { return fechaPago; }
    public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
}
