package com.gym.app.gui.dialogs;

import com.gym.app.models.Usuario;
import com.gym.app.services.UsuarioService;

import javax.swing.*;
import java.awt.*;

public class UsuarioDialog extends JDialog {

    private JTextField txtNombre;
    private JPasswordField txtContrasena, txtConfirmarContrasena;
    private JComboBox<Usuario.Rol> cbxRol;
    private JButton btnGuardar, btnCancelar;
    private boolean guardadoExitoso = false;
    private final UsuarioService usuarioService;

    public UsuarioDialog(Frame parent) {
        super(parent, "Crear Nuevo Usuario", true);
        this.usuarioService = new UsuarioService();
        initUI();
    }

    private void initUI() {
        setSize(380, 320);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelForm.add(new JLabel("Nombre de usuario:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Contraseña:"));
        txtContrasena = new JPasswordField();
        panelForm.add(txtContrasena);

        panelForm.add(new JLabel("Confirmar contraseña:"));
        txtConfirmarContrasena = new JPasswordField();
        panelForm.add(txtConfirmarContrasena);

        panelForm.add(new JLabel("Rol:"));
        // GERENCIA preseleccionado: es el caso de uso más frecuente para altas de usuarios.
        cbxRol = new JComboBox<>(new Usuario.Rol[]{Usuario.Rol.GERENCIA, Usuario.Rol.ADMIN});
        panelForm.add(cbxRol);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarUsuario());
    }

    private void guardarUsuario() {
        String nombre = txtNombre.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        String confirmacion = new String(txtConfirmarContrasena.getPassword());

        if (nombre.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y contraseña son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!contrasena.equals(confirmacion)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setContrasena(contrasena);
        nuevo.setRol((Usuario.Rol) cbxRol.getSelectedItem());

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                return usuarioService.crearUsuario(nuevo);
            }

            @Override
            protected void done() {
                try {
                    get();
                    guardadoExitoso = true;
                    JOptionPane.showMessageDialog(UsuarioDialog.this, "¡Usuario creado con éxito!");
                    dispose();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(UsuarioDialog.this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
