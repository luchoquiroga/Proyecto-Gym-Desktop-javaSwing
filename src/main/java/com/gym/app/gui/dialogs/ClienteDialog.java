package com.gym.app.gui.dialogs;

import com.gym.app.models.Cliente;
import com.gym.app.services.ClienteService;

import javax.swing.*;
import java.awt.*;

public class ClienteDialog extends JDialog {

    private JTextField txtNombre, txtApellido, txtTelefono;
    private JButton btnGuardar, btnCancelar;
    private boolean guardadoExitoso = false;
    private final ClienteService clienteService;
    private final Cliente clienteAEditar;

    public ClienteDialog(Frame parent) {
        super(parent, "Registrar Nuevo Cliente", true); // true = Modal (bloquea la ventana principal mientras está abierto)
        this.clienteService = new ClienteService();
        this.clienteAEditar = null;
        initUI();
    }

    /**
     * Modo edición: precarga los campos con los datos del cliente y, al guardar,
     * actualiza en vez de crear. El estado no se edita acá (el backend lo maneja aparte).
     */
    public ClienteDialog(Frame parent, Cliente clienteAEditar) {
        super(parent, "Editar Cliente", true);
        this.clienteService = new ClienteService();
        this.clienteAEditar = clienteAEditar;
        initUI();
        txtNombre.setText(clienteAEditar.getNombre());
        txtApellido.setText(clienteAEditar.getApellido());
        txtTelefono.setText(clienteAEditar.getTelefono());
        btnGuardar.setText("Guardar Cambios");
    }

    private void initUI() {
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // Panel de Formulario con GridLayout
        // El Estado no se elige a mano: el backend lo inicia en INACTIVO y lo actualiza
        // automáticamente (ACTIVO al registrar un pago, MOROSO/INACTIVO según vencimiento).
        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelForm.add(txtApellido);

        panelForm.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panelForm.add(txtTelefono);

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
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Apellido son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean esEdicion = clienteAEditar != null;

        // Creamos el objeto Cliente con los datos del formulario
        Cliente cliente = new Cliente();
        cliente.setNombre(txtNombre.getText().trim());
        cliente.setApellido(txtApellido.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        // No seteamos estado: en alta lo deja null a propósito para que el backend aplique
        // su regla por defecto (INACTIVO hasta el primer pago); en edición el backend lo ignora.

        String textoBotonNormal = esEdicion ? "Guardar Cambios" : "Guardar";
        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        SwingWorker<Cliente, Void> worker = new SwingWorker<>() {
            @Override
            protected Cliente doInBackground() throws Exception {
                return esEdicion
                        ? clienteService.actualizarCliente(clienteAEditar.getId(), cliente)
                        : clienteService.crearCliente(cliente);
            }

            @Override
            protected void done() {
                try {
                    get(); // Si hubo error, salta al catch
                    guardadoExitoso = true;
                    JOptionPane.showMessageDialog(ClienteDialog.this,
                            esEdicion ? "¡Cliente actualizado con éxito!" : "¡Cliente registrado con éxito!");
                    dispose(); // Cierra el diálogo
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClienteDialog.this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText(textoBotonNormal);
                }
            }
        };
        worker.execute();
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}
