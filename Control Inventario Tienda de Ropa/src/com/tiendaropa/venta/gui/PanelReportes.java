package com.tiendaropa.venta.gui;

import com.tiendaropa.venta.modelo.Venta;
import com.tiendaropa.venta.repositorio.IGestionVentas;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Panel que muestra el historial de ventas realizadas y permite buscar una
 * venta específica por su código de factura.
 *
 * <p>Es el módulo de reportes exigido por el flujo del tema: un listado en
 * tabla de todas las ventas registradas, con filtro por código de factura.</p>
 */
public class PanelReportes extends JPanel {

    /** Historial de ventas del que se obtienen los datos a mostrar. */
    private final IGestionVentas gestionVentas;

    /** Campo de texto para buscar por código de factura. */
    private JTextField campoFactura;

    /** Tabla que visualiza las ventas registradas. */
    private JTable tabla;

    /** Modelo de la tabla con las columnas del reporte. */
    private DefaultTableModel modelo;

    /**
     * Construye el panel, inicializa sus componentes y carga el historial.
     *
     * @param gestionVentas historial de ventas contra el que se consultan los datos.
     */
    public PanelReportes(IGestionVentas gestionVentas) {
        this.gestionVentas = gestionVentas;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Reporte de Ventas"));

        add(construirBarraBusqueda(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);

        actualizarTabla(gestionVentas.obtenerTodas());
    }

    /**
     * Construye la barra superior con el campo de búsqueda por factura y sus botones.
     *
     * @return panel con el campo de factura y los botones Buscar / Limpiar.
     */
    private JPanel construirBarraBusqueda() {
        JPanel barra = new JPanel(new BorderLayout(8, 8));

        campoFactura = new JTextField(15);
        campoFactura.setToolTipText("Buscar venta por código de factura");
        campoFactura.addActionListener(evento -> buscarPorFactura());

        JButton botonBuscar = new JButton("Buscar Factura");
        botonBuscar.addActionListener(evento -> buscarPorFactura());

        JButton botonLimpiar = new JButton("Ver Todas");
        botonLimpiar.addActionListener(evento -> {
            campoFactura.setText("");
            actualizarTabla(gestionVentas.obtenerTodas());
        });

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonBuscar);
        panelBotones.add(botonLimpiar);

        barra.add(new JLabel("Código factura: "), BorderLayout.WEST);
        barra.add(campoFactura, BorderLayout.CENTER);
        barra.add(panelBotones, BorderLayout.EAST);
        return barra;
    }

    /**
     * Construye la tabla y su modelo de columnas para las ventas.
     *
     * @return panel con scroll que contiene la tabla de ventas.
     */
    private JScrollPane construirTabla() {
        modelo = new DefaultTableModel(new Object[]{
                "Factura", "Fecha", "Prendas", "Subtotal", "IVA", "Total"
        }, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        return new JScrollPane(tabla);
    }

    /**
     * Busca una venta por el código de factura introducido y la muestra en la tabla.
     *
     * <p>Si el campo está vacío muestra todo el historial; si no encuentra una
     * venta con ese código muestra un mensaje informativo.</p>
     */
    private void buscarPorFactura() {
        String codigo = campoFactura.getText().trim();
        if (codigo.isEmpty()) {
            actualizarTabla(gestionVentas.obtenerTodas());
            return;
        }
        Venta venta = gestionVentas.buscarPorFactura(codigo);
        if (venta == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró ninguna venta con la factura '" + codigo + "'.",
                    "Reporte",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        actualizarTabla(List.of(venta));
    }

    /**
     * Carga en la tabla una lista de ventas.
     *
     * @param ventas lista de ventas a visualizar.
     */
    private void actualizarTabla(List<Venta> ventas) {
        modelo.setRowCount(0);
        for (Venta venta : ventas) {
            modelo.addRow(new Object[]{
                    venta.getCodigoFactura(),
                    venta.getFechaFormateada(),
                    venta.getCantidadPrendas(),
                    String.format("¢%,.2f", venta.getSubtotal()),
                    String.format("¢%,.2f", venta.getIva()),
                    String.format("¢%,.2f", venta.getTotal())
            });
        }
    }

    /**
     * Recarga la tabla con el historial de ventas actual.
     * Debe invocarse tras confirmar una nueva venta.
     */
    public void recargar() {
        actualizarTabla(gestionVentas.obtenerTodas());
    }
}