package com.gym.app.services;

import com.google.gson.reflect.TypeToken;
import com.gym.app.models.Pago;

import java.lang.reflect.Type;
import java.util.List;

public class PagoService {

    public List<Pago> listarPagos() throws Exception {
        Type tipoLista = new TypeToken<List<Pago>>(){}.getType();
        return ApiClient.getList("/pagos", tipoLista);
    }

    public Pago registrarPago(Pago nuevoPago) throws Exception {
        return ApiClient.post("/pagos", nuevoPago, Pago.class);
    }
}