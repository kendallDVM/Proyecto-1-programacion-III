package com.tiendaropa.cliente.modelo;

import java.util.Objects;

/**
 * Representa a un cliente de la tienda que puede asociarse a una venta.
 *
 * <p>Se identifica por su cédula, que es la clave primaria de la tabla
 * {@code Cliente}. El nombre se guarda una sola vez y las ventas solo hacen
 * referencia a la cédula, evitando repetir datos (3FN).</p>
 */
public class Cliente {

    /** Largo máximo de la cédula, igual que la columna {@code cedula VARCHAR(20)}. */
    public static final int LARGO_MAXIMO_CEDULA = 20;

    /** Largo máximo del nombre, igual que la columna {@code nombre_completo VARCHAR(100)}. */
    public static final int LARGO_MAXIMO_NOMBRE = 100;

    /** Cédula única del cliente. */
    private final String cedula;

    /** Nombre completo del cliente. */
    private final String nombreCompleto;

    /**
     * Crea un cliente validando sus datos.
     *
     * @param cedula         cédula del cliente; no puede estar vacía.
     * @param nombreCompleto nombre completo; no puede estar vacío.
     * @throws IllegalArgumentException si algún dato está vacío o excede el largo permitido.
     */
    public Cliente(String cedula, String nombreCompleto) {
        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException("La cédula del cliente es obligatoria.");
        }
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (cedula.trim().length() > LARGO_MAXIMO_CEDULA) {
            throw new IllegalArgumentException(
                    "La cédula no puede tener más de " + LARGO_MAXIMO_CEDULA + " caracteres.");
        }
        if (nombreCompleto.trim().length() > LARGO_MAXIMO_NOMBRE) {
            throw new IllegalArgumentException(
                    "El nombre no puede tener más de " + LARGO_MAXIMO_NOMBRE + " caracteres.");
        }
        this.cedula = cedula.trim();
        this.nombreCompleto = nombreCompleto.trim();
    }

    /**
     * Devuelve la cédula del cliente.
     *
     * @return cédula del cliente.
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Devuelve el nombre completo del cliente.
     *
     * @return nombre completo del cliente.
     */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /**
     * Compara este cliente con otro objeto por su cédula.
     *
     * @param objeto objeto a comparar.
     * @return {@code true} si el otro objeto es un cliente con la misma cédula.
     */
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        return Objects.equals(cedula, ((Cliente) objeto).cedula);
    }

    /**
     * Calcula el código hash a partir de la cédula.
     *
     * @return código hash del cliente.
     */
    @Override
    public int hashCode() {
        return Objects.hash(cedula);
    }

    /**
     * Devuelve el cliente en formato legible para mostrar en pantalla.
     *
     * @return nombre y cédula, por ejemplo {@code "Ana Mora (1-1111-1111)"}.
     */
    @Override
    public String toString() {
        return nombreCompleto + " (" + cedula + ")";
    }
}
