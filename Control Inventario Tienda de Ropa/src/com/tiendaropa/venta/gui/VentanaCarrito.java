package com.tiendaropa.venta.gui;


import com.tiendaropa.catalogo.gui.VentanaCatalogo;
import com.tiendaropa.catalogo.repositorio.IGestionPrendas;
import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.modelo.Venta;
import com.tiendaropa.venta.repositorio.IGestionVentas;
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

    private IGestionPrendas gestionPrendas;   // Repositorio de prendas (compartido con la administración)
    private IGestionVentas gestionVentas;     // Repositorio de ventas (compartido)
    private Carrito carrito;                     // Carrito del cliente
    private ServicioBusqueda servicioBusqueda;   // Servicio de búsqueda

    private PanelBusqueda panelBusqueda;      // Panel de búsqueda
    private PanelDisponibles panelDisponibles; // Panel de prendas disponibles
    private PanelCarrito panelCarrito;        // Panel del carrito
    private PanelReportes panelReportes;      // Panel de reportes de ventas


    /**
     * Crea la ventana principal (tienda) con inyección de dependencias.
     *
     * @param gestionPrendas Repositorio de prendas (compartido con la administración).
     * @param gestionVentas  Repositorio de ventas (historial).
     */
    public VentanaCarrito(IGestionPrendas gestionPrendas, IGestionVentas gestionVentas) {

        this.gestionPrendas = gestionPrendas;
        this.gestionVentas = gestionVentas;
        this.carrito = new Carrito();
        this.servicioBusqueda = new ServicioBusqueda(gestionPrendas);




        // Configurar la ventana principal
        configurarVentana();

        // Crear los paneles
        crearPaneles();

        // Organizar los paneles en la ventana
        organizarLayout();

        // Cargar las prendas disponibles al abrir la ventana
        actualizarDisponibles();
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

        // La tienda es la ventana principal: al cerrarla termina la aplicación
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

        panelCarrito = new PanelCarrito(carrito, this, gestionVentas);

        panelReportes = new PanelReportes(gestionVentas);
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

        // Panel de reportes en la parte inferior (South), con alto acotado
        panelReportes.setPreferredSize(new java.awt.Dimension(0, 180));
        add(panelReportes, java.awt.BorderLayout.SOUTH);
    }

    // ========== MÉTODOS DE ACTUALIZACIÓN ==========

    /**
     * Actualiza el panel de disponibles con nueva búsqueda.
     * Se llama desde PanelBusqueda cuando el usuario hace clic en "Buscar".
     */
    public void actualizarDisponibles() {
        panelDisponibles.recargar(false);
    }

    /**
     * Refresca las prendas disponibles mostrando el resultado de la búsqueda.
     * Se usa cuando el usuario pulsa "Filtrar".
     */
    public void actualizarDisponiblesConMensaje() {
        panelDisponibles.recargar(true);
    }

    /**
     * Actualiza el panel del carrito.
     * Se llama desde PanelDisponibles cuando agrega/quita una prenda.
     */
    public void actualizarCarrito() {
        panelCarrito.recargar();
    }

    /**
     * Indica si el carrito está registrando una venta en segundo plano.
     * Se usa para impedir agregar prendas mientras tanto.
     *
     * @return {@code true} si hay una venta en curso.
     */
    public boolean hayVentaEnCurso() {
        return panelCarrito.isVentaEnCurso();
    }

    /**
     * Actualiza el panel de reportes con el historial de ventas actual.
     * Se llama tras confirmar una venta.
     */
    public void actualizarReportes() {
        panelReportes.recargar();
    }

    /**
     * Genera el siguiente código de factura a partir de las ventas guardadas.
     *
     * <p>Busca el número más alto entre las facturas registradas y le suma uno,
     * para que la numeración continúe aunque se cierre la aplicación.</p>
     *
     * @return código de factura con formato {@code FAC-0001}, {@code FAC-0002}, etc.
     * @throws com.tiendaropa.infraestructura.PersistenciaException si no se
     *         pueden consultar las ventas registradas.
     */
    public String generarCodigoFactura() {
        int mayor = 0;
        for (Venta venta : gestionVentas.obtenerTodas()) {
            String codigo = venta.getCodigoFactura();
            if (codigo != null && codigo.startsWith("FAC-")) {
                try {
                    mayor = Math.max(mayor, Integer.parseInt(codigo.substring(4)));
                } catch (NumberFormatException excepcion) {
                    // Se ignoran los códigos que no siguen el formato FAC-####
                }
            }
        }
        return String.format("FAC-%04d", mayor + 1);
    }

    /**
     * Abre la administración (previa autenticación), ocultando la tienda.
     */
    public void abrirAdministracion() {
        if (verificarCredenciales()) {
            setVisible(false);
            VentanaCatalogo ventanaAdmin = new VentanaCatalogo(gestionPrendas, this::mostrarTienda);
            ventanaAdmin.setVisible(true);
        }
    }

    /**
     * Vuelve a mostrar la tienda y refresca las prendas disponibles.
     */
    private void mostrarTienda() {
        setVisible(true);
        actualizarDisponibles();
    }

    /**
     * Autenticación demostrativa (no es un sistema de seguridad real).
     *
     * @return {@code true} si las credenciales admin/1234 son correctas.
     */
    private boolean verificarCredenciales() {
        JPanel panel = new JPanel(new java.awt.GridLayout(2, 2, 8, 8));
        JTextField campoUsuario = new JTextField(10);
        JPasswordField campoPassword = new JPasswordField(10);
        panel.add(new JLabel("Usuario:"));
        panel.add(campoUsuario);
        panel.add(new JLabel("Contraseña:"));
        panel.add(campoPassword);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Acceso de administrador",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return false;
        }
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoPassword.getPassword());
        boolean valido = "admin".equals(usuario) && "1234".equals(contrasena);
        if (!valido) {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos.",
                    "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
        return valido;
    }

}
