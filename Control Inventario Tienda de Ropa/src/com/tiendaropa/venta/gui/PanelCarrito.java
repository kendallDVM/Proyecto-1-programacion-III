package com.tiendaropa.venta.gui;

import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.modelo.LineaCarrito;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


/**
 * Panel que muestra el carrito del cliente con totales.
 ------------------------------------------------------
 * Muestra tabla de prendas en carrito.
 * Permite eliminar prendas del carrito.
 * Muestra subtotal, IVA y total a pagar.
 ----------------------------------------
 * @author Liseth Briones
 */
public class PanelCarrito extends JPanel {


    // ========== ATRIBUTOS ==========

    private Carrito carrito;                    // Referencia al carrito
    private VentanaCarrito ventanaCarrito;      // Referencia a ventana principal

    private JTable tblCarrito;                  // Tabla del carrito
    private DefaultTableModel modeloTabla;      // Modelo de datos
    private JButton btnEliminar;                // Botón eliminar

    private JLabel lblSubtotal;                 // Etiqueta subtotal
    private JLabel lblIVA;                      // Etiqueta IVA (13%)
    private JLabel lblTotal;                    // Etiqueta total


    // ========== CONSTRUCTOR ==========

    /**
     * Crea el panel del carrito.
     *
     * @param carrito Carrito del cliente
     * @param ventanaCarrito Referencia a ventana principal
     */
    public PanelCarrito(Carrito carrito, VentanaCarrito ventanaCarrito) {
        this.carrito = carrito;
        this.ventanaCarrito = ventanaCarrito;

        // Configurar el panel
        configurarPanel();

        // Crear componentes
        crearComponentes();

        // Organizar componentes
        organizarComponentes();
    }

                 // ========== CONFIGURACIÓN DEL PANEL ==========
    //Configura las propiedades básicas del panel.
    private void configurarPanel() {
        // Usar BorderLayout
        setLayout(new BorderLayout(5, 5));

        // Fondo blanco
        setBackground(Color.WHITE);

        // Borde con título
        setBorder(BorderFactory.createTitledBorder("Carrito de Compras"));
    }


    // ========== CREACIÓN DE COMPONENTES ==========

    /**
     * Crea todos los componentes del panel.
     */
    private void crearComponentes() {
        // CREAR TABLA
        crearTabla();

        // BOTÓN ELIMINAR
        btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.addActionListener(e -> eliminarDelCarrito());

        // ETIQUETAS DE TOTALES
        lblSubtotal = new JLabel("Subtotal: ¢0");
        lblIVA = new JLabel("IVA (13%): ¢0");
        lblTotal = new JLabel("Total: ¢0");

        // Hacer las etiquetas más visibles
        lblSubtotal.setFont(new Font("Arial", Font.PLAIN, 12));
        lblIVA.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(Color.RED);
    }

    /**
     * Crea la tabla del carrito.
     */
    private void crearTabla() {
        // Nombres de columnas
        String[] columnas = {"Código", "Tipo", "Talla", "Precio (¢)"};

        // Crear modelo de tabla vacío
        modeloTabla = new DefaultTableModel(columnas, 0) {
            // Hacer la tabla no editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Crear tabla con el modelo
        tblCarrito = new JTable(modeloTabla);

        // Configurar ancho de columnas
        tblCarrito.getColumnModel().getColumn(0).setPreferredWidth(80);   // Código
        tblCarrito.getColumnModel().getColumn(1).setPreferredWidth(100);  // Tipo
        tblCarrito.getColumnModel().getColumn(2).setPreferredWidth(60);   // Talla
        tblCarrito.getColumnModel().getColumn(3).setPreferredWidth(100);  // Precio

        // Permitir solo seleccionar una fila a la vez
        tblCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    // ========== ORGANIZACIÓN DE COMPONENTES ==========

    /**
     * Organiza los componentes en el panel.
     */
    private void organizarComponentes() {
        // Tabla en el centro (con scroll)
        JScrollPane scrollPane = new JScrollPane(tblCarrito);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botón y totales
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());

        // Panel izquierda: botón eliminar
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnEliminar);
        panelInferior.add(panelBotones, BorderLayout.WEST);

        // Panel derecha: totales
        JPanel panelTotales = new JPanel();
        panelTotales.setLayout(new BoxLayout(panelTotales, BoxLayout.Y_AXIS));
        panelTotales.add(lblSubtotal);
        panelTotales.add(Box.createVerticalStrut(5));
        panelTotales.add(lblIVA);
        panelTotales.add(Box.createVerticalStrut(5));
        panelTotales.add(lblTotal);
        panelInferior.add(panelTotales, BorderLayout.EAST);

        add(panelInferior, BorderLayout.SOUTH);
    }

    // ========== LÓGICA DE RECARGA ==========

    /**
     * Recarga la tabla con los items actuales del carrito.
     * Actualiza también los totales.
     */
    public void recargar() {

        System.out.println("\n=== RECARGANDO CARRITO ===");

        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Obtener líneas del carrito
        List<LineaCarrito> lineas = carrito.getLineas();

        System.out.println("Items en carrito: " + lineas.size());

        // Agregar cada línea a la tabla
        for (LineaCarrito linea : lineas) {

            System.out.println("  - " + linea.getPrenda().getCodigo() + ": ¢" + linea.getSubtotal());

            agregarFilaCarrito(linea);
        }

        // Actualizar totales
        actualizarTotales();

        System.out.println("===================================\n");
    }


    /**
     * Agrega una fila a la tabla con datos de una línea del carrito.
     */
    private void agregarFilaCarrito(LineaCarrito linea) {
        Object[] fila = {
                linea.getPrenda().getCodigo(),
                linea.getPrenda().getTipo().toString(),
                linea.getPrenda().getTalla().toString(),
                String.format("¢%,.2f", linea.getSubtotal())
        };
        modeloTabla.addRow(fila);
    }

    /**
     * Actualiza las etiquetas de subtotal, IVA y total.
     */
    private void actualizarTotales() {
        // Calcular subtotal
        double subtotal = carrito.calcularSubtotal();

        // Calcular IVA (13%)
        double iva = subtotal * 0.13;

        // Calcular total
        double total = subtotal + iva;

        // Actualizar etiquetas
        lblSubtotal.setText(String.format("Subtotal: ¢%,.2f", subtotal));
        lblIVA.setText(String.format("IVA (13%%): ¢%,.2f", iva));
        lblTotal.setText(String.format("Total: ¢%,.2f", total));
    }


    // ========== LÓGICA DE ELIMINAR ==========

    /**
     * Elimina la prenda seleccionada del carrito.
     */
    private void eliminarDelCarrito() {
        // Verificar que hay una fila seleccionada
        int filaSeleccionada = tblCarrito.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor selecciona una prenda para eliminar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener código de la prenda
        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        // Eliminar del carrito
        boolean eliminado = carrito.eliminarLinea(codigo);

        if (eliminado) {
            JOptionPane.showMessageDialog(this,
                    "Prenda eliminada del carrito",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            // Recargar tabla
            recargar();

            // Actualizar panel de búsqueda (si fuera necesario)
            ventanaCarrito.actualizarDisponibles();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar la prenda",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // ========== GETTERS ==========

    /**
     * Retorna el total a pagar (con IVA).
     */
    public double getTotalAPagar() {
        double subtotal = carrito.calcularSubtotal();
        return subtotal * 1.13;  // Subtotal + 13% IVA
    }

    /**
     * Retorna el cantidad de items en el carrito.
     */
    public int getCantidadItems() {
        return carrito.obtenerCantidadItems();
    }
}
