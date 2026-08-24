package com.gym.app.gui.dialogs;

import com.gym.app.models.Cliente;
import com.gym.app.models.Pago;
import com.gym.app.models.PagoRequest;
import com.gym.app.models.Plan;
import com.gym.app.services.ClienteService;
import com.gym.app.services.PagoService;
import com.gym.app.services.PlanService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PagoDialog extends JDialog {

    private JComboBox<Cliente> cbxCliente;
    private JComboBox<Plan> cbxPlan;
    private JTextField txtMonto;
    private JButton btnGuardar, btnCancelar;
    private boolean guardadoExitoso = false;

    private final PagoService pagoService;
    private final ClienteService clienteService;
    private final PlanService planService;

    public PagoDialog(Frame parent) {
        super(parent, "Registrar Nuevo Pago", true);
        this.pagoService = new PagoService();
        this.clienteService = new ClienteService();
        this.planService = new PlanService();
        initUI();
        cargarClientesYPlanes();
    }

    private void initUI() {
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelForm.add(new JLabel("Cliente:"));
        cbxCliente = new JComboBox<>();
        cbxCliente.setEnabled(false);
        panelForm.add(cbxCliente);

        panelForm.add(new JLabel("Plan:"));
        cbxPlan = new JComboBox<>();
        cbxPlan.setEnabled(false);
        panelForm.add(cbxPlan);

        panelForm.add(new JLabel("Monto ($):"));
        txtMonto = new JTextField();
        panelForm.add(txtMonto);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnGuardar.setEnabled(false);
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> registrarPago());

        // Al elegir un plan, autocompletamos el monto con su precio (el usuario lo puede editar igual)
        cbxPlan.addActionListener(e -> {
            Plan seleccionado = (Plan) cbxPlan.getSelectedItem();
            if (seleccionado != null && seleccionado.getPrecio() != null) {
                txtMonto.setText(seleccionado.getPrecio().toPlainString());
            }
        });
    }

    private void cargarClientesYPlanes() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<Cliente> clientes;
            private List<Plan> planes;

            @Override
            protected Void doInBackground() throws Exception {
                clientes = clienteService.listarClientes();
                planes = planService.listarPlanes();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    for (Cliente c : clientes) cbxCliente.addItem(c);
                    for (Plan p : planes) cbxPlan.addItem(p);

                    if (clientes.isEmpty() || planes.isEmpty()) {
                        JOptionPane.showMessageDialog(PagoDialog.this,
                                "Hace falta tener al menos un cliente y un plan cargados para registrar un pago.",
                                "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    cbxCliente.setEnabled(true);
                    cbxPlan.setEnabled(true);
                    btnGuardar.setEnabled(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PagoDialog.this,
                            "Error al cargar clientes/planes: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void registrarPago() {
        Cliente clienteSeleccionado = (Cliente) cbxCliente.getSelectedItem();
        Plan planSeleccionado = (Plan) cbxPlan.getSelectedItem();

        if (clienteSeleccionado == null || planSeleccionado == null || txtMonto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            PagoRequest nuevo = new PagoRequest();
            nuevo.setClienteId(clienteSeleccionado.getId());
            nuevo.setPlanId(planSeleccionado.getId());
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
            JOptionPane.showMessageDialog(this, "El monto debe ser un valor numérico válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}
