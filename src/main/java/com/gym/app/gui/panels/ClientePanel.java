package com.gym.app.gui.panels;

import com.gym.app.gui.dialogs.ClienteDialog;
import com.gym.app.models.Cliente;
import com.gym.app.services.ClienteService;

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
    private final ClienteService clienteService;
    private JTextField txtBuscar; // Paso 1: Declarar el campo de texto para la búsqueda
    private TableRowSorter<DefaultTableModel> sorter; // Paso 1: Declarar el ordenador de filas

    public ClientePanel() {
        this.clienteService = new ClienteService();
        initUI();
        cargarDatos(); // Cargamos la tabla apenas se crea el panel
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Panel Superior General (contendrá título/botones y buscador) ---
        JPanel mainNorthPanel = new JPanel();
        mainNorthPanel.setLayout(new BoxLayout(mainNorthPanel, BoxLayout.Y_AXIS)); // Usamos BoxLayout para apilar verticalmente

        // --- 1. TÍTULO Y BOTONES SUPERIORES ---
        JPanel panelTop = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Gestión de Clientes");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnActualizar = new JButton("Actualizar Tabla");
        btnNuevo = new JButton("+ Nuevo Cliente");

        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevo);

        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(panelBotones, BorderLayout.EAST);
        panelTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        mainNorthPanel.add(panelTop); // Agregamos el panel de título/botones al panel superior general

        // --- Paso 2: Inicializar el buscador y agregarlo ---
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Usamos FlowLayout para el buscador
        txtBuscar = new JTextField(20);
        panelBuscar.add(new JLabel("Buscar Cliente: "));
        panelBuscar.add(txtBuscar);
        panelBuscar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Pequeño margen inferior

        mainNorthPanel.add(panelBuscar); // Agregamos el panel del buscador al panel superior general

        add(mainNorthPanel, BorderLayout.NORTH); // Agregamos el panel superior general al NORTH del ClientePanel

        // --- 2. TABLA DE DATOS ---
        String[] columnas = {"ID", "Nombre", "Apellido", "DNI", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que editen las celdas haciendo doble clic
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(25);
        tablaClientes.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Paso 3: Configurar el filtro en el modelo de la tabla
        sorter = new TableRowSorter<>(modeloTabla);
        tablaClientes.setRowSorter(sorter);

        // Metemos la tabla en un ScrollPane por si hay muchos clientes
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. EVENTOS ---
        btnActualizar.addActionListener(e -> cargarDatos());
        btnNuevo.addActionListener(e -> {
            // Abrimos el diálogo modal pasando la ventana principal como referencia
            ClienteDialog dialog = new ClienteDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);

            // Si el usuario guardó con éxito, recargamos la tabla automáticamente
            if (dialog.isGuardadoExitoso()) {
                cargarDatos();
            }
        });

        // Paso 4: La Magia (El Listener que escucha cada tecla)
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
    }

    private void cargarDatos() {
        btnActualizar.setEnabled(false);
        btnActualizar.setText("Cargando...");
        modeloTabla.setRowCount(0); // Limpiamos la tabla antes de cargar

        SwingWorker<List<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                return clienteService.listarClientes();
            }

            @Override
            protected void done() {
                try {
                    List<Cliente> clientes = get();
                    for (Cliente c : clientes) {
                        modeloTabla.addRow(new Object[]{
                                c.getId(),
                                c.getNombre(),
                                c.getApellido(),
                                c.getDni(),
                                c.getEstado()
                        });
                    }
                    filtrar(); // Aplicar filtro después de cargar datos
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClientePanel.this,
                            "Error al cargar clientes: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnActualizar.setEnabled(true);
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