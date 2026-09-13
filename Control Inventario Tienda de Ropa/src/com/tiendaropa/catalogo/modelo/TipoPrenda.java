package com.tiendaropa.catalogo.modelo;

/**
 * Enumeración que define los tipos de prenda soportados por el catálogo
 * de la tienda de ropa de segunda mano.
 *
 * <p>Permite clasificar cada prenda dentro de una categoría concreta,
 * facilitando su filtrado y búsqueda dentro del inventario.</p>
 */
public enum TipoPrenda {

    /** Prenda ligera de tela de algodón con o sin mangas. */
    CAMISETA,

    /** Prenda de vestir con botones en la parte frontal. */
    CAMISA,

    /** Prenda para cubrir las piernas desde la cintura. */
    PANTALON,

    /** Prenda de una sola pieza para el torso y las piernas. */
    VESTIDO,

    /** Prenda que se ajusta a la cintura y cubre parcialmente las piernas. */
    FALDA,

    /** Prenda exterior de manga larga que se ajusta al bucle. */
    CHAQUETA,

    /** Prenda exterior de abrigo de uso común en invierno. */
    ABRIGO,

    /** Prenda de punto que cubre el torso. */
    SUETER,

    /** Prenda de abrigo de tejido grueso, habitualmente sin forro. */
    BUZO,

    /** Calzado destinado a cubrir y proteger el pie. */
    ZAPATOS,

    /** Comprobante como bufandas, gorros o correas. */
    ACCESORIO;

    /**
     * Devuelve una representación textual en minúsculas del tipo de prenda.
     *
     * @param tipo valor del enumerado cuyo nombre se desea formatear.
     * @return nombre del tipo de prenda en minúsculas.
     */
    public static String mostrarEnMinusculas(TipoPrenda tipo) {
        return tipo == null ? "" : tipo.name().toLowerCase();
    }
}