package com.gym.app.gui.dialogs;

import com.gym.app.models.Plan;
import com.gym.app.services.PlanService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class PlanDialog extends JDialog {

    private JTextField txtNombre, txtPrecio, txtDuracion;
    private JButton btnGuardar, btnCancelar;
    private boolean guardadoExitoso = false;
    private final PlanService planService;
    private final Plan planAEditar;

    public PlanDialog(Frame parent) {
        super(parent, "Registrar Nuevo Plan", true);
        this.planService = new PlanService();
        this.planAEditar = null;
        initUI();
    }

    public PlanDialog(Frame parent, Plan planAEditar) {
        super(parent, "Editar Plan", true);
        this.planService = new PlanService();
        this.planAEditar = planAEditar;
        initUI();
        txtNombre.setText(planAEditar.getNombre());
        txtPrecio.setText(planAEditar.getPrecio().toPlainString());
        txtDuracion.setText(String.valueOf(planAEditar.getDuracion()));
        btnGuardar.setText("Guardar Cambios");
    }

    private void initUI() {
        setSize(350, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelForm.add(txtPrecio);

        panelForm.add(new JLabel("Duración (Días):"));
        txtDuracion = new JTextField();
        panelForm.add(txtDuracion);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarPlan());
    }

    private void guardarPlan() {
        if (txtNombre.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty() || txtDuracion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean esEdicion = planAEditar != null;

            Plan plan = new Plan();
            plan.setNombre(txtNombre.getText().trim());
            plan.setPrecio(new BigDecimal(txtPrecio.getText().trim()));
            plan.setDuracion(Integer.parseInt(txtDuracion.getText().trim()));

            String textoBotonNormal = esEdicion ? "Guardar Cambios" : "Guardar";
            btnGuardar.setEnabled(false);
            btnGuardar.setText("Guardando...");

            SwingWorker<Plan, Void> worker = new SwingWorker<>() {
                @Override
                protected Plan doInBackground() throws Exception {
                    return esEdicion
                            ? planService.actualizarPlan(planAEditar.getId(), plan)
                            : planService.crearPlan(plan);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        guardadoExitoso = true;
                        JOptionPane.showMessageDialog(PlanDialog.this,
                                esEdicion ? "¡Plan actualizado con éxito!" : "¡Plan registrado con éxito!");
                        dispose();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PlanDialog.this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText(textoBotonNormal);
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio y la duración deben ser valores numéricos válidos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}