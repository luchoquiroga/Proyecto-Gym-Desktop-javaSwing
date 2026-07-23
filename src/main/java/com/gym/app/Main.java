package com.gym.app;

import com.formdev.flatlaf.FlatDarkLaf;
import com.gym.app.gui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 1. Activar Look and Feel oscuro moderno
        FlatDarkLaf.setup();

        // 2. Abrir la pantalla de Login en el Hilo de Eventos (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}