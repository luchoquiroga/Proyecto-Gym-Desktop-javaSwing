package com.gym.app.gui.panels;

import com.gym.app.models.Cliente;
import com.gym.app.models.GananciasMensuales;
import com.gym.app.services.ClienteService;
import com.gym.app.services.DashboardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DashboardPanel extends JPanel {

    private static final String[] NOMBRES_MESES = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    // Etiquetas donde mostraremos los números actualizados
    private JLabel lblActivos;
    private JLabel lblInactivos;
    private JLabel lblTotalClientes;
    private JLabel lblRecaudacion;

    private JComboBox<String> cbxMes;
    private JComboBox<Integer> cbxAnio;

    private final boolean esAdmin;
    private final ClienteService clienteService = new ClienteService();
    private final DashboardService dashboardService = new DashboardService();

    public DashboardPanel(boolean esAdmin) {
        this.esAdmin = esAdmin;
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

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);
        panelNorte.add(lblTitulo, BorderLayout.NORTH);

        // Selector de mes/año: solo tiene sentido para ADMIN, único rol habilitado
        // para consultar /dashboard/ganancias-mensuales en el backend.
        if (esAdmin) {
            panelNorte.add(crearSelectorPeriodo(), BorderLayout.SOUTH);
        }

        this.add(panelNorte, BorderLayout.NORTH);

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
        panelTarjetas.add(crearTarjeta("CLIENTES INACTIVOS/MOROSOS", lblInactivos, new Color(231, 76, 60)));  // Rojo
        panelTarjetas.add(crearTarjeta("TOTAL SOCIOS", lblTotalClientes, new Color(52, 152, 219)));   // Azul
        panelTarjetas.add(crearTarjeta("RECAUDACIÓN DEL MES", lblRecaudacion, new Color(241, 196, 15))); // Dorado

        this.add(panelTarjetas, BorderLayout.CENTER);
    }

    private JPanel crearSelectorPeriodo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        LocalDate hoy = LocalDate.now();

        cbxMes = new JComboBox<>(NOMBRES_MESES);
        cbxMes.setSelectedIndex(hoy.getMonthValue() - 1);

        int anioActual = hoy.getYear();
        Integer[] anios = new Integer[6];
        for (int i = 0; i < anios.length; i++) {
            anios[i] = anioActual - i;
        }
        cbxAnio = new JComboBox<>(anios);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> cargarRecaudacionMes());

        panel.add(new JLabel("Mes:"));
        panel.add(cbxMes);
        panel.add(new JLabel("Año:"));
        panel.add(cbxAnio);
        panel.add(btnConsultar);

        return panel;
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

    private void cargarDatosDesdeAPI() {
        // Ejecutamos ambas peticiones en un hilo secundario para no congelar la ventana
        new Thread(() -> {
            cargarEstadisticasClientes();
            cargarRecaudacionMes();
        }).start();
    }

    /**
     * El backend no tiene un endpoint agregado de estadísticas de clientes,
     * así que traemos la lista completa (GET /clientes, accesible para ADMIN o GERENCIA)
     * y contamos los estados acá.
     */
    private void cargarEstadisticasClientes() {
        try {
            List<Cliente> clientes = clienteService.listarClientes();

            int activos = 0;
            int inactivosOMorosos = 0;
            for (Cliente c : clientes) {
                if (c.getEstado() == Cliente.Estado.ACTIVO) {
                    activos++;
                } else {
                    inactivosOMorosos++;
                }
            }

            int total = clientes.size();
            final int activosFinal = activos;
            final int inactivosFinal = inactivosOMorosos;
            SwingUtilities.invokeLater(() -> {
                lblActivos.setText(String.valueOf(activosFinal));
                lblInactivos.setText(String.valueOf(inactivosFinal));
                lblTotalClientes.setText(String.valueOf(total));
            });
        } catch (Exception e) {
            System.err.println("Error al obtener clientes para el dashboard: " + e.getMessage());
            SwingUtilities.invokeLater(() -> {
                lblActivos.setText("Error");
                lblInactivos.setText("Error");
                lblTotalClientes.setText("Error");
            });
        }
    }

    /**
     * Endpoint restringido a rol ADMIN en el backend: si el usuario logueado es GERENCIA,
     * la petición devuelve 403 y mostramos "N/A" en vez de romper el panel.
     */
    private void cargarRecaudacionMes() {
        SwingUtilities.invokeLater(() -> lblRecaudacion.setText("..."));

        new Thread(() -> {
            try {
                GananciasMensuales ganancias = esAdmin
                        ? dashboardService.obtenerGananciasMensuales(
                                (Integer) cbxAnio.getSelectedItem(), cbxMes.getSelectedIndex() + 1)
                        : dashboardService.obtenerGananciasMensuales();
                SwingUtilities.invokeLater(() ->
                        lblRecaudacion.setText(String.format("$ %,.2f", ganancias.getTotalGanancias()))
                );
            } catch (Exception e) {
                System.err.println("Error al obtener ganancias mensuales: " + e.getMessage());
                SwingUtilities.invokeLater(() -> lblRecaudacion.setText("N/A"));
            }
        }).start();
    }
}
