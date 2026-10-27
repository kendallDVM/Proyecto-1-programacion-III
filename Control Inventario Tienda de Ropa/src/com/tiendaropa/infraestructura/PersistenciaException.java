package com.tiendaropa.infraestructura;

/**
 * Excepción de dominio que envuelve errores de persistencia JDBC.
 *
 * <p>Se lanza cuando una operación contra la base de datos falla (por ejemplo,
 * conexión caída, violación de restricción o error de sentencia SQL). Al ser
 * una {@link RuntimeException}, no obliga a modificar las firmas de las
 * interfaces de repositorio existentes, pero permite al llamador distinguir un
 * error real de base de datos de un resultado legítimamente vacío
 * (lista vacía o {@code null} por "no encontrado").</p>
 */
public class PersistenciaException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo y la causa original.
     *
     * @param mensaje descripción del error de persistencia.
     * @param causa   {@link SQLException} original que motivó el error.
     */
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
