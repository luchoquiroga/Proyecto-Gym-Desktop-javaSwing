package com.gym.app.models;

import com.google.gson.annotations.SerializedName;

public class EstadisticasDTO {

    private ClientesStats clientes;
    private FinanzasStats finanzas;

    public ClientesStats getClientes() {
        return clientes;
    }

    public void setClientes(ClientesStats clientes) {
        this.clientes = clientes;
    }

    public FinanzasStats getFinanzas() {
        return finanzas;
    }

    public void setFinanzas(FinanzasStats finanzas) {
        this.finanzas = finanzas;
    }

    // --- Subclase para los datos de Clientes ---
    public static class ClientesStats {
        private int activos;
        private int inactivos;
        private int total;

        public int getActivos() {
            return activos;
        }

        public void setActivos(int activos) {
            this.activos = activos;
        }

        public int getInactivos() {
            return inactivos;
        }

        public void setInactivos(int inactivos) {
            this.inactivos = inactivos;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    // --- Subclase para los datos Financieros ---
    public static class FinanzasStats {
        @SerializedName("recaudacion_mes")
        private double recaudacionMes;

        public double getRecaudacionMes() {
            return recaudacionMes;
        }

        public void setRecaudacionMes(double recaudacionMes) {
            this.recaudacionMes = recaudacionMes;
        }
    }
}
