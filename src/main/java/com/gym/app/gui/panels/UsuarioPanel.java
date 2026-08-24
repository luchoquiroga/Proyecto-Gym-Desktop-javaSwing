package com.gym.app.gui.panels;

import com.gym.app.gui.dialogs.UsuarioDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de administración de usuarios. Solo se muestra a usuarios con rol ADMIN
 * (ver MainFrame). El backend no expone un endpoint para listar usuarios,
 * así que este panel se limita al alta de nuevas cuentas.
 */
public class UsuarioPanel extends JPanel {

    public UsuarioPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelTop = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevo = new JButton("+ Nuevo Usuario");
        panelBotones.add(btnNuevo);

        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(panelBotones, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        JLabel lblInfo = new JLabel(
                "<html>Desde acá podés crear nuevas cuentas de acceso al sistema.<br>"
                        + "Por defecto se crean con rol GERENCIA; elegí ADMIN solo si corresponde.</html>",
                SwingConstants.CENTER);
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblInfo.setForeground(new Color(100, 100, 100));
        add(lblInfo, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> {
            UsuarioDialog dialog = new UsuarioDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
        });
    }
}
