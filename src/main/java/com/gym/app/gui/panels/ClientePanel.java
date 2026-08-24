package com.gym.app.gui.panels;

import com.gym.app.gui.dialogs.ClienteDialog;
import com.gym.app.models.Cliente;
import com.gym.app.models.Pago;
import com.gym.app.services.ClienteService;
import com.gym.app.services.PagoService;
import com.gym.app.gui.render.EstadoRenderer;
import com.gym.app.gui.render.AlertaVencimientoRenderer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientePanel extends JPanel {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnActualizar;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnDarDeBaja;
    private final ClienteService clienteService;
    private final PagoService pagoService;
    private JTextField txtBuscar;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Cliente> clientesCargados = List.of();

    public ClientePanel() {
        this.clienteService = new ClienteService();
        this.pagoService = new PagoService();
        initUI();
        cargarDatos();
    }

    // Resultado combinado de la carga en segundo plano: clientes + el vencimiento
    // de su último pago (calculado acá porque el backend no lo expone en /clientes)
    private record DatosClientes(List<Cliente> clientes, Map<Integer, String> ultimosVencimientos) {}

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- 1. TÍTULO, CONTROLES SUPERIORES Y BUSCADOR ---
        JPanel mainNorthPanel = new JPanel();
        mainNorthPanel.setLayout(new BoxLayout(mainNorthPanel, BoxLayout.Y_AXIS));

        // Panel de Título y Botones
        JPanel panelTop = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Gestión de Clientes");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnActualizar = new JButton("Actualizar Tabla");
        btnNuevo = new JButton("+ Nuevo Cliente");
        btnEditar = new JButton("Editar Cliente");
        btnDarDeBaja = new JButton("Dar de Baja");
        btnEditar.setEnabled(false);
        btnDarDeBaja.setEnabled(false);

        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnDarDeBaja);

        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(panelBotones, BorderLayout.EAST);
        panelTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        mainNorthPanel.add(panelTop);

        // Panel del Buscador
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscar = new JTextField(20);
        panelBuscar.add(new JLabel("Buscar Cliente: "));
        panelBuscar.add(txtBuscar);
        panelBuscar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        mainNorthPanel.add(panelBuscar);

        add(mainNorthPanel, BorderLayout.NORTH);

        // --- 2. TABLA DE DATOS ---
        String[] columnas = {"Nombre", "Apellido", "Teléfono", "Estado", "Vencimiento"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(25);
        tablaClientes.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        tablaClientes.getColumnModel().getColumn(3).setCellRenderer(new EstadoRenderer());
        tablaClientes.getColumnModel().getColumn(4).setCellRenderer(new AlertaVencimientoRenderer());

        sorter = new TableRowSorter<>(modeloTabla);
        tablaClientes.setRowSorter(sorter);

        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            boolean haySeleccion = tablaClientes.getSelectedRow() != -1;
            btnEditar.setEnabled(haySeleccion);
            btnDarDeBaja.setEnabled(haySeleccion);
        });

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. EVENTOS ---
        btnActualizar.addActionListener(e -> cargarDatos());

        btnNuevo.addActionListener(e -> {
            ClienteDialog dialog = new ClienteDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);

            if (dialog.isGuardadoExitoso()) {
                cargarDatos();
            }
        });

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        btnEditar.addActionListener(e -> editarClienteSeleccionado());
        btnDarDeBaja.addActionListener(e -> darDeBajaClienteSeleccionado());
    }

    private Cliente obtenerClienteSeleccionado() {
        int filaVista = tablaClientes.getSelectedRow();
        if (filaVista == -1) return null;
        int filaModelo = tablaClientes.convertRowIndexToModel(filaVista);
        return clientesCargados.get(filaModelo);
    }

    private void editarClienteSeleccionado() {
        Cliente seleccionado = obtenerClienteSeleccionado();
        if (seleccionado == null) return;

        ClienteDialog dialog = new ClienteDialog((Frame) SwingUtilities.getWindowAncestor(this), seleccionado);
        dialog.setVisible(true);

        if (dialog.isGuardadoExitoso()) {
            cargarDatos();
        }
    }

    private void darDeBajaClienteSeleccionado() {
        Cliente seleccionado = obtenerClienteSeleccionado();
        if (seleccionado == null) return;

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Confirma dar de baja a " + seleccionado.getNombreCompleto() + "?",
                "Confirmar baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        btnDarDeBaja.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                clienteService.darDeBaja(seleccionado.getId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(ClientePanel.this, "Cliente dado de baja con éxito.");
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClientePanel.this,
                            "Error al dar de baja: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnDarDeBaja.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void cargarDatos() {
        btnActualizar.setEnabled(false);
        btnActualizar.setText("Cargando...");
        btnEditar.setEnabled(false);
        btnDarDeBaja.setEnabled(false);
        modeloTabla.setRowCount(0);

        SwingWorker<DatosClientes, Void> worker = new SwingWorker<>() {
            @Override
            protected DatosClientes doInBackground() throws Exception {
                List<Cliente> clientes = clienteService.listarClientes();
                clientes.sort(Comparator.comparing(Cliente::getApellido, Comparator.nullsLast(String::compareToIgnoreCase)));

                // El backend no expone el vencimiento en /clientes: lo calculamos acá
                // tomando, de todos los pagos, el de mayor fechaVencimiento por cliente.
                Map<Integer, String> ultimosVencimientos = new HashMap<>();
                for (Pago pago : pagoService.listarPagos()) {
                    if (pago.getCliente() == null || pago.getFechaVencimiento() == null) continue;
                    Integer clienteId = pago.getCliente().getId();
                    String actual = ultimosVencimientos.get(clienteId);
                    if (actual == null || LocalDate.parse(pago.getFechaVencimiento().substring(0, 10))
                            .isAfter(LocalDate.parse(actual.substring(0, 10)))) {
                        ultimosVencimientos.put(clienteId, pago.getFechaVencimiento());
                    }
                }

                return new DatosClientes(clientes, ultimosVencimientos);
            }

            @Override
            protected void done() {
                try {
                    DatosClientes datos = get();
                    clientesCargados = datos.clientes();
                    for (Cliente c : datos.clientes()) {
                        String vencimiento = datos.ultimosVencimientos().get(c.getId());
                        modeloTabla.addRow(new Object[]{
                                c.getNombre(),
                                c.getApellido(),
                                c.getTelefono(),
                                c.getEstado(),
                                vencimiento != null ? vencimiento.substring(0, 10) : "---"
                        });
                    }
                    filtrar(); // Aplicar filtro después de cargar datos
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClientePanel.this,
                            "Error al cargar clientes: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnActualizar.setEnabled(true);
                    btnActualizar.setText("Actualizar Tabla");
                }
            }
        };
        worker.execute();
    }

    private void filtrar() {
        String texto = txtBuscar.getText();
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null); // Si está vacío, muestra todo
        } else {
            // El "(?i)" hace que la búsqueda ignore mayúsculas y minúsculas
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }
}
