package com.gym.app.services;

import com.google.gson.reflect.TypeToken;
import com.gym.app.models.Cliente;

import java.lang.reflect.Type;
import java.util.List;

public class ClienteService {

    /**
     * Obtiene la lista completa de clientes desde la API
     */
    public List<Cliente> listarClientes(String orden) throws Exception {
        // Le decimos a Gson qué tipo de lista exacta esperamos recibir
        Type tipoLista = new TypeToken<List<Cliente>>(){}.getType();

        // Hacemos un GET a /clientes (el ApiClient ya inyecta el Token JWT automáticamente)
        return ApiClient.getList("/clientes?orden=" + orden , tipoLista);
    }

    /**
     * Envía un nuevo cliente por POST para guardarlo en la base de datos
     */
    public Cliente crearCliente(Cliente nuevoCliente) throws Exception {
        return ApiClient.post("/clientes", nuevoCliente, Cliente.class);
    }
}