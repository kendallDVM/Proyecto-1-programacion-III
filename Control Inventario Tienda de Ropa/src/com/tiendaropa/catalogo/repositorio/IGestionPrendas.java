package com.tiendaropa.catalogo.repositorio;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;

import java.util.List;

/**
 * Contrato que define las operaciones de gestión de las prendas del
 * catálogo de la tienda de ropa de segunda mano.
 *
 * <p>Expone métodos CRUD (crear, consultar, actualizar y eliminar) junto
 * con métodos de búsqueda y filtrado que permiten localizar las prendas
 * por tipo, talla, estado o rango de precio.</p>
 */
public interface IGestionPrendas {

    /**
     * Registra una nueva prenda en el catálogo.
     *
     * @param prenda prenda que se desea registrar en el catálogo.
     * @return {@code true} si la prenda se registró correctamente,
     *         {@code false} si ya existe un registro con el mismo código
     *         o si la prenda (o su código) es nula.
     */
    boolean registrar(Prenda prenda);

    /**
     * Actualiza los datos de una prenda existente en el catálogo.
     *
     * @param prenda prenda con los datos nuevos que reemplazan al original.
     * @return {@code true} si se encontró y actualizó la prenda,
     *         {@code false} si no existe un registro con el mismo código.
     */
    boolean actualizar(Prenda prenda);

    /**
     * Elimina la prenda cuyo código se indica.
     *
     * @param codigo código único de la prenda que se desea eliminar.
     * @return {@code true} si la prenda fue eliminada,
     *         {@code false} si no se encontró ningún registro con ese código.
     */
    boolean eliminar(String codigo);

    /**
     * Busca una prenda por su código único.
     *
     * @param codigo código de la prenda a localizar.
     * @return la prenda encontrada o {@code null} si no existe.
     */
    Prenda buscarPorCodigo(String codigo);

    /**
     * Devuelve todas las prendas del catálogo ordenadas por código.
     *
     * @return lista no modificable con todas las prendas registradas.
     */
    List<Prenda> obtenerTodas();

    /**
     * Filtra las prendas que coincidan con el tipo indicado.
     *
     * @param tipo criterio de filtrado por tipo de prenda.
     * @return lista con las prendas que pertenecen al tipo indicado.
     */
    List<Prenda> filtrarPorTipo(TipoPrenda tipo);

    /**
     * Filtra las prendas que coincidan con la talla indicada.
     *
     * @param talla criterio de filtrado por talla de prenda.
     * @return lista con las prendas de la talla indicada.
     */
    List<Prenda> filtrarPorTalla(Talla talla);

    /**
     * Filtra las prendas que coincidan con el estado indicado.
     *
     * @param estado criterio de filtrado por estado de conservación.
     * @return lista con las prendas que presentan el estado indicado.
     */
    List<Prenda> filtrarPorEstado(EstadoPrenda estado);

    /**
     * Filtra las prendas cuyo precio se encuentre dentro del rango dado.
     *
     * @param precioMinimo límite inferior del rango (incluido).
     * @param precioMaximo límite superior del rango (incluido).
     * @return lista con las prendas cuyo precio está dentro del rango.
     */
    List<Prenda> filtrarPorPrecio(double precioMinimo, double precioMaximo);

    /**
     * Filtra prendas por coincidencia de texto libre aplicado a varios
     * atributos: código, tipo, talla, estado y precio.
     *
     * @param texto criterio de búsqueda, puede ser parcial o completo.
     * @return lista con las prendas que coinciden con el texto dado.
     */
    List<Prenda> buscarPorTexto(String texto);
}