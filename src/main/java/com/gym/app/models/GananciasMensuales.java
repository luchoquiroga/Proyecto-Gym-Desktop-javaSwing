package com.gym.app.models;

/**
 * Respuesta de GET /dashboard/ganancias-mensuales (solo accesible para rol ADMIN).
 */
public class GananciasMensuales {
    private int anio;
    private int mes;
    private double totalGanancias;
    private long cantidadPagos;

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public double getTotalGanancias() { return totalGanancias; }
    public void setTotalGanancias(double totalGanancias) { this.totalGanancias = totalGanancias; }

    public long getCantidadPagos() { return cantidadPagos; }
    public void setCantidadPagos(long cantidadPagos) { this.cantidadPagos = cantidadPagos; }
}
