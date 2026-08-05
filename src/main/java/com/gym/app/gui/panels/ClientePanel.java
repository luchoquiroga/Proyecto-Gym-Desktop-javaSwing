package com.gym.app.gui.panels;

import com.gym.app.gui.dialogs.ClienteDialog;
import com.gym.app.models.Cliente;
import com.gym.app.services.ClienteService;
import com.gym.app.gui.render.AlertaVencimientoRenderer;
import com.gym.app.gui.render.EstadoRenderer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class ClientePanel extends JPanel {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnActualizar;
    private JButton btnNuevo;
    private JComboBox<String> cbxOrden; // <-- COMBOBOX PARA ELEGIR EL ORDEN
    private final ClienteService clienteService;
    private JTextField txtBuscar; // Paso 1: Declarar el campo de texto para la búsqueda
    private TableRowSorter<DefaultTableModel> sorter; // Paso 1: Declarar el ordenador de filas

    public ClientePanel() {
        this.clienteService = new ClienteService();
        initUI();
        cargarDatos("vencimiento"); // Por defecto cargamos por vencimiento
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- 1. TÍTULO, CONTROLES SUPERIORES Y BUSCADOR ---
        JPanel mainNorthPanel = new JPanel();
        mainNorthPanel.setLayout(new BoxLayout(mainNorthPanel, BoxLayout.Y_AXIS));

        // Panel de Título y Botones
        JPanel panelTop = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Gestión de Clientes");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cbxOrden = new JComboBox<>(new String[]{"Orden: Próximos a Vencer", "Orden: Alfabético (Apellido)"});
        btnActualizar = new JButton("Actualizar Tabla");
        btnNuevo = new JButton("+ Nuevo Cliente");

        panelBotones.add(new JLabel("Mostrar por: "));
        panelBotones.add(cbxOrden);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevo);

        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(panelBotones, BorderLayout.EAST);
        panelTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        mainNorthPanel.add(panelTop);

        // Panel del Buscador
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscar = new JTextField(20);
        panelBuscar.add(new JLabel("Buscar Cliente: "));
        panelBuscar.add(txtBuscar);
        panelBuscar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        mainNorthPanel.add(panelBuscar);

        add(mainNorthPanel, BorderLayout.NORTH);

        // --- 2. TABLA DE DATOS ---
        String[] columnas = {"Nombre", "Apellido", "DNI", "Estado", "Vencimiento"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(25);
        tablaClientes.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        tablaClientes.getColumnModel().getColumn(3).setCellRenderer(new EstadoRenderer());
        tablaClientes.getColumnModel().getColumn(4).setCellRenderer(new AlertaVencimientoRenderer());

        sorter = new TableRowSorter<>(modeloTabla);
        tablaClientes.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. EVENTOS ---
        cbxOrden.addActionListener(e -> {
            String ordenElegido = cbxOrden.getSelectedIndex() == 0 ? "vencimiento" : "apellido";
            cargarDatos(ordenElegido);
        });

        btnActualizar.addActionListener(e -> {
            String ordenElegido = cbxOrden.getSelectedIndex() == 0 ? "vencimiento" : "apellido";
            cargarDatos(ordenElegido);
        });

        btnNuevo.addActionListener(e -> {
            ClienteDialog dialog = new ClienteDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);

            if (dialog.isGuardadoExitoso()) {
                String ordenElegido = cbxOrden.getSelectedIndex() == 0 ? "vencimiento" : "apellido";
                cargarDatos(ordenElegido);
            }
        });

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
    }

    // Aceptamos el parámetro 'orden' para pasarlo a tu ClienteService
    private void cargarDatos(String orden) {
        btnActualizar.setEnabled(false);
        cbxOrden.setEnabled(false);
        btnActualizar.setText("Cargando...");
        modeloTabla.setRowCount(0);

        SwingWorker<List<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                // Pasamos el criterio de ordenamiento a nuestro servicio
                return clienteService.listarClientes(orden);
            }

            @Override
            protected void done() {
                try {
                    List<Cliente> clientes = get();
                    for (Cliente c : clientes) {
                        modeloTabla.addRow(new Object[]{
                                c.getNombre(),     // Columna 0
                                c.getApellido(),   // Columna 1
                                c.getDni(),        // Columna 2
                                c.getEstado(),     // Columna 3
                                c.getFechaVencimiento() != null ? c.getFechaVencimiento() : "---" // Columna 4
                        });
                    }
                    filtrar(); // Aplicar filtro después de cargar datos
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClientePanel.this,
                            "Error al cargar clientes: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnActualizar.setEnabled(true);
                    cbxOrden.setEnabled(true);
                    btnActualizar.setText("Actualizar Tabla");
                }
            }
        };
        worker.execute();
    }

    // Paso 5: Crear el método filtrar()
    private void filtrar() {
        String texto = txtBuscar.getText();
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null); // Si está vacío, muestra todo
        } else {
            // El "(?i)" hace que la búsqueda ignore mayúsculas y minúsculas
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }
}