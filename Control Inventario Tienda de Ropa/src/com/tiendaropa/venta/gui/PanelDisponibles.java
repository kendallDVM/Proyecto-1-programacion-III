package com.tiendaropa.venta.gui;


import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.modelo.LineaCarrito;
import com.tiendaropa.venta.servicio.ServicioBusqueda;
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
        modeloTabla.setRowCount(0);

        // Verificar que panelBusqueda está conectado
        if (panelBusqueda == null) {
            return;
        }

        // Obtener filtros del panel de búsqueda
        String tipo = panelBusqueda.getTipo();
        String talla = panelBusqueda.getTalla();
        double precioMin = panelBusqueda.getPrecioMin();
        double precioMax = panelBusqueda.getPrecioMax();

        // Obtener servicio de búsqueda
        ServicioBusqueda servicio = panelBusqueda.getServicioBusqueda();

        if (servicio == null) {
            return;
        }

        // Realizar búsqueda con los filtros
        List<Prenda> prendas = servicio.buscarAvanzado(tipo, talla, precioMin, precioMax);

        // Agregar cada prenda a la tabla
        for (Prenda prenda : prendas) {
            agregarFilaPrenda(prenda);
        }
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

        // Obtener el código de la prenda seleccionada
        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        // Buscar la prenda real en el repositorio mediante el servicio
        Prenda prendaSeleccionada = panelBusqueda.getServicioBusqueda()
                .obtenerDisponibles()
                .stream()
                .filter(prenda -> prenda.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);

        // Verificar que la prenda exista
        if (prendaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró la prenda seleccionada",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear la línea del carrito
        LineaCarrito linea = new LineaCarrito(prendaSeleccionada);

        // Agregar la línea al carrito
        boolean agregada = carrito.agregarLinea(linea);

        if (agregada) {
            JOptionPane.showMessageDialog(this,
                    "Prenda agregada: " + codigo,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            // Actualizar panel del carrito
            ventanaCarrito.actualizarCarrito();

        } else {
            JOptionPane.showMessageDialog(this,
                    "La prenda ya se encuentra en el carrito",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
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
                String.format("¢%,.2f", prenda.getPrecio())
        };
        modeloTabla.addRow(fila);
    }

    // ========== GETTERS ==========

    /**
     * Devuelve el índice de la fila seleccionada en la tabla de prendas.
     *
     * @return índice de la fila seleccionada, o {@code -1} si no hay selección.
     */
    public int getPrendaSeleccionada() {
        return tblPrendas.getSelectedRow();
    }

    /**
     * Establece el panel de búsqueda del que se obtienen los filtros.
     *
     * @param panelBusqueda panel de búsqueda a asociar.
     */
    public void setPanelBusqueda(PanelBusqueda panelBusqueda) {
        this.panelBusqueda = panelBusqueda;
    }
}
