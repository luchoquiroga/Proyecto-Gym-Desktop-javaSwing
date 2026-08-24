package com.gym.app.models;

import java.math.BigDecimal;

/**
 * Cuerpo que espera el backend en POST /pagos (com.gimnasio.api.dto.PagoRequest):
 * clienteId y planId en vez de los objetos completos, y fechaPago opcional
 * (si se omite, el backend usa la fecha de hoy).
 */
public class PagoRequest {
    private Integer clienteId;
    private Integer planId;
    private BigDecimal montoAbonado;
    private String fechaPago;

    public PagoRequest() {}

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public BigDecimal getMontoAbonado() { return montoAbonado; }
    public void setMontoAbonado(BigDecimal montoAbonado) { this.montoAbonado = montoAbonado; }

    public String getFechaPago() { return fechaPago; }
    public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }
}
