package com.tiendaropa.catalogo.repositorio;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;

import java.util.List;

/**
 * Implementación en memoria de {@link IGestionPrendas} para el catálogo
 * de ropa de segunda mano.
 *
 * <p>Extiende {@link BaseRepositorio} para reutilizar las operaciones CRUD
 * genéricas (registrar, buscar, actualizar, eliminar y listar), aportando
 * únicamente la clave de cada prenda y los filtros específicos del dominio
 * (tipo, talla, estado, precio y búsqueda por texto).</p>
 */
public class RepositorioPrendas extends BaseRepositorio<Prenda> implements IGestionPrendas {

    /**
     * Constructor por defecto que inicializa el almacenamiento en memoria vacío.
     */
    public RepositorioPrendas() {
        super();
    }

    /**
     * Devuelve la clave única que identifica una prenda dentro del repositorio.
     *
     * @param prenda prenda de la que se extrae su clave.
     * @return código único de la prenda.
     */
    @Override
    protected String claveDe(Prenda prenda) {
        return prenda.getCodigo();
    }

    /**
     * Registra una nueva prenda en el catálogo.
     *
     * @param prenda prenda que se desea registrar en el catálogo.
     * @return {@code true} si la prenda se registró correctamente,
     *         {@code false} si es nula, su código es nulo o ya existe.
     */
    @Override
    public boolean registrar(Prenda prenda) {
        return super.registrar(prenda);
    }

    /**
     * Actualiza los datos de una prenda existente en el catálogo.
     *
     * @param prenda prenda con los datos nuevos que reemplazan al original.
     * @return {@code true} si la prenda fue encontrada y actualizada,
     *         {@code false} si no existe un registro con ese código.
     */
    @Override
    public boolean actualizar(Prenda prenda) {
        return super.actualizar(prenda);
    }

    /**
     * Elimina la prenda cuyo código se indica.
     *
     * @param codigo código único de la prenda que se desea eliminar.
     * @return {@code true} si la prenda fue eliminada, {@code false} en caso contrario.
     */
    @Override
    public boolean eliminar(String codigo) {
        return super.eliminar(codigo);
    }

    /**
     * Busca una prenda por su código único.
     *
     * @param codigo código de la prenda a localizar.
     * @return la prenda encontrada o {@code null} si no existe.
     */
    @Override
    public Prenda buscarPorCodigo(String codigo) {
        return super.buscar(codigo);
    }

    /**
     * Devuelve todas las prendas del catálogo ordenadas por código.
     *
     * @return lista no modificable con todas las prendas registradas.
     */
    @Override
    public List<Prenda> obtenerTodas() {
        return super.obtenerTodos();
    }

    /**
     * Filtra las prendas que coincidan con el tipo indicado.
     *
     * @param tipo criterio de filtrado por tipo de prenda.
     * @return lista con las prendas que pertenecen al tipo indicado.
     */
    @Override
    public List<Prenda> filtrarPorTipo(TipoPrenda tipo) {
        return filtrar(prenda -> prenda.getTipo() == tipo);
    }

    /**
     * Filtra las prendas que coincidan con la talla indicada.
     *
     * @param talla criterio de filtrado por talla de prenda.
     * @return lista con las prendas de la talla indicada.
     */
    @Override
    public List<Prenda> filtrarPorTalla(Talla talla) {
        return filtrar(prenda -> prenda.getTalla() == talla);
    }

    /**
     * Filtra las prendas que coincidan con el estado indicado.
     *
     * @param estado criterio de filtrado por estado de conservación.
     * @return lista con las prendas que presentan el estado indicado.
     */
    @Override
    public List<Prenda> filtrarPorEstado(EstadoPrenda estado) {
        return filtrar(prenda -> prenda.getEstado() == estado);
    }

    /**
     * Filtra las prendas cuyo precio se encuentre dentro del rango dado.
     *
     * @param precioMinimo límite inferior del rango (incluido).
     * @param precioMaximo límite superior del rango (incluido).
     * @return lista con las prendas cuyo precio está dentro del rango.
     */
    @Override
    public List<Prenda> filtrarPorPrecio(double precioMinimo, double precioMaximo) {
        return filtrar(prenda -> prenda.getPrecio() >= precioMinimo && prenda.getPrecio() <= precioMaximo);
    }

    /**
     * Filtra prendas por coincidencia de texto libre aplicado a varios
     * atributos: código, tipo, talla, estado y precio.
     *
     * @param texto criterio de búsqueda, puede ser parcial o completo.
     * @return lista con las prendas que coinciden con el texto dado.
     */
    @Override
    public List<Prenda> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return obtenerTodas();
        }
        String normalizado = texto.trim().toLowerCase();
        return filtrar(prenda -> coincideConTexto(prenda, normalizado));
    }

    /**
     * Indica si una prenda coincide con el texto de búsqueda normalizado.
     *
     * @param prenda prenda a evaluar.
     * @param texto  texto normalizado en minúsculas a comparar.
     * @return {@code true} si coincide en algún atributo, {@code false} en caso contrario.
     */
    private boolean coincideConTexto(Prenda prenda, String texto) {
        return contiene(prenda.getCodigo(), texto)
                || contiene(prenda.getTipo() == null ? null : prenda.getTipo().name(), texto)
                || contiene(prenda.getTalla() == null ? null : prenda.getTalla().name(), texto)
                || contiene(prenda.getEstado() == null ? null : prenda.getEstado().name(), texto)
                || contiene(EstadoPrenda.descripcion(prenda.getEstado()), texto)
                || contiene(String.valueOf(prenda.getPrecio()), texto);
    }

    /**
     * Determina si un valor de texto contiene la cadena buscada.
     *
     * @param valor valor de origen (puede ser nulo).
     * @param texto texto a buscar en minúsculas.
     * @return {@code true} si el valor no es nulo e incluye el texto.
     */
    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }
}