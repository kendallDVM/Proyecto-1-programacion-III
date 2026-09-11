package com.tiendaropa;

import com.tiendaropa.catalogo.gui.VentanaCatalogo;
import com.tiendaropa.catalogo.repositorio.RepositorioPrendas;
import com.tiendaropa.venta.gui.VentanaCarrito;
import com.tiendaropa.venta.repositorio.RepositorioVentas;

/**
 * Punto de entrada principal de la aplicación de control de inventario
 * para la tienda de ropa de segunda mano.
 *
 * <p>Construye los repositorios compartidos y lanza las dos ventanas del
 * sistema: el catálogo de prendas y el módulo de búsqueda/carrito.</p>
 */
public class Main {

    /**
     * Inicia la aplicación creando los repositorios y mostrando las ventanas.
     *
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {

        // Un único repositorio de prendas compartido por ambos módulos
        RepositorioPrendas repositorio = new RepositorioPrendas();

        // Repositorio para el historial de ventas
        RepositorioVentas repositorioVentas = new RepositorioVentas();

        // Abrir ventana del Módulo 1 (catálogo)
        VentanaCatalogo ventanaCatalogo = new VentanaCatalogo(repositorio);
        ventanaCatalogo.setVisible(true);

        // Abrir ventana del Módulo 2 (búsqueda y carrito)
        VentanaCarrito ventanaCarrito = new VentanaCarrito(repositorio, repositorioVentas);
        ventanaCarrito.setVisible(true);
    }
}