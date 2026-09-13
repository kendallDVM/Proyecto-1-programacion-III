package com.tiendaropa.catalogo.modelo;

import java.util.Objects;

/**
 * Modelo de datos que representa una prenda única de ropa de segunda mano
 * registrada en el catálogo del sistema de inventario.
 *
 * <p>Dado que se trata de ropa de segunda mano, cada instancia corresponde
 * a un artículo individual: no existen cantidades ni lotes.</p>
 */
public class Prenda {

    /** Código único e identificador de la prenda dentro del catálogo. */
    private String codigo;

    /** Tipo o categoría de la prenda, por ejemplo camiseta o pantalón. */
    private TipoPrenda tipo;

    /** Talla asociada a la prenda. */
    private Talla talla;

    /** Estado o condición de conservación de la prenda de segunda mano. */
    private EstadoPrenda estado;

    /** Precio de venta de la prenda. Debe ser un valor positivo. */
    private double precio;

    /**
     * Constructor por defecto. No realiza inicialización de atributos y
     * deja los valores con sus características por defecto de Java.
     */
    public Prenda() {
    }

    /**
     * Constructor que inicializa todos los atributos de la prenda.
     *
     * @param codigo código único de la prenda.
     * @param tipo   tipo o categoría de la prenda.
     * @param talla  talla correspondiente a la prenda.
     * @param estado estado de conservación de la prenda.
     * @param precio precio de venta de la prenda.
     */
    public Prenda(String codigo, TipoPrenda tipo, Talla talla, EstadoPrenda estado, double precio) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.talla = talla;
        this.estado = estado;
        this.precio = precio;
    }

    /**
     * Devuelve el código de la prenda.
     *
     * @return cadena con el código único de la prenda.
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Establece el código de la prenda.
     *
     * @param codigo cadena con el nuevo código de la prenda.
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Devuelve el tipo de prenda.
     *
     * @return valor del enumerado {@link TipoPrenda}.
     */
    public TipoPrenda getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de prenda.
     *
     * @param tipo nuevo valor del enumerado {@link TipoPrenda}.
     */
    public void setTipo(TipoPrenda tipo) {
        this.tipo = tipo;
    }

    /**
     * Devuelve la talla de la prenda.
     *
     * @return valor del enumerado {@link Talla}.
     */
    public Talla getTalla() {
        return talla;
    }

    /**
     * Establece la talla de la prenda.
     *
     * @param talla nuevo valor del enumerado {@link Talla}.
     */
    public void setTalla(Talla talla) {
        this.talla = talla;
    }

    /**
     * Devuelve el estado de conservación de la prenda.
     *
     * @return valor del enumerado {@link EstadoPrenda}.
     */
    public EstadoPrenda getEstado() {
        return estado;
    }

    /**
     * Establece el estado de conservación de la prenda.
     *
     * @param estado nuevo valor del enumerado {@link EstadoPrenda}.
     */
    public void setEstado(EstadoPrenda estado) {
        this.estado = estado;
    }

    /**
     * Devuelve el precio de venta de la prenda.
     *
     * @return precio de la prenda como valor numérico en coma flotante.
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio de venta de la prenda.
     *
     * @param precio nuevo valor del precio de venta.
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * Compara esta prenda con otro objeto para determinar si representa
     * el mismo artículo del catálogo.
     *
     * <p>Dos prendas se consideran iguales cuando comparten el mismo
     * código, ya que este identifica de forma única cada artículo.</p>
     *
     * @param objeto objeto a comparar con la prenda actual.
     * @return {@code true} si ambas prendas tienen el mismo código,
     *         {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Prenda prenda = (Prenda) objeto;
        return Objects.equals(codigo, prenda.codigo);
    }

    /**
     * Devuelve el código hash de la prenda basado en su código único.
     *
     * @return entero que representa el hash de la prenda.
     */
    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    /**
     * Devuelve una representación textual de la prenda con sus atributos.
     *
     * @return cadena que describe la prenda de forma legible.
     */
    @Override
    public String toString() {
        return "Prenda{" +
                "codigo='" + codigo + '\''
                + ", tipo=" + tipo
                + ", talla=" + talla
                + ", estado=" + estado
                + ", precio=" + precio
                + '}';
    }
}