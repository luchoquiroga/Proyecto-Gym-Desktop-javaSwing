package com.gym.app.gui;

import com.gym.app.models.Usuario;
import com.gym.app.services.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPass;
    private JButton btnIngresar;
    private JLabel lblEstado;
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Acceso al Sistema - Gimnasio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Título principal
        JLabel lblTitulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Campo Email
        gbc.gridwidth = 2; gbc.gridy = 1;
        panel.add(new JLabel("Correo Electrónico:"), gbc);

        txtEmail = new JTextField(20);
        gbc.gridy = 2;
        panel.add(txtEmail, gbc);

        // Campo Contraseña
        gbc.gridy = 3;
        panel.add(new JLabel("Contraseña:"), gbc);

        txtPass = new JPasswordField(20);
        gbc.gridy = 4;
        panel.add(txtPass, gbc);

        // Botón Ingresar
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridy = 5;
        gbc.insets = new Insets(18, 5, 8, 5);
        panel.add(btnIngresar, gbc);

        // Label de Estado / Errores
        lblEstado = new JLabel(" ", SwingConstants.CENTER);
        lblEstado.setForeground(Color.RED);
        gbc.gridy = 6;
        panel.add(lblEstado, gbc);

        add(panel);

        // Evento al presionar botón o ENTER
        btnIngresar.addActionListener(e -> ejecutarLogin());
        txtPass.addActionListener(e -> ejecutarLogin());
    }

    private void ejecutarLogin() {
        String email = txtEmail.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            lblEstado.setText("Complete todos los campos.");
            return;
        }

        // Feedback visual de carga
        btnIngresar.setEnabled(false);
        btnIngresar.setText("Validando...");
        lblEstado.setText(" ");

        // Ejecución Asincrónica para no congelar la ventana
        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                return authService.login(email, pass);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuarioLogueado = get(); // Si hubo excepción en doInBackground, salta al catch

                    // ¡Login Exitoso! Cerramos Login y abrimos MainFrame
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = new MainFrame();
                        mainFrame.setVisible(true);
                    });

                } catch (Exception e) {
                    // Extraer mensaje limpio de error
                    Throwable causa = e.getCause() != null ? e.getCause() : e;
                    lblEstado.setText(causa.getMessage());
                    btnIngresar.setEnabled(true);
                    btnIngresar.setText("Ingresar");
                }
            }
        };

        worker.execute();
    }
}
