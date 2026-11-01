package com.tiendaropa;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.catalogo.repositorio.IGestionPrendas;
import com.tiendaropa.catalogo.repositorio.RepositorioPrendasJDBC;
import com.tiendaropa.infraestructura.PersistenciaException;
import com.tiendaropa.venta.gui.VentanaCarrito;
import com.tiendaropa.venta.repositorio.IGestionVentas;
import com.tiendaropa.venta.repositorio.RepositorioVentasJDBC;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal de la aplicación de la tienda de ropa de segunda mano.
 *
 * <p>Construye los repositorios JDBC que persisten la información en MySQL,
 * carga datos de ejemplo solo si el catálogo está vacío y abre la ventana
 * principal (la tienda / carrito). La administración (catálogo) se abre
 * desde la propia tienda bajo autenticación.</p>
 */
public class Main {

    /**
     * Inicia la aplicación: repositorios MySQL, datos de ejemplo y tienda.
     *
     * <p>Si no es posible conectarse a la base de datos, muestra un mensaje
     * explicativo y termina la aplicación sin abrir la tienda.</p>
     *
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {

        // Un único repositorio de prendas compartido con la administración
        IGestionPrendas repositorio = new RepositorioPrendasJDBC();

        // Repositorio para el historial de ventas
        IGestionVentas repositorioVentas = new RepositorioVentasJDBC();

        try {
            // Primera consulta a MySQL: también comprueba que la conexión funciona
            cargarDatosEjemplo(repositorio);
        } catch (PersistenciaException excepcion) {
            mostrarErrorConexion(excepcion);
            return;
        }

        // La tienda es la ventana principal; se crea en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            VentanaCarrito ventanaCarrito = new VentanaCarrito(repositorio, repositorioVentas);
            ventanaCarrito.setVisible(true);
        });
    }

    /**
     * Inserta unas prendas de ejemplo para que la tienda no arranque vacía.
     *
     * <p>Solo se cargan cuando el catálogo no tiene ninguna prenda, para no
     * volver a crear prendas que el administrador haya eliminado.</p>
     *
     * @param repositorio repositorio de prendas compartido.
     * @throws PersistenciaException si la base de datos no está disponible.
     */
    private static void cargarDatosEjemplo(IGestionPrendas repositorio) {
        if (!repositorio.obtenerTodas().isEmpty()) {
            return;
        }
        repositorio.registrar(new Prenda("P-001", TipoPrenda.CAMISA, Talla.M, EstadoPrenda.BUEN_ESTADO, 12.50));
        repositorio.registrar(new Prenda("P-002", TipoPrenda.PANTALON, Talla.L, EstadoPrenda.COMO_NUEVO, 18.00));
        repositorio.registrar(new Prenda("P-003", TipoPrenda.VESTIDO, Talla.S, EstadoPrenda.ACEPTABLE, 14.75));
        repositorio.registrar(new Prenda("P-004", TipoPrenda.SUETER, Talla.XL, EstadoPrenda.BUEN_ESTADO, 16.20));
    }

    /**
     * Informa al usuario que no se pudo conectar con MySQL.
     *
     * <p>Muestra la causa resumida (por ejemplo, variable de entorno sin
     * configurar o servidor apagado) sin exponer la traza técnica completa.</p>
     *
     * @param excepcion error de persistencia producido al conectar.
     */
    private static void mostrarErrorConexion(PersistenciaException excepcion) {
        String causa = excepcion.getCause() != null
                ? excepcion.getCause().getMessage()
                : excepcion.getMessage();
        JOptionPane.showMessageDialog(null,
                "No se pudo conectar con la base de datos MySQL.\n\n"
                        + "Detalle: " + causa + "\n\n"
                        + "Verifique que el servidor MySQL esté encendido y que la\n"
                        + "variable de entorno TIENDA_DB_PASSWORD esté configurada.",
                "Error de conexión",
                JOptionPane.ERROR_MESSAGE);
    }
}
