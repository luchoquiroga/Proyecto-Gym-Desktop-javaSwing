package com.gym.app.gui.panels;

import com.gym.app.gui.dialogs.PagoDialog;
import com.gym.app.models.Pago;
import com.gym.app.services.PagoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PagoPanel extends JPanel {

    private JTable tablaPagos;
    private DefaultTableModel modeloTabla;
    private JButton btnActualizar;
    private JButton btnNuevoPago;
    private final PagoService pagoService;

    public PagoPanel() {
        this.pagoService = new PagoService();
        initUI();
        cargarDatos();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título y Botones
        JPanel panelTop = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Control de Pagos y Membresías");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnActualizar = new JButton("Actualizar Tabla");
        btnNuevoPago = new JButton("+ Registrar Pago");

        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevoPago);

        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(panelBotones, BorderLayout.EAST);
        panelTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(panelTop, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Cliente", "Plan", "Monto", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPagos = new JTable(modeloTabla);
        tablaPagos.setRowHeight(25);
        tablaPagos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        add(new JScrollPane(tablaPagos), BorderLayout.CENTER);

        // Eventos
        btnActualizar.addActionListener(e -> cargarDatos());
        btnNuevoPago.addActionListener(e -> {
            PagoDialog dialog = new PagoDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            if (dialog.isGuardadoExitoso()) {
                cargarDatos();
            }
        });
    }

    private void cargarDatos() {
        btnActualizar.setEnabled(false);
        btnActualizar.setText("Cargando...");
        modeloTabla.setRowCount(0);

        SwingWorker<List<Pago>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Pago> doInBackground() throws Exception {
                return pagoService.listarPagos();
            }

            @Override
            protected void done() {
                try {
                    List<Pago> pagos = get();
                    for (Pago p : pagos) {
                        modeloTabla.addRow(new Object[]{
                                p.getId(),
                                p.getNombreCompletoCliente(), // ¡El modelo resuelve cómo mostrar el cliente!
                                p.getPlanNombre(),            // El modelo provee el nombre del plan directamente
                                p.getMontoAbonado(),
                                p.getFechaPago()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PagoPanel.this,
                            "Error al cargar pagos: " + e.getMessage(),
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