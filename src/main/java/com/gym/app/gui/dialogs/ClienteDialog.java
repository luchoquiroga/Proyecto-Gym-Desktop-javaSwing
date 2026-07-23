package com.gym.app.gui.dialogs;

import com.gym.app.models.Cliente;
import com.gym.app.services.ClienteService;

import javax.swing.*;
import java.awt.*;

public class ClienteDialog extends JDialog {

    private JTextField txtNombre, txtApellido, txtDni, txtEmail, txtTelefono;
    private JComboBox<Cliente.Estado> cmbEstado;
    private JButton btnGuardar, btnCancelar;
    private boolean guardadoExitoso = false;
    private final ClienteService clienteService;

    public ClienteDialog(Frame parent) {
        super(parent, "Registrar Nuevo Cliente", true); // true = Modal (bloquea la ventana principal mientras está abierto)
        this.clienteService = new ClienteService();
        initUI();
    }

    private void initUI() {
        setSize(400, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // Panel de Formulario con GridLayout
        JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelForm.add(txtApellido);

        panelForm.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelForm.add(txtDni);

        panelForm.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panelForm.add(txtEmail);

        panelForm.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panelForm.add(txtTelefono);

        panelForm.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(Cliente.Estado.values());
        panelForm.add(cmbEstado);

        add(panelForm, BorderLayout.CENTER);

        // Panel de Botones Inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarCliente());
    }

    private void guardarCliente() {
        // Validaciones básicas vacías
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty() || txtDni.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, Apellido y DNI son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Creamos el objeto Cliente con los datos del formulario
        Cliente nuevo = new Cliente();
        nuevo.setNombre(txtNombre.getText().trim());
        nuevo.setApellido(txtApellido.getText().trim());
        nuevo.setDni(txtDni.getText().trim());
        nuevo.setEmail(txtEmail.getText().trim());
        nuevo.setTelefono(txtTelefono.getText().trim());
        nuevo.setEstado((Cliente.Estado) cmbEstado.getSelectedItem());

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        // Usamos SwingWorker para no congelar la pantalla al hacer el POST a Node.js
        SwingWorker<Cliente, Void> worker = new SwingWorker<>() {
            @Override
            protected Cliente doInBackground() throws Exception {
                return clienteService.crearCliente(nuevo);
            }

            @Override
            protected void done() {
                try {
                    get(); // Si hubo error, salta al catch
                    guardadoExitoso = true;
                    JOptionPane.showMessageDialog(ClienteDialog.this, "¡Cliente registrado con éxito!");
                    dispose(); // Cierra el diálogo
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClienteDialog.this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar");
                }
            }
        };
        worker.execute();
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}
