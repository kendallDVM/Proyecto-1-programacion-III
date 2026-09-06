package com.tiendaropa.venta.gui;


import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.modelo.LineaCarrito;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


/**
 * Panel que muestra tabla de prendas disponibles y permite agregar al carrito.
 --------------------------------------------------------------------------
 * Obtiene las prendas del servicio de búsqueda según los filtros.
 * Permite seleccionar y agregar prendas al carrito.
 ------------------------
 *author Liseth Briones
 */
public class PanelDisponibles extends JPanel{

    // ========== ATRIBUTOS ==========

    private Carrito carrito;                    // Referencia al carrito
    private VentanaCarrito ventanaCarrito;      // Referencia a ventana principal
    private PanelBusqueda panelBusqueda;        // Referencia al panel de búsqueda

    private JTable tblPrendas;                  // Tabla de prendas
    private DefaultTableModel modeloTabla;      // Modelo de datos de la tabla
    private JButton btnAgregar;                 // Botón para agregar al carrito



    // ========== CONSTRUCTOR ==========

    /**
     * Crea el panel de disponibles.
     *
     * @param carrito Carrito del cliente
     * @param ventanaCarrito Referencia a ventana principal
     */
    public PanelDisponibles(Carrito carrito, VentanaCarrito ventanaCarrito) {
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

    /**
     * Configura las propiedades básicas del panel.
     */
    private void configurarPanel() {
        // Usar BorderLayout
        setLayout(new BorderLayout(5, 5));

        // Fondo blanco
        setBackground(Color.WHITE);

        // Borde con título
        setBorder(BorderFactory.createTitledBorder("Prendas Disponibles"));
    }


    // ========== CREACIÓN DE COMPONENTES ==========

    /**
     * Crea la tabla y el botón de agregar.
     */

    private void crearComponentes() {
        // CREAR TABLA
        crearTabla();

        // BOTÓN AGREGAR
        btnAgregar = new JButton("Agregar al Carrito");
        btnAgregar.addActionListener(e -> agregarAlCarrito());
    }

    /**
     * Crea la tabla con columnas de prendas.
     */
    private void crearTabla() {
        // Nombres de columnas
        String[] columnas = {"Código", "Tipo", "Talla", "Estado", "Precio (¢)"};

        // Crear modelo de tabla vacío
        modeloTabla = new DefaultTableModel(columnas, 0) {
            // Hacer la tabla no editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        // Crear tabla con el modelo
        tblPrendas = new JTable(modeloTabla);

        // Configurar ancho de columnas
        tblPrendas.getColumnModel().getColumn(0).setPreferredWidth(80);   // Código
        tblPrendas.getColumnModel().getColumn(1).setPreferredWidth(100);  // Tipo
        tblPrendas.getColumnModel().getColumn(2).setPreferredWidth(60);   // Talla
        tblPrendas.getColumnModel().getColumn(3).setPreferredWidth(100);  // Estado
        tblPrendas.getColumnModel().getColumn(4).setPreferredWidth(100);  // Precio

        // Permitir solo seleccionar una fila a la vez
        tblPrendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    // ========== ORGANIZACIÓN DE COMPONENTES ==========

    /**
     * Organiza los componentes en el panel.
     */
    private void organizarComponentes() {
        // Tabla en el centro (con scroll)
        JScrollPane scrollPane = new JScrollPane(tblPrendas);
        add(scrollPane, BorderLayout.CENTER);

        // Botón en la parte inferior
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnAgregar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    // ========== LÓGICA DE BÚSQUEDA Y RECARGA ==========

    /**
     * Recarga la tabla con los resultados de la búsqueda.
     * Se llama desde VentanaCarrito cuando presionan "Buscar".
     */
    public void recargar() {
        // Limpiar tabla actual
        modeloTabla.setRowCount(0);

        // Obtener el panel de búsqueda (lo crearemos cuando integremos)
        // Por ahora, se mantiene vacío
        // Esto se implementará cuando PanelBusqueda esté completamente integrado
    }

    /**
     * Agrega la prenda seleccionada al carrito.
     */
    private void agregarAlCarrito() {
        // Verificar que hay una fila seleccionada
        int filaSeleccionada = tblPrendas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor selecciona una prenda",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Obtener datos de la prenda seleccionada
        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        // Nota: Aquí obtendremos la Prenda real del servicio de búsqueda
        // Por ahora solo mostramos un mensaje

        JOptionPane.showMessageDialog(this,
                "Prenda agregada: " + codigo,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        // Actualizar panel del carrito
        ventanaCarrito.actualizarCarrito();
    }

    // ========== MÉTODO PARA AGREGAR FILAS A LA TABLA ==========

    /**
     * Agrega una fila a la tabla con datos de una prenda.
     *
     * @param prenda Prenda a mostrar en la tabla
     */
    private void agregarFilaPrenda(Prenda prenda) {
        Object[] fila = {
                prenda.getCodigo(),
                prenda.getTipo().toString(),
                prenda.getTalla().toString(),
                prenda.getEstado().toString(),
                String.format("¢%,.0f", prenda.getPrecio())
        };
        modeloTabla.addRow(fila);
    }

    // ========== GETTERS ==========

    /**
     * Retorna la prenda seleccionada en la tabla.
     */
    public int getPrendaSeleccionada() {
        return tblPrendas.getSelectedRow();
    }

}
