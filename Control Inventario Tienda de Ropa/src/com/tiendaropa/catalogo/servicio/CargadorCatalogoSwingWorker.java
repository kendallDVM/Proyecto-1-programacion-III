package com.tiendaropa.catalogo.servicio;

import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.repositorio.IGestionPrendas;

import java.util.List;
import javax.swing.SwingWorker;

/**
 * {@link SwingWorker} que carga el catálogo de prendas desde la base de datos
 * en un hilo de fondo, sin bloquear el hilo de eventos de Swing (EDT).
 *
 * <p>La operación de consulta a MySQL se ejecuta en {@code doInBackground()},
 * fuera del EDT. El resultado se entrega en {@code done()}, que sí se ejecuta
 * en el EDT, permitiendo actualizar la interfaz de forma segura.</p>
 *
 * <p>Ejemplo de uso:
 * <pre>{@code
 * CargadorCatalogoSwingWorker worker = new CargadorCatalogoSwingWorker(repositorio);
 * worker.execute();
 * }</pre></p>
 */
public class CargadorCatalogoSwingWorker extends SwingWorker<List<Prenda>, Void> {

    private final IGestionPrendas repositorio;

    private List<Prenda> resultado;

    private Throwable error;

    /**
     * Crea el worker para cargar prendas desde el repositorio dado.
     *
     * @param repositorio repositorio de prendas (puede ser la implementación JDBC).
     */
    public CargadorCatalogoSwingWorker(IGestionPrendas repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Ejecuta la consulta a la base de datos en un hilo de trabajo.
     *
     * @return lista de prendas obtenidas del repositorio.
     * @throws Exception si la consulta falla.
     */
    @Override
    protected List<Prenda> doInBackground() throws Exception {
        return repositorio.obtenerTodas();
    }

    /**
     * Se ejecuta en el EDT al completar la tarea de fondo.
     * Almacena el resultado de {@code get()} o la excepción capturada,
     * permitiendo que el código que creo el worker acceda a ellos de forma
     * segura desde el EDT.
     */
    @Override
    protected void done() {
        try {
            resultado = get();
        } catch (Exception e) {
            error = e;
        }
    }

    /**
     * Devuelve el resultado de la consulta ejecutada en segundo plano.
     * Debe invocarse desde el EDT, tras completar el worker.
     *
     * @return lista de prendas, o {@code null} si la tarea fallo.
     */
    public List<Prenda> getResultado() {
        return resultado;
    }

    /**
     * Devuelve la excepción capturada durante la ejecucion en segundo plano,
     * si la hubo.
     *
     * @return throwable con el error, o {@code null} si la tarea finalizo con exito.
     */
    public Throwable getError() {
        return error;
    }
}
