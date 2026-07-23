package com.gym.app.gui.panels;

import com.gym.app.models.Pago;
import com.gym.app.services.PagoService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class PagoDialog extends JDialog {

    private JTextField txtIdCliente, txtIdPlan, txtMonto;
    private JButton btnGuardar, btnCancelar;
    private boolean guardadoExitoso = false;
    private final PagoService pagoService;

    public PagoDialog(Frame parent) {
        super(parent, "Registrar Nuevo Pago", true);
        this.pagoService = new PagoService();
        initUI();
    }

    private void initUI() {
        setSize(350, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelForm.add(new JLabel("ID Cliente:"));
        txtIdCliente = new JTextField();
        panelForm.add(txtIdCliente);

        panelForm.add(new JLabel("ID Plan:"));
        txtIdPlan = new JTextField();
        panelForm.add(txtIdPlan);

        panelForm.add(new JLabel("Monto ($):"));
        txtMonto = new JTextField();
        panelForm.add(txtMonto);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> registrarPago());
    }

    private void registrarPago() {
        if (txtIdCliente.getText().trim().isEmpty() || txtIdPlan.getText().trim().isEmpty() || txtMonto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Pago nuevo = new Pago();
            nuevo.setClienteId(Integer.parseInt(txtIdCliente.getText().trim()));
            nuevo.setPlanId(Integer.parseInt(txtIdPlan.getText().trim()));
            nuevo.setMontoAbonado(new BigDecimal(txtMonto.getText().trim()));

            btnGuardar.setEnabled(false);
            btnGuardar.setText("Guardando...");

            SwingWorker<Pago, Void> worker = new SwingWorker<>() {
                @Override
                protected Pago doInBackground() throws Exception {
                    return pagoService.registrarPago(nuevo);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        guardadoExitoso = true;
                        JOptionPane.showMessageDialog(PagoDialog.this, "¡Pago registrado con éxito!");
                        dispose();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PagoDialog.this, "Error al registrar pago: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText("Guardar");
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los IDs deben ser numéricos y el monto un valor válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}