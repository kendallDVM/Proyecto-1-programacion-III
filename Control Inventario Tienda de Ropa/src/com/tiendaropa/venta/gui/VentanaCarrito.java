package com.tiendaropa.venta.gui;


import com.tiendaropa.catalogo.repositorio.IGestionPrendas;
import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.servicio.ServicioBusqueda;
import javax.swing.*;


/**
 * Ventana principal del módulo de búsqueda y carrito.
 ----------------------------------------------------
 * Organiza tres paneles:
 * - PanelBusqueda: filtros de búsqueda
 * - PanelDisponibles: tabla de prendas disponibles
 * - PanelCarrito: tabla del carrito y totales
 */
public class VentanaCarrito extends JFrame {

    private IGestionPrendas gestionPrendas;   // Conexión con Módulo 1
    private Carrito carrito;                  // Carrito del cliente
    private ServicioBusqueda servicioBusqueda; // Servicio de búsqueda

    private PanelBusqueda panelBusqueda;      // Panel de búsqueda
    private PanelDisponibles panelDisponibles; // Panel de prendas disponibles
    private PanelCarrito panelCarrito;        // Panel del carrito


    /**
     * Crea la ventana principal con inyección de dependencias.
     *
     * @param gestionPrendas Implementación de IGestionPrendas (del Módulo 1)
     */
    public VentanaCarrito(IGestionPrendas gestionPrendas) {

        this.gestionPrendas = gestionPrendas;
        this.carrito = new Carrito();
        this.servicioBusqueda = new ServicioBusqueda(gestionPrendas);




        // Configurar la ventana principal
        configurarVentana();

        // Crear los paneles
        crearPaneles();

        // Organizar los paneles en la ventana
        organizarLayout();
    }


    /**
     * Configura las propiedades básicas de la ventana JFrame.
     */
    private void configurarVentana() {
        // Título de la ventana
        setTitle("Búsqueda y Carrito - Tienda de Ropa");

        // Tamaño inicial de la ventana (ancho x alto)
        setSize(1200, 700);

        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);

        // Cerrar la aplicación cuando se cierra la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Usar BorderLayout para organizar componentes
        setLayout(new java.awt.BorderLayout(10, 10));

        // Margen interno de la ventana
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }


    // ========== CREACIÓN DE PANELES ==========

    /**
     * Crea los tres paneles que componen la ventana.
     */
    private void crearPaneles() {
        panelBusqueda = new PanelBusqueda(servicioBusqueda, this);

        panelDisponibles = new PanelDisponibles(carrito, this);
        panelDisponibles.setPanelBusqueda(panelBusqueda);  // ← AGREGA ESTA LÍNEA

        panelCarrito = new PanelCarrito(carrito, this);
    }

    // ========== ORGANIZACIÓN DEL LAYOUT ==========

    /**
     * Organiza los paneles en la ventana usando BorderLayout.
     */
    private void organizarLayout() {
        // Panel de búsqueda en la parte SUPERIOR (North)
        add(panelBusqueda, java.awt.BorderLayout.NORTH);

        // Panel central (Center) contiene disponibles y carrito lado a lado
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new java.awt.GridLayout(1, 2, 10, 0));  // 1 fila, 2 columnas
        panelCentral.add(panelDisponibles);
        panelCentral.add(panelCarrito);
        add(panelCentral, java.awt.BorderLayout.CENTER);
    }

    // ========== MÉTODOS DE ACTUALIZACIÓN ==========

    /**
     * Actualiza el panel de disponibles con nueva búsqueda.
     * Se llama desde PanelBusqueda cuando el usuario hace clic en "Buscar".
     */
    public void actualizarDisponibles() {
        panelDisponibles.recargar();
    }

    /**
     * Actualiza el panel del carrito.
     * Se llama desde PanelDisponibles cuando agrega/quita una prenda.
     */
    public void actualizarCarrito() {
        panelCarrito.recargar();
    }

}
