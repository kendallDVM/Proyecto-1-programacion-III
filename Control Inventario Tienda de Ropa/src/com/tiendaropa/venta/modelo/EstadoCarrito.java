package com.tiendaropa.venta.modelo;



/**
 * Enum que representa los estados posibles del carrito durante su ciclo de vida.
 *
 * @author Liseth Briones
 */



public enum EstadoCarrito {

    /** Cliente está agregando y quitando prendas del carrito. */
    ACTIVO,

    /** El carrito está siendo procesado en el checkout. */
    PROCESANDO,

    /** La venta ha sido completada exitosamente. */
    COMPLETADO;

}
