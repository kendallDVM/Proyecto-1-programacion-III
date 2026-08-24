package com.tiendaropa.catalogo.modelo;

/**
 * Enumeración que describe la condición física o estado de conservación
 * de cada prenda de segunda mano en el catálogo.
 *
 * <p>Este estado se utiliza como criterio de filtrado y permite valorar
 * la calidad de la prenda.</p>
 */
public enum EstadoPrenda {

    /** Prenda nueva con etiqueta o sin señales de uso. */
    NUEVO,

    /** Prenda en estado semejante a nuevo, casi sin uso. */
    COMO_NUEVO,

    /** Prenda con desgaste normal, en condiciones correctas de uso. */
    BUEN_ESTADO,

    /** Prenda funcional con imperfecciones visibles pero aceptable. */
    ACEPTABLE,

    /** Prenda con un desgaste o defecto evidente. */
    DETERIORADO;

    /**
     * Devuelve la descripción legible del estado.
     *
     * @param estado valor del enumerado de estado.
     * @return una cadena con la descripción humanamente legible.
     */
    public static String descripcion(EstadoPrenda estado) {
        if (estado == null) {
            return "SIN ESPECIFICAR";
        }
        return switch (estado) {
            case NUEVO -> "Nuevo con etiqueta";
            case COMO_NUEVO -> "Como nuevo";
            case BUEN_ESTADO -> "Buen estado";
            case ACEPTABLE -> "Estado aceptable";
            case DETERIORADO -> "Deteriorado";
            default -> "DESCONOCIDO";
        };
    }
}