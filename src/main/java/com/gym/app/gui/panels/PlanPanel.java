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
    private JButton btnEditar;
    private JButton btnEliminar;
    private final PlanService planService;
    private List<Plan> planesCargados = List.of();

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
        btnEditar = new JButton("Editar Plan");
        btnEliminar = new JButton("Eliminar Plan");
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(panelBotones, BorderLayout.EAST);
        panelTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(panelTop, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Nombre", "Precio", "Duración (Días)"};
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

        tablaPlanes.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            boolean haySeleccion = tablaPlanes.getSelectedRow() != -1;
            btnEditar.setEnabled(haySeleccion);
            btnEliminar.setEnabled(haySeleccion);
        });

        // Eventos
        btnActualizar.addActionListener(e -> cargarDatos());
        btnNuevo.addActionListener(e -> {
            PlanDialog dialog = new PlanDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            if (dialog.isGuardadoExitoso()) {
                cargarDatos();
            }
        });
        btnEditar.addActionListener(e -> editarPlanSeleccionado());
        btnEliminar.addActionListener(e -> eliminarPlanSeleccionado());
    }

    private Plan obtenerPlanSeleccionado() {
        int filaVista = tablaPlanes.getSelectedRow();
        if (filaVista == -1) return null;
        int filaModelo = tablaPlanes.convertRowIndexToModel(filaVista);
        return planesCargados.get(filaModelo);
    }

    private void editarPlanSeleccionado() {
        Plan seleccionado = obtenerPlanSeleccionado();
        if (seleccionado == null) return;

        PlanDialog dialog = new PlanDialog((Frame) SwingUtilities.getWindowAncestor(this), seleccionado);
        dialog.setVisible(true);

        if (dialog.isGuardadoExitoso()) {
            cargarDatos();
        }
    }

    private void eliminarPlanSeleccionado() {
        Plan seleccionado = obtenerPlanSeleccionado();
        if (seleccionado == null) return;

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Confirma eliminar el plan \"" + seleccionado.getNombre() + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        btnEliminar.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                planService.eliminarPlan(seleccionado.getId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(PlanPanel.this, "Plan eliminado con éxito.");
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PlanPanel.this,
                            "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnEliminar.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void cargarDatos() {
        btnActualizar.setEnabled(false);
        btnActualizar.setText("Cargando...");
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
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
                    planesCargados = planes;
                    for (Plan p : planes) {
                        modeloTabla.addRow(new Object[]{
                                p.getNombre(),
                                p.getPrecio(),
                                p.getDuracion()
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
