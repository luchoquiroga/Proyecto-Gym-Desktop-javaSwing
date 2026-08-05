package com.gym.app.gui.render;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class EstadoRenderer extends DefaultTableCellRenderer {

    private static final Color BG_ACTIVO = new Color(220, 252, 231);    // Verde claro
    private static final Color FG_ACTIVO = new Color(22, 101, 52);      // Verde oscuro

    private static final Color BG_MOROSO = new Color(254, 243, 195);    // Amarillo/Naranja claro
    private static final Color FG_MOROSO = new Color(180, 83, 9);       // Naranja oscuro

    private static final Color BG_INACTIVO = new Color(243, 244, 246);  // Gris claro
    private static final Color FG_INACTIVO = new Color(107, 114, 128);  // Gris oscuro

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(SwingConstants.CENTER);

        if (isSelected) {
            return c;
        }

        c.setBackground(table.getBackground());
        c.setForeground(table.getForeground());

        if (value != null) {
            String estado = value.toString().trim().toLowerCase();
            switch (estado) {
                case "moroso":
                    c.setBackground(BG_MOROSO);
                    c.setForeground(FG_MOROSO);
                    break;
                case "inactivo":
                    c.setBackground(BG_INACTIVO);
                    c.setForeground(FG_INACTIVO);
                    break;
                case "activo":
                    c.setBackground(BG_ACTIVO);
                    c.setForeground(FG_ACTIVO);
                    break;
            }
        }

        return c;
    }
}