package com.tiendaropa.catalogo.modelo;

/**
 * Enumeración que representa las tallas disponibles para las prendas
 * del catálogo de la tienda de ropa de segunda mano.
 *
 * <p>Incluye talares estándar (XS a XXL) y un valor {@code UNICA} para
 * aquellas prendas de talla libre.</p>
 */
public enum Talla {

    /** Talla extra pequeña. */
    XS,

    /** Talla pequeña. */
    S,

    /** Talla mediana. */
    M,

    /** Talla grande. */
    L,

    /** Talla extra grande. */
    XL,

    /** Talla doble extra grande. */
    XXL,

    /** Valor especial para prendas con talla libre o única. */
    UNICA;
}