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

    /**
     * Devuelve la talla correspondiente a un nombre ignorando mayúsculas
     * o minúsculas.
     *
     * @param nombre nombre de la talla a buscar.
     * @return la talla encontrada o {@code null} si no coincide con ninguna.
     */
    public static Talla desdeNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        String normalizado = nombre.trim().toUpperCase();
        for (Talla taller : values()) {
            if (taller.name().equals(normalizado)) {
                return taller;
            }
        }
        return null;
    }
}