

//Se ejecuta ventana Catalogo y Carrito desde este Main
package com.tiendaropa;
import com.tiendaropa.catalogo.gui.VentanaCatalogo;
import com.tiendaropa.catalogo.repositorio.RepositorioPrendas;
import com.tiendaropa.venta.gui.VentanaCarrito;
import com.tiendaropa.venta.repositorio.RepositorioVentas;

public class Main {

    public static void main(String[] args) {

        // Un único repositorio de prendas compartido por ambos módulos
        RepositorioPrendas repositorio = new RepositorioPrendas();

        // Repositorio para el historial de ventas
        RepositorioVentas repositorioVentas = new RepositorioVentas();

        // Abrir ventana del Módulo 1
        VentanaCatalogo ventanaCatalogo = new VentanaCatalogo(repositorio);
        ventanaCatalogo.setVisible(true);

        // Abrir ventana del Módulo 2
        VentanaCarrito ventanaCarrito = new VentanaCarrito(repositorio, repositorioVentas);
        ventanaCarrito.setVisible(true);
    }
}