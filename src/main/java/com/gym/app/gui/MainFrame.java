package com.gym.app.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel panelContenedor;
    private CardLayout cardLayout;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Sistema de Gestión de Gimnasio - Panel Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        // El layout principal divide la ventana (Oeste para el menú, Centro para el contenido)
        setLayout(new BorderLayout());

        // 1. Inicializamos el mazo de cartas (CardLayout) y su panel contenedor
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // 2. Agregamos las "cartas" (pantallas) al contenedor
        // Por ahora usamos paneles de prueba. Luego reemplazaremos por new ClientePanel(), etc.
        panelContenedor.add(crearPanelPrueba("Dashboard - Resumen General"), "DASHBOARD");
        panelContenedor.add(new com.gym.app.gui.panels.ClientePanel(), "CLIENTES");
        panelContenedor.add(new com.gym.app.gui.panels.PlanPanel(), "PLANES");
        panelContenedor.add(new com.gym.app.gui.panels.PagoPanel(), "PAGOS");

        // 3. Creamos el menú lateral
        JPanel panelMenu = crearMenuLateral();

        // 4. Agregamos todo a la ventana principal
        add(panelMenu, BorderLayout.WEST);
        add(panelContenedor, BorderLayout.CENTER);
    }

    private JPanel crearMenuLateral() {
        JPanel menu = new JPanel();
        // BoxLayout acomoda los elementos uno debajo del otro
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(220, 0)); // Ancho fijo del menú
        menu.setBackground(new Color(40, 44, 52)); // Color oscuro elegante
        menu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Título del menú
        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setForeground(Color.LIGHT_GRAY);
        lblMenu.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);

        menu.add(lblMenu);
        menu.add(Box.createRigidArea(new Dimension(0, 30))); // Espaciador vertical

        // Botones de navegación (Conectados al nombre de la carta)
        menu.add(crearBotonMenu("Inicio", "DASHBOARD"));
        menu.add(Box.createRigidArea(new Dimension(0, 10)));
        menu.add(crearBotonMenu("Gestión de Clientes", "CLIENTES"));
        menu.add(Box.createRigidArea(new Dimension(0, 10)));
        menu.add(crearBotonMenu("Gestión de Planes", "PLANES"));
        menu.add(Box.createRigidArea(new Dimension(0, 10)));
        menu.add(crearBotonMenu("Gestión de Pagos", "PAGOS"));

        // Esto empuja el botón de salir hacia el fondo de la pantalla
        menu.add(Box.createVerticalGlue());

        // Botón de Cerrar Sesión
        JButton btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.setMaximumSize(new Dimension(180, 40));
        btnSalir.addActionListener(e -> {
            // Cierra la ventana principal y vuelve a abrir el Login
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        menu.add(btnSalir);

        return menu;
    }

    private JButton crearBotonMenu(String texto, String nombreCarta) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setFocusPainted(false);

        // Al hacer clic, le decimos al CardLayout que muestre la carta correspondiente
        btn.addActionListener(e -> cardLayout.show(panelContenedor, nombreCarta));

        return btn;
    }

    // Método temporal para generar paneles de prueba (se borrará más adelante)
    private JPanel crearPanelPrueba(String texto) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}