package com.gym.app.services;

import com.google.gson.reflect.TypeToken;
import com.gym.app.models.Cliente;

import java.lang.reflect.Type;
import java.util.List;

public class ClienteService {

    /**
     * Obtiene la lista completa de clientes desde la API.
     * El backend no soporta ordenamiento por query param; el orden se aplica en el cliente Swing.
     */
    public List<Cliente> listarClientes() throws Exception {
        Type tipoLista = new TypeToken<List<Cliente>>(){}.getType();
        return ApiClient.getList("/clientes", tipoLista);
    }

    /**
     * Envía un nuevo cliente por POST para guardarlo en la base de datos
     */
    public Cliente crearCliente(Cliente nuevoCliente) throws Exception {
        return ApiClient.post("/clientes", nuevoCliente, Cliente.class);
    }

    /**
     * Actualiza nombre, apellido y teléfono de un cliente existente.
     * El backend ignora el campo estado en este endpoint (tiene su propio flujo de cambio de estado).
     */
    public Cliente actualizarCliente(Integer id, Cliente cliente) throws Exception {
        return ApiClient.put("/clientes/" + id, cliente, Cliente.class);
    }

    /**
     * Da de baja a un cliente (baja lógica: el backend lo pasa a estado INACTIVO).
     */
    public void darDeBaja(Integer id) throws Exception {
        ApiClient.delete("/clientes/" + id);
    }
}
