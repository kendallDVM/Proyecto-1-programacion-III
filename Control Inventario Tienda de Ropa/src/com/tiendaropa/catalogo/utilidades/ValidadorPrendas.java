package com.tiendaropa.catalogo.utilidades;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.catalogo.repositorio.RepositorioPrendas;

/**
 * Utilidad encargada de validar los datos de las prendas antes de que
 * sean registrados, actualizados o eliminados en el catálogo.
 *
 * <p>Centraliza las reglas de negocio: campos obligatorios no vacíos,
 * precio positivo y código único sin duplicados. Cuando una validación
 * falla se lanza {@link IllegalArgumentException} con un mensaje claro.</p>
 */
public final class ValidadorPrendas {

    /**
     * Constructor privado para impedir la instanciación de la utilidad.
     */
    private ValidadorPrendas() {
    }

    /**
     * Valida los datos obligatorios de una prenda antes de su registro.
     *
     * <p>Verifica que el código no sea nulo ni vacío, que el tipo, la talla
     * y el estado estén presentes, y que el precio sea mayor que cero.
     * Cuando se trata de una prenda nueva comprueba además que el código
     * no esté duplicado.</p>
     *
     * @param repositorio repositorio contra el que se comprueba la unicidad.
     * @param prenda      prenda cuyos datos se desean validar.
     * @param esNueva     {@code true} si se validan como registro nuevo,
     *                    {@code false} si es una actualización.
     * @throws IllegalArgumentException si alguna regla de negocio no se cumple.
     */
    public static void validar(RepositorioPrendas repositorio, Prenda prenda, boolean esNueva) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo.");
        }
        if (prenda == null) {
            throw new IllegalArgumentException("La prenda no puede ser nula.");
        }
        validarCamposNoVacios(prenda.getCodigo(), prenda.getTipo(), prenda.getTalla(), prenda.getEstado());
        validarPrecio(prenda.getPrecio());
        if (esNueva) {
            validarCodigoUnico(repositorio, prenda.getCodigo());
        }
    }

/**
     * Valida que los atributos básicos de la prenda no sean nulos o vacíos.
     *
     * @param codigo código único de la prenda.
     * @param tipo   tipo de prenda.
     * @param talla  talla de la prenda.
     * @param estado estado de conservación de la prenda.
     * @throws IllegalArgumentException si algún campo obligatorio está vacío.
     */
    public static void validarCamposNoVacios(String codigo, TipoPrenda tipo, Talla talla, EstadoPrenda estado) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la prenda es obligatorio.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de prenda.");
        }
        if (talla == null) {
            throw new IllegalArgumentException("Debe seleccionar la talla de la prenda.");
        }
        if (estado == null) {
            throw new IllegalArgumentException("Debe seleccionar el estado de la prenda.");
        }
    }

    /**
     * Valida que el precio de la prenda sea un número positivo y finito.
     *
     * @param precio precio de venta de la prenda.
     * @throws IllegalArgumentException si el precio no es positivo o no es finito.
     */
    public static void validarPrecio(double precio) {
        if (precio <= 0 || Double.isNaN(precio) || Double.isInfinite(precio)) {
            throw new IllegalArgumentException("El precio debe ser un valor positivo válido.");
        }
    }

    /**
     * Comprueba que el código de una prenda sea único dentro del repositorio.
     *
     * @param repositorio repositorio de prendas donde se consulta la unicidad.
     * @param codigo      código que se desea verificar.
     * @throws IllegalArgumentException si el código ya existe en el catálogo.
     */
    public static void validarCodigoUnico(RepositorioPrendas repositorio, String codigo) {
        if (codigoExiste(repositorio, codigo)) {
            throw new IllegalArgumentException("Ya existe una prenda con el código '" + codigo + "'.");
        }
    }

    /**
     * Indica si un código ya está registrado en el catálogo.
     *
     * @param repositorio repositorio donde se realiza la comprobación.
     * @param codigo      código a verificar.
     * @return {@code true} si el código ya pertenece a una prenda registrada.
     */
    public static boolean codigoExiste(RepositorioPrendas repositorio, String codigo) {
        return repositorio != null && repositorio.buscarPorCodigo(codigo) != null;
    }
}