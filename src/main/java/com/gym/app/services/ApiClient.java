package com.gym.app.services;

import com.google.gson.Gson;
import com.gym.app.config.ApiConfig;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(ApiConfig.TIMEOUT_SECONDS))
            .build();

    private static final Gson gson = new Gson();

    // Aquí guardaremos el Token JWT al hacer Login para usarlo en todas las peticiones
    private static String jwtToken = null;

    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    public static String getJwtToken() {
        return jwtToken;
    }

    // ==========================================
    // MÉTODOS HTTP GENÉRICOS
    // ==========================================

    /**
     * Realiza una petición GET para obtener un único Objeto
     */
    public static <T> T get(String endpoint, Class<T> claseRespuesta) throws Exception {
        HttpRequest.Builder builder = crearRequestBuilder(endpoint)
                .GET();

        return ejecutarPeticion(builder.build(), claseRespuesta);
    }

    /**
     * Realiza una petición GET para obtener una Lista de Objetos
     * Ejemplo de uso: ApiClient.getList("/clientes", new TypeToken<List<Cliente>>(){}.getType());
     */
    public static <T> T getList(String endpoint, Type tipoLista) throws Exception {
        HttpRequest.Builder builder = crearRequestBuilder(endpoint)
                .GET();

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        validarCodigoEstado(response);

        return gson.fromJson(response.body(), tipoLista);
    }

    /**
     * Realiza una petición POST enviando un objeto como JSON
     */
    public static <T> T post(String endpoint, Object body, Class<T> claseRespuesta) throws Exception {
        String jsonBody = gson.toJson(body);

        HttpRequest.Builder builder = crearRequestBuilder(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        return ejecutarPeticion(builder.build(), claseRespuesta);
    }

    /**
     * Realiza una petición PUT enviando un objeto como JSON para actualizar
     */
    public static <T> T put(String endpoint, Object body, Class<T> claseRespuesta) throws Exception {
        String jsonBody = gson.toJson(body);

        HttpRequest.Builder builder = crearRequestBuilder(endpoint)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));

        return ejecutarPeticion(builder.build(), claseRespuesta);
    }

    /**
     * Realiza una petición DELETE
     */
    public static void delete(String endpoint) throws Exception {
        HttpRequest.Builder builder = crearRequestBuilder(endpoint)
                .DELETE();

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        validarCodigoEstado(response);
    }

    // ==========================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ==========================================

    private static HttpRequest.Builder crearRequestBuilder(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + endpoint))
                .header("Content-Type", "application/json") // <-- ¡ESTA ES LA LÍNEA MÁGICA!
                .header("Accept", "application/json");      // (Opcional pero recomendada)

        if (jwtToken != null && !jwtToken.isBlank()) {
            builder.header("Authorization", "Bearer " + jwtToken);
        }

        return builder;
    }

    private static <T> T ejecutarPeticion(HttpRequest request, Class<T> claseRespuesta) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        validarCodigoEstado(response);

        if (claseRespuesta == String.class) {
            return claseRespuesta.cast(response.body());
        }

        return gson.fromJson(response.body(), claseRespuesta);
    }

    private static void validarCodigoEstado(HttpResponse<String> response) throws Exception {
        int code = response.statusCode();

        if (code >= 200 && code < 300) {
            return; // OK
        }

        switch (code) {
            case 400 -> throw new Exception("Petición incorrecta (400): " + extraerMensajeError(response.body()));
            case 401 -> throw new Exception("No autorizado (401). Verifique credenciales o token expirado.");
            case 403 -> throw new Exception("Acceso denegado (403). No posee permisos suficientes.");
            case 404 -> throw new Exception("Recurso no encontrado (404).");
            case 500 -> throw new Exception("Error interno del servidor (500).");
            default -> throw new Exception("Error HTTP " + code + ": " + response.body());
        }
    }

    private static String extraerMensajeError(String jsonBody) {
        try {
            // Intenta extraer la propiedad "mensaje" o "error" si la API de Node.js la devuelve
            var jsonObject = gson.fromJson(jsonBody, com.google.gson.JsonObject.class);
            if (jsonObject.has("mensaje")) return jsonObject.get("mensaje").getAsString();
            if (jsonObject.has("error")) return jsonObject.get("error").getAsString();
        } catch (Exception ignored) {}
        return jsonBody;
    }
}
