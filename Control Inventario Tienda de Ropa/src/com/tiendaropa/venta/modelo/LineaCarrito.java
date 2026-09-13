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
     * Devuelve la prenda contenida en esta línea.
     *
     * @return objeto {@link Prenda} de la línea.
     */
    public Prenda getPrenda() {return prenda;}

    /**
     * Devuelve el precio congelado al momento en que se agregó la prenda.
     *
     * @return precio en colones congelado.
     */
    public double getPrecioEnMomento() {return precioEnMomento;}

    /**
     * Devuelve el subtotal de esta línea.
     *
     * <p>Como cada prenda es única (ropa de segunda mano), la cantidad siempre
     * es 1 y, por lo tanto, el subtotal equivale al precio congelado.</p>
     *
     * @return subtotal de la línea en colones.
     */
    public double getSubtotal() {
        return precioEnMomento;
    }

    /**
     * Devuelve la fecha y hora en que se agregó esta línea al carrito.
     *
     * @return fecha y hora de agregación.
     */
    public LocalDateTime getFechaAgregacion() {
        return fechaAgregacion;
    }

    // ========== MÉTODO toString ==========

    /**
     * Devuelve una representación textual de la línea para depuración.
     *
     * @return cadena con el formato {@code PR001 - ¢25,000}.
     */
    @Override
    public String toString() {
        return String.format("%s - ¢%,.0f",
                prenda.getCodigo(),
                precioEnMomento);
    }

}
