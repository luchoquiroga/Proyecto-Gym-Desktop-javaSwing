package com.gym.app.gui.panels;

import com.google.gson.Gson;
import com.gym.app.models.EstadisticasDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DashboardPanel extends JPanel {

    // Etiquetas donde mostraremos los números actualizados
    private JLabel lblActivos;
    private JLabel lblInactivos;
    private JLabel lblTotalClientes;
    private JLabel lblRecaudacion;

    public DashboardPanel() {
        initComponents();
        cargarDatosDesdeAPI();
    }

    private void initComponents() {
        // Configuración principal del panel con márgenes
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(25, 25, 25, 25));
        this.setBackground(new Color(245, 245, 248)); // Fondo claro moderno

        // Título superior
        JLabel lblTitulo = new JLabel("Resumen General del Gimnasio");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(40, 40, 40));
        this.add(lblTitulo, BorderLayout.NORTH);

        // Panel de Tarjetas (Grilla 2x2 con separación de 20px)
        JPanel panelTarjetas = new JPanel(new GridLayout(2, 2, 20, 20));
        panelTarjetas.setOpaque(false);

        // Inicializamos las etiquetas de valor con "Cargando..." o "0"
        lblActivos = new JLabel("...", SwingConstants.CENTER);
        lblInactivos = new JLabel("...", SwingConstants.CENTER);
        lblTotalClientes = new JLabel("...", SwingConstants.CENTER);
        lblRecaudacion = new JLabel("...", SwingConstants.CENTER);

        // Agregamos las 4 tarjetas personalizadas
        panelTarjetas.add(crearTarjeta("CLIENTES ACTIVOS", lblActivos, new Color(46, 204, 113)));     // Verde
        panelTarjetas.add(crearTarjeta("CLIENTES INACTIVOS", lblInactivos, new Color(231, 76, 60)));  // Rojo
        panelTarjetas.add(crearTarjeta("TOTAL SOCIOS", lblTotalClientes, new Color(52, 152, 219)));   // Azul
        panelTarjetas.add(crearTarjeta("RECAUDACIÓN DEL MES", lblRecaudacion, new Color(241, 196, 15))); // Dorado

        this.add(panelTarjetas, BorderLayout.CENTER);
    }

    // Método auxiliar para diseñar las "Tarjetas" visuales
    private JPanel crearTarjeta(String titulo, JLabel lblValor, Color colorAcento) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Título de la tarjeta
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(100, 100, 100));

        // Estilo del número gigante
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValor.setForeground(colorAcento);

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }

    // --- CONEXIÓN CON TU BACKEND NODE.JS ---
    private void cargarDatosDesdeAPI() {
        // Ejecutamos la petición en un hilo secundario para no congelar la ventana
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:3000/api/estadisticas"); // Ajustá el puerto si es necesario
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder jsonRespuesta = new StringBuilder();
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        jsonRespuesta.append(linea);
                    }
                    br.close();

                    // Mapeamos el JSON a nuestro DTO con Gson
                    Gson gson = new Gson();
                    EstadisticasDTO stats = gson.fromJson(jsonRespuesta.toString(), EstadisticasDTO.class);

                    // Actualizamos la interfaz gráfica (siempre desde el hilo de Swing)
                    SwingUtilities.invokeLater(() -> {
                        lblActivos.setText(String.valueOf(stats.getClientes().getActivos()));
                        lblInactivos.setText(String.valueOf(stats.getClientes().getInactivos()));
                        lblTotalClientes.setText(String.valueOf(stats.getClientes().getTotal()));

                        // Formateamos la plata con signo $
                        lblRecaudacion.setText(String.format("$ %,.2f", stats.getFinanzas().getRecaudacionMes()));
                    });
                }
                conn.disconnect();
            } catch (Exception e) {
                System.err.println("Error al obtener estadísticas del backend: " + e.getMessage());
                SwingUtilities.invokeLater(() -> {
                    lblActivos.setText("Error");
                    lblInactivos.setText("Error");
                    lblTotalClientes.setText("Error");
                    lblRecaudacion.setText("Error");
                });
            }
        }).start();
    }
}