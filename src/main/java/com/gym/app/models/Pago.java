package com.gym.app.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class Pago {

    @SerializedName("cliente_id")
    private Integer clienteId;

    @SerializedName("plan_id")
    private Integer planId;

    @SerializedName("pago_id")
    private Integer id;

    @SerializedName("cliente_nombre")
    private String clienteNombre;

    @SerializedName("cliente_apellido")
    private String clienteApellido;

    @SerializedName("cliente_dni")
    private String clienteDni;

    @SerializedName("plan_nombre")
    private String planNombre;

    @SerializedName("monto_abonado")
    private BigDecimal montoAbonado;

    @SerializedName("fecha_pago")
    private String fechaPago;

    @SerializedName("fecha_vencimiento")
    private String fechaVencimiento;

    public Pago() {}

    // --- MÉTODOS DE CONVENIENCIA (Responsabilidad del Modelo) ---

    public String getNombreCompletoCliente() {
        if (clienteNombre != null && clienteApellido != null) {
            return clienteNombre + " " + clienteApellido;
        }
        return "Desconocido";
    }

    // --- Getters y Setters estándar ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteApellido() { return clienteApellido; }
    public void setClienteApellido(String clienteApellido) { this.clienteApellido = clienteApellido; }

    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }

    public String getPlanNombre() { return planNombre; }
    public void setPlanNombre(String planNombre) { this.planNombre = planNombre; }

    public BigDecimal getMontoAbonado() { return montoAbonado; }
    public void setMontoAbonado(BigDecimal montoAbonado) { this.montoAbonado = montoAbonado; }

    public String getFechaPago() { return fechaPago; }
    public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
}
