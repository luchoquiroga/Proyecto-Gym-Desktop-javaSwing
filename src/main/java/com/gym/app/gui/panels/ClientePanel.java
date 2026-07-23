package com.gym.app.gui.panels;

import com.gym.app.gui.dialogs.ClienteDialog;
import com.gym.app.models.Cliente;
import com.gym.app.services.ClienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientePanel extends JPanel {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnActualizar;
    private JButton btnNuevo;
    private final ClienteService clienteService;

    public ClientePanel() {
        this.clienteService = new ClienteService();
        initUI();
        cargarDatos(); // Cargamos la tabla apenas se crea el panel
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

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

        add(panelTop, BorderLayout.NORTH);

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
}