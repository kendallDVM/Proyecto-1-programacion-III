package com.tiendaropa.catalogo.repositorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repositorio base genérico que almacena entidades en memoria indexadas por
 * una clave única de tipo {@link String}.
 *
 * <p>Centraliza las operaciones CRUD (registrar, buscar, actualizar, eliminar
 * y listar) para evitar duplicar su implementación en cada repositorio
 * concreto. Las subclases solo deben aportar la clave de cada entidad
 * mediante {@link #claveDe(Object)} y, si lo desean, añadir filtros
 * específicos de su dominio.</p>
 *
 * @param <T> tipo de entidad que este repositorio almacena y gestiona.
 */
public abstract class BaseRepositorio<T> {

    /** Mapa que asocia cada clave única con su entidad correspondiente. */
    protected final Map<String, T> elementos;

    /**
     * Constructor por defecto que inicializa el almacenamiento en memoria vacío.
     */
    protected BaseRepositorio() {
        this.elementos = new HashMap<>();
    }

    /**
     * Devuelve la clave única que identifica a una entidad dentro del repositorio.
     *
     * @param elemento entidad de la que se extrae su clave.
     * @return cadena que identifica de forma única al elemento.
     */
    protected abstract String claveDe(T elemento);

    /**
     * Registra una nueva entidad en el repositorio.
     *
     * @param elemento entidad que se desea registrar.
     * @return {@code true} si se registró correctamente, {@code false} si la
     *         entidad o su clave es nula, o si ya existe una con la misma clave.
     */
    public boolean registrar(T elemento) {
        if (elemento == null || claveDe(elemento) == null) {
            return false;
        }
        if (elementos.containsKey(claveDe(elemento))) {
            return false;
        }
        elementos.put(claveDe(elemento), elemento);
        return true;
    }

    /**
     * Actualiza los datos de una entidad existente identificada por su clave.
     *
     * @param elemento entidad con los nuevos datos que reemplazan al original.
     * @return {@code true} si se encontró y actualizó, {@code false} si la
     *         entidad o su clave es nula, o si no existe dicha clave.
     */
    public boolean actualizar(T elemento) {
        if (elemento == null || claveDe(elemento) == null) {
            return false;
        }
        if (!elementos.containsKey(claveDe(elemento))) {
            return false;
        }
        elementos.put(claveDe(elemento), elemento);
        return true;
    }

    /**
     * Elimina la entidad cuya clave se indica.
     *
     * @param clave clave única de la entidad a eliminar.
     * @return {@code true} si la entidad fue eliminada, {@code false} si la
     *         clave es nula o no existe.
     */
    public boolean eliminar(String clave) {
        if (clave == null) {
            return false;
        }
        return elementos.remove(clave) != null;
    }

    /**
     * Busca una entidad por su clave única.
     *
     * @param clave clave de la entidad a localizar.
     * @return la entidad encontrada o {@code null} si no existe o la clave es nula.
     */
    public T buscar(String clave) {
        if (clave == null) {
            return null;
        }
        return elementos.get(clave);
    }

    /**
     * Devuelve todas las entidades almacenadas ordenadas por su clave.
     *
     * @return lista no modificable con todas las entidades registradas.
     */
    public List<T> obtenerTodos() {
        List<T> resultado = new ArrayList<>(elementos.values());
        resultado.sort(Comparator.comparing(this::claveDe));
        return Collections.unmodifiableList(resultado);
    }

    /**
     * Filtra las entidades que cumplan el criterio indicado mediante un
     * {@link Predicate}. Método genérico reutilizable para aplicar filtros
     * arbitrarios sobre la colección almacenada.
     *
     * @param criterio predicado que determina si una entidad se incluye.
     * @return lista con las entidades que satisfacen el criterio, ordenadas
     *         por clave.
     */
    protected List<T> filtrar(Predicate<T> criterio) {
        return elementos.values().stream()
                .filter(criterio)
                .sorted(Comparator.comparing(this::claveDe))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}