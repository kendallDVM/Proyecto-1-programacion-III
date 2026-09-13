package com.tiendaropa.venta.gui;

import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.modelo.LineaCarrito;
import com.tiendaropa.venta.modelo.Venta;
import com.tiendaropa.venta.repositorio.IGestionVentas;
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

    private IGestionVentas gestionVentas;        // Historial de ventas

    private JTable tblCarrito;                  // Tabla del carrito
    private DefaultTableModel modeloTabla;      // Modelo de datos
    private JButton btnEliminar;                // Botón eliminar
    private JButton btnFinalizar;               // Botón finalizar compra

    private JLabel lblSubtotal;                 // Etiqueta subtotal
    private JLabel lblIVA;                      // Etiqueta IVA (13%)
    private JLabel lblTotal;                    // Etiqueta total


    // ========== CONSTRUCTOR ==========

    /**
     * Crea el panel del carrito.
     *
     * @param carrito Carrito del cliente
     * @param ventanaCarrito Referencia a ventana principal
     * @param gestionVentas Historial de ventas
     */
    public PanelCarrito(Carrito carrito, VentanaCarrito ventanaCarrito,
                        IGestionVentas gestionVentas) {
        this.carrito = carrito;
        this.ventanaCarrito = ventanaCarrito;
        this.gestionVentas = gestionVentas;

        // Configurar el panel
        configurarPanel();

        // Crear componentes
        crearComponentes();

        // Organizar componentes
        organizarComponentes();
    }

                 // ========== CONFIGURACIÓN DEL PANEL ==========

    /**
     * Configura las propiedades básicas del panel.
     */
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

        // BOTÓN FINALIZAR COMPRA
        btnFinalizar = new JButton("Finalizar Compra");
        btnFinalizar.addActionListener(e -> finalizarCompra());

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

        // Panel izquierda: botones eliminar, finalizar y volver al catálogo
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnEliminar);
        panelBotones.add(btnFinalizar);

        JButton btnAdmin = new JButton("Administrador");
        btnAdmin.addActionListener(e -> ventanaCarrito.abrirAdministracion());
        panelBotones.add(btnAdmin);

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

        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Obtener líneas del carrito
        List<LineaCarrito> lineas = carrito.getLineas();

        // Agregar cada línea a la tabla
        for (LineaCarrito linea : lineas) {
            agregarFilaCarrito(linea);
        }

        // Actualizar totales
        actualizarTotales();
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


    // ========== LÓGICA DE FINALIZAR COMPRA ==========

    /**
     * Procesa el checkout del carrito: valida que no esté vacío, genera la
     * factura, registra la venta, retira las prendas vendidas del catálogo y
     * refresca la interfaz.
     */
    private void finalizarCompra() {
        // Validar que el carrito no esté vacío
        if (carrito.estaVacio()) {
            JOptionPane.showMessageDialog(this,
                    "El carrito está vacío. Agregue prendas antes de finalizar.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Generar código de factura única
            String codigoFactura = ventanaCarrito.generarCodigoFactura();

            // Convertir el carrito en una venta (checkout) y limpiarlo
            Venta venta = carrito.checkout(codigoFactura);

            // Registrar la venta en el historial
            gestionVentas.registrar(venta);

            // Retirar permanentemente las prendas vendidas del catálogo
            ventanaCarrito.retirarPrendasVendidas(venta);

            // Mostrar resumen de la factura
            String mensaje = String.format(
                    "Venta completada.%n%nFactura: %s%nPrendas: %d%nSubtotal: ¢%,.2f%nIVA (13%%): ¢%,.2f%nTotal: ¢%,.2f",
                    venta.getCodigoFactura(),
                    venta.getCantidadPrendas(),
                    venta.getSubtotal(),
                    venta.getIva(),
                    venta.getTotal());
            JOptionPane.showMessageDialog(this, mensaje, "Venta exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refrescar carrito y disponibles
            recargar();
            ventanaCarrito.actualizarDisponibles();
            ventanaCarrito.actualizarReportes();

        } catch (IllegalStateException excepcion) {
            JOptionPane.showMessageDialog(this,
                    excepcion.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
