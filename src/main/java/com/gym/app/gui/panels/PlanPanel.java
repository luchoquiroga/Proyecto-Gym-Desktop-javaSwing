package com.gym.app.gui.panels;

import com.gym.app.gui.dialogs.PlanDialog;
import com.gym.app.models.Plan;
import com.gym.app.services.PlanService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlanPanel extends JPanel {

    private JTable tablaPlanes;
    private DefaultTableModel modeloTabla;
    private JButton btnActualizar;
    private JButton btnNuevo;
    private final PlanService planService;

    public PlanPanel() {
        this.planService = new PlanService();
        initUI();
        cargarDatos();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título y Botones
        JPanel panelTop = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Gestión de Planes");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnActualizar = new JButton("Actualizar Tabla");
        btnNuevo = new JButton("+ Nuevo Plan");

        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevo);

        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(panelBotones, BorderLayout.EAST);
        panelTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(panelTop, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Precio", "Duración (Días)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPlanes = new JTable(modeloTabla);
        tablaPlanes.setRowHeight(25);
        tablaPlanes.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        add(new JScrollPane(tablaPlanes), BorderLayout.CENTER);

        // Eventos
        btnActualizar.addActionListener(e -> cargarDatos());
        btnNuevo.addActionListener(e -> {
            PlanDialog dialog = new PlanDialog((Frame) SwingUtilities.getWindowAncestor(this));
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

        SwingWorker<List<Plan>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Plan> doInBackground() throws Exception {
                return planService.listarPlanes();
            }

            @Override
            protected void done() {
                try {
                    List<Plan> planes = get();
                    for (Plan p : planes) {
                        modeloTabla.addRow(new Object[]{
                                p.getId(),
                                p.getNombre(),
                                p.getPrecio(),
                                p.getDuracionDias()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PlanPanel.this,
                            "Error al cargar planes: " + e.getMessage(),
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
