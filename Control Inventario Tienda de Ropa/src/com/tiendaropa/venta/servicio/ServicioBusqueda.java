package com.tiendaropa.venta.servicio;

import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.catalogo.repositorio.IGestionPrendas;
import java.util.List;
import java.util.stream.Collectors;




/**
 * Implementación del servicio de búsqueda de prendas.
 *
 * Se conecta con Módulo 1 (IGestionPrendas) para obtener prendas.
 * Proporciona búsquedas simples y avanzadas con múltiples criterios.
 *
 * @author Liseth Briones
 */
public class ServicioBusqueda implements IBuscador {

    private IGestionPrendas gestionPrendas;  // Inyectado del Módulo 1


    /**
     * Crea un nuevo servicio de búsqueda con inyección de dependencias.
     *
     * @param gestionPrendas Implementación de IGestionPrendas (del Módulo 1)
     * @throws IllegalArgumentException si gestionPrendas es nula
     */
    public ServicioBusqueda(IGestionPrendas gestionPrendas) {
        if (gestionPrendas == null) {
            throw new IllegalArgumentException("IGestionPrendas no puede ser nula");
        }
        this.gestionPrendas = gestionPrendas;
    }



    /**
     * Obtiene todas las prendas disponibles del catálogo.
     *
     * @return {@code List<Prenda>} con todas las prendas.
     */
    @Override
    public List<Prenda> obtenerDisponibles() {
        return gestionPrendas.obtenerTodas();
    }

    /**
     * Busca prendas por tipo.
     * Convierte el String a enum TipoPrenda.
     *
     * @param tipo Tipo de prenda como String
     * @return {@code List<Prenda>} que coinciden con el tipo.
     */
    @Override
    public List<Prenda> buscarPorTipo(String tipo) {
        try {
            TipoPrenda tipoPrenda = TipoPrenda.valueOf(tipo.toUpperCase());
            return gestionPrendas.filtrarPorTipo(tipoPrenda);
        } catch (IllegalArgumentException e) {
            return List.of();  // Si tipo inválido, retorna lista vacía
        }
    }


    /**
     * Busca prendas por talla.
     * Convierte el String a enum Talla.
     *
     * @param talla Talla de prenda como String
     * @return {@code List<Prenda>} que coinciden con la talla.
     */
    @Override
    public List<Prenda> buscarPorTalla(String talla) {
        try {
            Talla tallaEnum = Talla.valueOf(talla.toUpperCase());
            return gestionPrendas.filtrarPorTalla(tallaEnum);
        } catch (IllegalArgumentException e) {
            return List.of();  // Si talla inválida, retorna lista vacía
        }
    }


    /**
     * Busca prendas en rango de precio.
     *
     * @param precioMinimo Precio mínimo en colones
     * @param precioMaximo Precio máximo en colones
     * @return {@code List<Prenda>} en ese rango.
     */
    @Override
    public List<Prenda> buscarPorPrecio(double precioMinimo, double precioMaximo) {
        return gestionPrendas.filtrarPorPrecio(precioMinimo, precioMaximo);
    }


    /**
     * Busca prendas con múltiples criterios simultáneamente.
     *
     * @param tipo Tipo de prenda (puede ser null)
     * @param talla Talla (puede ser null)
     * @param precioMin Precio mínimo (0 para ignorar)
     * @param precioMax Precio máximo (0 para ignorar)
     * @return {@code List<Prenda>} que cumplen TODOS los criterios.
     */


    @Override
    public List<Prenda> buscarAvanzado(String tipo, String talla, String estado,
                                       double precioMin, double precioMax) {
        // Obtiene todas las prendas
        List<Prenda> resultado = gestionPrendas.obtenerTodas();

        // Filtro 1: Por tipo (si se especifica)
        // Verifica si el usuario especificó un tipo de prenda
        // Si tipo es null o vacío (""), ignoramos este filtro
        if (tipo != null && !tipo.isEmpty()) {
            try {

                // Convierte el String a mayúsculas y lo busca en el enum TipoPrenda
                // Ejemplo: "pantalon" → "PANTALON" → TipoPrenda.PANTALON
                TipoPrenda tipoPrenda = TipoPrenda.valueOf(tipo.toUpperCase());

                //.stream() → convierte lista en flujo de datos
                // .filter() → mantiene solo las prendas cuyo tipo coincide
                // .collect() → convierte el flujo de vuelta a una lista
                resultado = resultado.stream()
                        .filter(p -> p.getTipo() == tipoPrenda)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {

                // Si el tipo no existe en el enum, retorna lista vacía
                // Ejemplo: usuario escribe "ZAPATO" que no existe
                return List.of();  // Tipo inválido
            }
        }

        // Filtro 2: Por talla (si se especifica)
        // Verifica si el usuario especificó una talla
        // Si talla es null o vacío (""), ignoramos este filtro
        if (talla != null && !talla.isEmpty()) {
            try {

                // Convierte el String a mayúsculas y lo busca en el enum Talla
                // Ejemplo: "m" → "M" → Talla.M
                Talla tallaEnum = Talla.valueOf(talla.toUpperCase());

                // Stream API: filtra prendas cuya talla coincide con tallaEnum
                resultado = resultado.stream()
                        .filter(p -> p.getTalla() == tallaEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {

                // Si la talla no existe en el enum, retorna lista vacía
                return List.of();  // Talla inválida
            }
        }

        // Filtro 3: Por estado (si se especifica)
        // Si estado es null o vacío (""), ignoramos este filtro
        if (estado != null && !estado.isEmpty()) {
            try {
                EstadoPrenda estadoEnum = EstadoPrenda.valueOf(estado.toUpperCase());
                resultado = resultado.stream()
                        .filter(p -> p.getEstado() == estadoEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                return List.of();  // Estado inválido
            }
        }

        // Filtro 4: Por precio mínimo (si se especifica)
        // Verifica si precioMin > 0 (significa que el usuario lo especificó)
        // Si precioMin = 0, ignoramos este filtro
        if (precioMin > 0) {
            // Stream API: filtra prendas cuyo precio sea >= precioMin (mayor o igual)
            resultado = resultado.stream()
                    .filter(p -> p.getPrecio() >= precioMin)
                    .collect(Collectors.toList());
        }

        // Filtro 5: Por precio máximo (si se especifica)
        // Verifica si precioMax > 0 (significa que el usuario lo especificó)
        // Si precioMax = 0, ignoramos este filtro
        if (precioMax > 0) {

            // Stream API: filtra prendas cuyo precio sea <= precioMax (menor o igual)
            resultado = resultado.stream()
                    .filter(p -> p.getPrecio() <= precioMax)
                    .collect(Collectors.toList());
        }
        // Retorna la lista FINAL después de aplicar TODOS los filtros especificados
        return resultado;
    }

}
