package com.tiendaropa.venta.modelo;


import com.tiendaropa.catalogo.modelo.Prenda;
import java.time.LocalDateTime;



/**
 * Representa una línea (item) en el carrito de compras.
 * Cada línea corresponde a UNA prenda única (segunda mano = cantidad siempre 1).
 * Congela el precio en el momento de agregación para evitar cambios posteriores.
 * @author Liseth Briones
 */

public class LineaCarrito {

                            //ATRIBUTOS

    private Prenda prenda;        // La prenda única que contiene esta línea
    private double precioEnMomento;      // Precio congelado al momento de agregación
    private LocalDateTime fechaAgregacion;  // Cuándo se agregó al carrito


                            //Constructor
    /**
     * Crea una nueva línea de carrito con una prenda.
     * Congela el precio en el momento de agregación.
     *
     * @param prenda Objeto Prenda a agregar (no puede ser nulo)
     * @throws IllegalArgumentException si prenda es nula
     */

    public LineaCarrito(Prenda prenda) {
        // Validación: la prenda no puede ser nula
        if (prenda == null) {
            throw new IllegalArgumentException("La prenda no puede ser nula");
        }

        this.prenda = prenda;
        this.precioEnMomento = prenda.getPrecio();  // Congela el precio AHORA
        this.fechaAgregacion = LocalDateTime.now();  // Registra cuándo se agregó
    }


    // ========== GETTERS ==========

    /**
     * Retorna la prenda de esta línea.
     * @return Objeto Prenda
     */
    public Prenda getPrenda() {return prenda;}

    /**
     * Retorna el precio congelado al momento de agregación.
     * @return double: precio en colones
     */
    public double getPrecioEnMomento() {return precioEnMomento;}

    /**
      Retorna el subtotal de esta línea.
      Como cantidad siempre es 1 (ropa única), subtotal = precio.
      @return double: subtotal en colones
     */
    public double getSubtotal() {
        return precioEnMomento; // Cantidad = 1 siempre
    }

    /**
     * Retorna cuándo se agregó esta línea al carrito.
     * @return LocalDateTime
     */
    public LocalDateTime getFechaAgregacion() {
        return fechaAgregacion;
    }

    // ========== MÉTODO toString ==========

    /**
     * Retorna representación en texto para debugging.
     * @return String con formato: "PR001 - ¢25,000"
     */
    @Override
    public String toString() {
        return String.format("%s - ¢%,.0f",
                prenda.getCodigo(),     // Código de la prenda
                precioEnMomento);       // Precio congelado
    }

}
