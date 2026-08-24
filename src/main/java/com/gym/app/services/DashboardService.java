package com.gym.app.services;

import com.gym.app.models.GananciasMensuales;

public class DashboardService {

    /**
     * Ganancias del mes actual. Endpoint restringido a rol ADMIN en el backend:
     * con un usuario GERENCIA, esta llamada lanza una excepción (403).
     */
    public GananciasMensuales obtenerGananciasMensuales() throws Exception {
        return ApiClient.get("/dashboard/ganancias-mensuales", GananciasMensuales.class);
    }

    /**
     * Ganancias de un mes/año puntual. Igual que el método sin parámetros,
     * solo accesible para rol ADMIN en el backend.
     */
    public GananciasMensuales obtenerGananciasMensuales(int anio, int mes) throws Exception {
        return ApiClient.get("/dashboard/ganancias-mensuales?anio=" + anio + "&mes=" + mes, GananciasMensuales.class);
    }
}
