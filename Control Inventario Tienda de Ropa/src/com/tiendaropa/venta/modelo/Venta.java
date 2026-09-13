package com.tiendaropa.venta.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una venta completada en la tienda de ropa de segunda mano.
 *
 * <p>Agrupa la fecha de la transacción, el código de factura, las líneas
 * (prendas) vendidas y los montos económicos asociados: subtotal, impuesto
 * (IVA) y total a pagar. Sirve como registro histórico para el módulo de
 * reportes.</p>
 */
public class Venta {

    /** Porcentaje del impuesto sobre el valor agregado (IVA). */
    public static final double PORCENTAJE_IVA = 0.13;

    /** Formato estándar para mostrar la fecha y hora de la venta. */
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /** Código único que identifica la factura de la venta. */
    private final String codigoFactura;

    /** Fecha y hora en que se concretó la venta. */
    private final LocalDateTime fecha;

    /** Líneas (prendas) incluidas en la venta. */
    private final List<LineaCarrito> lineas;

    /** Suma de los precios de las prendas antes de impuestos. */
    private final double subtotal;

    /** Monto del impuesto (IVA) aplicado sobre el subtotal. */
    private final double iva;

    /** Monto final a pagar (subtotal más IVA). */
    private final double total;

    /**
     * Construye una venta a partir del código de factura y las líneas vendidas.
     *
     * <p>La fecha se fija al momento de crear la venta y los montos se calculan
     * automáticamente a partir del subtotal de cada línea y del IVA.</p>
     *
     * @param codigoFactura código único de la factura.
     * @param lineas        líneas (prendas) que conforman la venta.
     */
    public Venta(String codigoFactura, List<LineaCarrito> lineas) {
        this.codigoFactura = codigoFactura;
        this.fecha = LocalDateTime.now();
        this.lineas = lineas == null
                ? new ArrayList<>()
                : new ArrayList<>(lineas);
        this.subtotal = calcularSubtotal(this.lineas);
        this.iva = this.subtotal * PORCENTAJE_IVA;
        this.total = this.subtotal + this.iva;
    }

    /**
     * Suma los subtotales de todas las líneas de la venta.
     *
     * @param lineas líneas cuyo subtotal se desea sumar.
     * @return suma de los subtotales de las líneas.
     */
    private double calcularSubtotal(List<LineaCarrito> lineas) {
        return lineas.stream()
                .mapToDouble(LineaCarrito::getSubtotal)
                .sum();
    }

    /**
     * Devuelve el código único de la factura.
     *
     * @return cadena con el código de factura de la venta.
     */
    public String getCodigoFactura() {
        return codigoFactura;
    }

    /**
     * Devuelve la fecha y hora en que se concretó la venta.
     *
     * @return fecha y hora de la venta.
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Devuelve la fecha formateada de forma legible (día/mes/año hora).
     *
     * @return cadena con la fecha y hora en formato {@code dd/MM/yyyy HH:mm:ss}.
     */
    public String getFechaFormateada() {
        return FORMATO_FECHA.format(fecha);
    }

    /**
     * Devuelve las líneas (prendas) incluidas en la venta.
     *
     * @return lista no modificable con las líneas vendidas.
     */
    public List<LineaCarrito> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    /**
     * Devuelve el subtotal de la venta (suma antes de impuestos).
     *
     * @return subtotal de la venta.
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Devuelve el monto del impuesto (IVA) aplicado.
     *
     * @return monto del IVA de la venta.
     */
    public double getIva() {
        return iva;
    }

    /**
     * Devuelve el total a pagar de la venta.
     *
     * @return total (subtotal más IVA) de la venta.
     */
    public double getTotal() {
        return total;
    }

    /**
     * Devuelve la cantidad de prendas incluidas en la venta.
     *
     * @return número de líneas (prendas) vendidas.
     */
    public int getCantidadPrendas() {
        return lineas.size();
    }

    /**
     * Devuelve una representación textual de la venta.
     *
     * @return cadena que resume la factura, la cantidad de prendas y el total.
     */
    @Override
    public String toString() {
        return "Venta{" +
                "codigoFactura='" + codigoFactura + '\'' +
                ", fecha=" + getFechaFormateada() +
                ", prendas=" + getCantidadPrendas() +
                ", total=" + total +
                '}';
    }
}