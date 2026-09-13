package com.tiendaropa;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.catalogo.repositorio.RepositorioPrendas;
import com.tiendaropa.venta.gui.VentanaCarrito;
import com.tiendaropa.venta.repositorio.RepositorioVentas;

/**
 * Punto de entrada principal de la aplicación de la tienda de ropa de segunda mano.
 *
 * <p>Construye los repositorios compartidos, carga datos de ejemplo y abre la
 * ventana principal (la tienda / carrito). La administración (catálogo) se abre
 * desde la propia tienda bajo autenticación.</p>
 */
public class Main {

    /**
     * Inicia la aplicación: repositorios compartidos, datos de ejemplo y tienda.
     *
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {

        // Un único repositorio de prendas compartido con la administración
        RepositorioPrendas repositorio = new RepositorioPrendas();

        // Repositorio para el historial de ventas
        RepositorioVentas repositorioVentas = new RepositorioVentas();

        // Cargar algunas prendas de ejemplo disponibles desde el inicio
        cargarDatosEjemplo(repositorio);

        // La tienda es la ventana principal
        VentanaCarrito ventanaCarrito = new VentanaCarrito(repositorio, repositorioVentas);
        ventanaCarrito.setVisible(true);
    }

    /**
     * Inserta unas prendas de ejemplo para que la tienda no arranque vacía.
     *
     * @param repositorio repositorio de prendas compartido.
     */
    private static void cargarDatosEjemplo(RepositorioPrendas repositorio) {
        registrarSiNoExiste(repositorio, new Prenda("P-001", TipoPrenda.CAMISA, Talla.M, EstadoPrenda.BUEN_ESTADO, 12.50));
        registrarSiNoExiste(repositorio, new Prenda("P-002", TipoPrenda.PANTALON, Talla.L, EstadoPrenda.COMO_NUEVO, 18.00));
        registrarSiNoExiste(repositorio, new Prenda("P-003", TipoPrenda.VESTIDO, Talla.S, EstadoPrenda.ACEPTABLE, 14.75));
        registrarSiNoExiste(repositorio, new Prenda("P-004", TipoPrenda.SUETER, Talla.XL, EstadoPrenda.BUEN_ESTADO, 16.20));
    }

    /**
     * Registra una prenda de ejemplo ignorando duplicados.
     *
     * @param repositorio repositorio de prendas.
     * @param prenda      prenda de ejemplo que se desea cargar.
     */
    private static void registrarSiNoExiste(RepositorioPrendas repositorio, Prenda prenda) {
        if (repositorio.buscarPorCodigo(prenda.getCodigo()) == null) {
            repositorio.registrar(prenda);
        }
    }
}