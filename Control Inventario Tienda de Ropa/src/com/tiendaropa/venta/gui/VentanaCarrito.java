package com.tiendaropa.venta.gui;


import com.tiendaropa.catalogo.gui.VentanaCatalogo;
import com.tiendaropa.catalogo.repositorio.IGestionPrendas;
import com.tiendaropa.cliente.repositorio.IGestionClientes;
import com.tiendaropa.seguridad.modelo.Usuario;
import com.tiendaropa.seguridad.servicio.ServicioAutenticacion;
import com.tiendaropa.sincronizacion.ServicioSincronizacion;
import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.modelo.Venta;
import com.tiendaropa.venta.repositorio.IGestionVentas;
import com.tiendaropa.venta.servicio.ServicioBusqueda;
import javax.swing.*;
import java.util.concurrent.ExecutionException;


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
    private IGestionClientes gestionClientes; // Consulta de clientes para las ventas
    private ServicioAutenticacion servicioAutenticacion; // Verificación del acceso de administrador
    private Carrito carrito;                     // Carrito del cliente
    private ServicioBusqueda servicioBusqueda;   // Servicio de búsqueda

    private PanelBusqueda panelBusqueda;      // Panel de búsqueda
    private PanelDisponibles panelDisponibles; // Panel de prendas disponibles
    private PanelCarrito panelCarrito;        // Panel del carrito
    private PanelReportes panelReportes;      // Panel de reportes de ventas

    /** Título base de la ventana, al que se agrega el estado de la sincronización. */
    private static final String TITULO = "Búsqueda y Carrito - Tienda de Ropa";

    /** Sincronización con otras cajas abiertas; puede ser {@code null}. */
    private ServicioSincronizacion sincronizacion;

    /** Ventana de administración abierta, o {@code null} si no hay ninguna. */
    private VentanaCatalogo ventanaAdmin;


    /**
     * Crea la ventana principal (tienda) con inyección de dependencias.
     *
     * @param gestionPrendas Repositorio de prendas (compartido con la administración).
     * @param gestionVentas  Repositorio de ventas (historial).
     * @param gestionClientes Consulta de clientes para asociarlos a las ventas.
     * @param servicioAutenticacion Servicio que verifica el acceso a la administración.
     */
    public VentanaCarrito(IGestionPrendas gestionPrendas, IGestionVentas gestionVentas,
                          IGestionClientes gestionClientes, ServicioAutenticacion servicioAutenticacion) {
        this(gestionPrendas, gestionVentas, gestionClientes, servicioAutenticacion, null);
    }

    /**
     * Crea la ventana principal sincronizada con otras cajas abiertas.
     *
     * @param gestionPrendas Repositorio de prendas (compartido con la administración).
     * @param gestionVentas  Repositorio de ventas (historial).
     * @param gestionClientes Consulta de clientes para asociarlos a las ventas.
     * @param servicioAutenticacion Servicio que verifica el acceso a la administración.
     * @param sincronizacion Servicio de sockets que avisa los cambios entre cajas;
     *                       puede ser {@code null} para trabajar sin sincronizar.
     */
    public VentanaCarrito(IGestionPrendas gestionPrendas, IGestionVentas gestionVentas,
                          IGestionClientes gestionClientes, ServicioAutenticacion servicioAutenticacion,
                          ServicioSincronizacion sincronizacion) {

        this.gestionPrendas = gestionPrendas;
        this.gestionVentas = gestionVentas;
        this.gestionClientes = gestionClientes;
        this.servicioAutenticacion = servicioAutenticacion;
        this.sincronizacion = sincronizacion;
        this.carrito = new Carrito();
        this.servicioBusqueda = new ServicioBusqueda(gestionPrendas);




        // Configurar la ventana principal
        configurarVentana();

        // Crear los paneles
        crearPaneles();

        // Organizar los paneles en la ventana
        organizarLayout();

        // Escuchar los avisos de otras cajas
        configurarSincronizacion();

        // Cargar las prendas disponibles al abrir la ventana
        actualizarDisponibles();
    }


    /**
     * Configura las propiedades básicas de la ventana JFrame.
     */
    private void configurarVentana() {
        // Título de la ventana
        setTitle(TITULO);

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

        panelCarrito = new PanelCarrito(carrito, this, gestionVentas, gestionClientes);

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
     * Registra esta ventana como oyente de los avisos de otras cajas.
     *
     * <p>Los avisos llegan en un hilo de red, por eso la actualización de la
     * interfaz se envía al hilo de Swing con {@link SwingUtilities#invokeLater}.
     * El título de la ventana muestra el estado de la sincronización.</p>
     */
    private void configurarSincronizacion() {
        if (sincronizacion == null) {
            return;
        }
        mostrarEstadoSincronizacion(sincronizacion.getEstado());
        sincronizacion.agregarOyenteEstado(estado ->
                SwingUtilities.invokeLater(() -> mostrarEstadoSincronizacion(estado)));
        sincronizacion.agregarOyente(mensaje ->
                SwingUtilities.invokeLater(this::actualizarPorCambioEnOtraCaja));
    }

    /**
     * Muestra en el título de la ventana el estado de la sincronización.
     *
     * @param estado descripción del estado, por ejemplo "Caja principal".
     */
    private void mostrarEstadoSincronizacion(String estado) {
        setTitle(TITULO + "  [" + estado + "]");
    }

    /**
     * Vuelve a consultar MySQL porque otra caja registró una venta o cambió el catálogo.
     *
     * <p>Actualiza las prendas disponibles, los reportes y, si está abierta,
     * la tabla de la administración.</p>
     */
    private void actualizarPorCambioEnOtraCaja() {
        actualizarDisponibles();
        actualizarReportes();
        if (ventanaAdmin != null && ventanaAdmin.isDisplayable()) {
            ventanaAdmin.refrescarCatalogo();
        }
    }

    /**
     * Avisa a las demás cajas que esta registró una venta.
     * Se llama desde el carrito después de confirmar la venta.
     */
    public void notificarVentaRegistrada() {
        if (sincronizacion != null) {
            sincronizacion.notificar(ServicioSincronizacion.MENSAJE_VENTA);
        }
    }

    /**
     * Avisa a las demás cajas que cambió el catálogo de prendas.
     * Se llama desde la administración después de crear, actualizar o eliminar.
     */
    private void notificarCambioCatalogo() {
        if (sincronizacion != null) {
            sincronizacion.notificar(ServicioSincronizacion.MENSAJE_CATALOGO);
        }
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
     * Abre la administración, previa autenticación, ocultando la tienda.
     *
     * <p>Pide usuario y contraseña, y los verifica en segundo plano contra los
     * usuarios guardados en MySQL. Solo los usuarios con rol administrador
     * pueden entrar.</p>
     */
    public void abrirAdministracion() {
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
            return;
        }
        String usuario = campoUsuario.getText().trim();
        char[] contrasena = campoPassword.getPassword();
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        new SwingWorker<Usuario, Void>() {
            /**
             * Verifica las credenciales contra MySQL fuera del hilo de Swing.
             *
             * @return el usuario autenticado, o {@code null} si las credenciales son incorrectas.
             */
            @Override
            protected Usuario doInBackground() {
                return servicioAutenticacion.autenticar(usuario, contrasena);
            }

            /**
             * Abre la administración o informa el error, en el hilo de Swing.
             */
            @Override
            protected void done() {
                setCursor(java.awt.Cursor.getDefaultCursor());
                try {
                    Usuario autenticado = get();
                    if (autenticado == null || !autenticado.esAdministrador()) {
                        JOptionPane.showMessageDialog(VentanaCarrito.this,
                                "Usuario o contraseña incorrectos.",
                                "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    setVisible(false);
                    ventanaAdmin = new VentanaCatalogo(gestionPrendas,
                            VentanaCarrito.this::mostrarTienda,
                            VentanaCarrito.this::notificarCambioCatalogo);
                    ventanaAdmin.setVisible(true);
                } catch (InterruptedException excepcion) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException excepcion) {
                    JOptionPane.showMessageDialog(VentanaCarrito.this,
                            "No se pudo verificar el usuario en la base de datos.\n"
                                    + "Verifique que MySQL esté disponible e intente de nuevo.",
                            "Error de base de datos", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Vuelve a mostrar la tienda y refresca las prendas disponibles.
     */
    private void mostrarTienda() {
        ventanaAdmin = null;
        setVisible(true);
        actualizarDisponibles();
    }

}
