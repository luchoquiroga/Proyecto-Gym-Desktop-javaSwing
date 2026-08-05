package com.gym.app.gui.render;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class AlertaVencimientoRenderer extends DefaultTableCellRenderer {

    private static final Color BG_VENCIDO = new Color(254, 226, 226);     // Rojo claro
    private static final Color FG_VENCIDO = new Color(153, 27, 27);       // Texto rojo oscuro

    private static final Color BG_POR_VENCER = new Color(254, 249, 195);  // Amarillo claro
    private static final Color FG_POR_VENCER = new Color(133, 77, 14);    // Texto amarillo oscuro

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        if (value != null && !value.toString().trim().isEmpty() && !value.toString().equals("---")) {
            try {
                String fechaLimpia = value.toString().trim().substring(0, 10);
                LocalDate fechaVencimiento = LocalDate.parse(fechaLimpia, formatter);
                LocalDate hoy = LocalDate.now();

                long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaVencimiento);

                if (diasRestantes <= 0) {
                    c.setBackground(BG_VENCIDO);
                    c.setForeground(FG_VENCIDO);
                } else if (diasRestantes <= 3) {
                    c.setBackground(BG_POR_VENCER);
                    c.setForeground(FG_POR_VENCER);
                }
            } catch (Exception e) {
                // Si falla el parseo, queda normal
            }
        }

        return c;
    }
}