package com.tiendaropa.sincronizacion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Sincroniza varias instancias de la aplicación abiertas en la misma
 * computadora mediante sockets ({@link ServerSocket} / {@link Socket}).
 *
 * <p>La primera instancia que se abre crea un {@link ServerSocket} y queda
 * como "caja principal". Las siguientes se conectan a ella como clientes.
 * Cuando una instancia cambia datos (registra una venta o modifica el
 * catálogo) envía un aviso corto; la caja principal lo reenvía a las demás,
 * y cada instancia vuelve a consultar MySQL para actualizar su pantalla.</p>
 *
 * <p>Por los sockets solo viajan avisos ({@link #MENSAJE_VENTA} y
 * {@link #MENSAJE_CATALOGO}), nunca datos: la información sigue guardándose
 * únicamente en MySQL. Si no se puede abrir ni conectar el socket, la
 * aplicación funciona normalmente, sin sincronización.</p>
 *
 * <p>Cada conexión se atiende en su propio hilo, para no bloquear la interfaz.
 * Los oyentes se ejecutan en esos hilos: quien actualice componentes Swing
 * debe hacerlo con {@code SwingUtilities.invokeLater}.</p>
 */
public class ServicioSincronizacion {

    /** Puerto local usado para la comunicación entre instancias. */
    public static final int PUERTO = 5050;

    /** Aviso enviado cuando se registra una venta. */
    public static final String MENSAJE_VENTA = "VENTA";

    /** Aviso enviado cuando se crea, modifica o elimina una prenda. */
    public static final String MENSAJE_CATALOGO = "CATALOGO";

    /** Avisos aceptados; cualquier otro texto recibido se ignora. */
    private static final Set<String> MENSAJES_VALIDOS = Set.of(MENSAJE_VENTA, MENSAJE_CATALOGO);

    /** Tiempo máximo para intentar conectarse a la caja principal. */
    private static final int TIEMPO_CONEXION_MS = 500;

    /** Espera antes de intentar recuperar la sincronización tras una desconexión. */
    private static final int ESPERA_RECONEXION_MS = 1000;

    /** Funciones que reciben los avisos llegados de otras instancias. */
    private final List<Consumer<String>> oyentesMensajes = new CopyOnWriteArrayList<>();

    /** Funciones que reciben los cambios de estado de la sincronización. */
    private final List<Consumer<String>> oyentesEstado = new CopyOnWriteArrayList<>();

    /** Salidas hacia las instancias conectadas (solo en la caja principal). */
    private final List<PrintWriter> clientes = new CopyOnWriteArrayList<>();

    /** Socket servidor, si esta instancia es la caja principal. */
    private volatile ServerSocket servidor;

    /** Salida hacia la caja principal, si esta instancia es cliente. */
    private volatile PrintWriter salidaHaciaServidor;

    /** Descripción del estado actual, para mostrarla en la ventana. */
    private volatile String estado = "Sin sincronización";

    /**
     * Inicia la sincronización.
     *
     * <p>Primero intenta conectarse a una caja principal ya abierta; si no hay
     * ninguna, abre el {@link ServerSocket} y queda como caja principal. Si
     * nada de eso es posible, la aplicación continúa sin sincronizar.</p>
     */
    public void iniciar() {
        if (conectarComoCliente()) {
            return;
        }
        if (iniciarComoServidor()) {
            return;
        }
        // Otra instancia abrió el servidor al mismo tiempo: se intenta conectar a ella
        if (!conectarComoCliente()) {
            cambiarEstado("Sin sincronización");
        }
    }

    /**
     * Registra una función que recibe los avisos enviados por otras instancias.
     *
     * @param oyente función que recibe el aviso ({@link #MENSAJE_VENTA} o
     *               {@link #MENSAJE_CATALOGO}); se ejecuta en un hilo de red.
     */
    public void agregarOyente(Consumer<String> oyente) {
        oyentesMensajes.add(oyente);
    }

    /**
     * Registra una función que recibe los cambios de estado de la sincronización.
     *
     * @param oyente función que recibe la descripción del estado; se ejecuta
     *               en el hilo que produjo el cambio.
     */
    public void agregarOyenteEstado(Consumer<String> oyente) {
        oyentesEstado.add(oyente);
    }

    /**
     * Devuelve la descripción del estado actual de la sincronización.
     *
     * @return por ejemplo "Caja principal", "Conectada a la caja principal"
     *         o "Sin sincronización".
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Avisa a las demás instancias que hubo un cambio en los datos.
     *
     * <p>Si esta instancia es la caja principal, envía el aviso a todos los
     * clientes; si es cliente, lo envía a la caja principal, que lo reenvía.
     * Sin sincronización activa, no hace nada.</p>
     *
     * @param mensaje aviso a enviar ({@link #MENSAJE_VENTA} o {@link #MENSAJE_CATALOGO}).
     * @throws IllegalArgumentException si el aviso no es uno de los permitidos.
     */
    public void notificar(String mensaje) {
        if (!MENSAJES_VALIDOS.contains(mensaje)) {
            throw new IllegalArgumentException("Aviso de sincronización no válido: " + mensaje);
        }
        if (servidor != null) {
            reenviar(mensaje, null);
            return;
        }
        PrintWriter salida = salidaHaciaServidor;
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    /**
     * Intenta conectarse como cliente a la caja principal de esta computadora.
     *
     * @return {@code true} si la conexión se estableció.
     */
    private boolean conectarComoCliente() {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), PUERTO), TIEMPO_CONEXION_MS);
            salidaHaciaServidor = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (IOException excepcion) {
            cerrarSinError(socket);
            return false;
        }
        cambiarEstado("Conectada a la caja principal");
        iniciarHilo("sincronizacion-cliente", () -> {
            escuchar(socket, null);
            // Si la caja principal se cerró, se intenta recuperar la sincronización
            salidaHaciaServidor = null;
            recuperarSincronizacion();
        });
        return true;
    }

    /**
     * Abre el {@link ServerSocket} para que esta instancia sea la caja principal.
     *
     * <p>Solo acepta conexiones desde la misma computadora.</p>
     *
     * @return {@code true} si el servidor se abrió.
     */
    private boolean iniciarComoServidor() {
        try {
            servidor = new ServerSocket(PUERTO, 50, InetAddress.getLoopbackAddress());
        } catch (IOException excepcion) {
            return false;
        }
        cambiarEstado("Caja principal");
        iniciarHilo("sincronizacion-servidor", this::aceptarConexiones);
        return true;
    }

    /**
     * Acepta nuevas instancias mientras el servidor esté abierto.
     *
     * <p>Cada instancia conectada se atiende en un hilo propio.</p>
     */
    private void aceptarConexiones() {
        ServerSocket socketServidor = servidor;
        while (socketServidor != null && !socketServidor.isClosed()) {
            try {
                Socket socket = socketServidor.accept();
                PrintWriter salida = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                clientes.add(salida);
                iniciarHilo("sincronizacion-conexion", () -> {
                    escuchar(socket, salida);
                    clientes.remove(salida);
                });
            } catch (IOException excepcion) {
                // El servidor se cerró o la conexión falló al aceptarse
            }
        }
    }

    /**
     * Lee los avisos que llegan por un socket hasta que la conexión se cierra.
     *
     * <p>En la caja principal, cada aviso recibido se reenvía también a las
     * demás instancias conectadas.</p>
     *
     * @param socket conexión de la que se leen los avisos.
     * @param origen salida de esa misma conexión, para no devolverle su propio
     *               aviso; {@code null} si esta instancia es cliente.
     */
    private void escuchar(Socket socket, PrintWriter origen) {
        try (socket;
             BufferedReader entrada = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                String mensaje = linea.trim();
                if (!MENSAJES_VALIDOS.contains(mensaje)) {
                    continue; // Se ignora cualquier texto desconocido
                }
                if (origen != null) {
                    reenviar(mensaje, origen);
                }
                for (Consumer<String> oyente : oyentesMensajes) {
                    oyente.accept(mensaje);
                }
            }
        } catch (IOException excepcion) {
            // La conexión se cerró
        }
    }

    /**
     * Envía un aviso a todas las instancias conectadas, salvo a la indicada.
     *
     * @param mensaje aviso a enviar.
     * @param excepto salida a la que no se envía el aviso, o {@code null} para
     *                enviarlo a todas.
     */
    private void reenviar(String mensaje, PrintWriter excepto) {
        for (PrintWriter cliente : clientes) {
            if (cliente != excepto) {
                cliente.println(mensaje);
            }
        }
    }

    /**
     * Intenta volver a sincronizar después de perder la caja principal.
     *
     * <p>Tras una breve espera, vuelve a conectarse o, si ya no hay caja
     * principal, se convierte en ella.</p>
     */
    private void recuperarSincronizacion() {
        cambiarEstado("Sin sincronización");
        try {
            Thread.sleep(ESPERA_RECONEXION_MS);
        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
            return;
        }
        iniciar();
    }

    /**
     * Actualiza el estado y avisa a los oyentes de estado.
     *
     * @param nuevoEstado descripción del nuevo estado.
     */
    private void cambiarEstado(String nuevoEstado) {
        estado = nuevoEstado;
        for (Consumer<String> oyente : oyentesEstado) {
            oyente.accept(nuevoEstado);
        }
    }

    /**
     * Inicia un hilo en segundo plano que no impide cerrar la aplicación.
     *
     * @param nombre nombre del hilo, útil para depurar.
     * @param tarea  tarea que ejecutará el hilo.
     */
    private static void iniciarHilo(String nombre, Runnable tarea) {
        Thread hilo = new Thread(tarea, nombre);
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Cierra un socket ignorando los errores de cierre.
     *
     * @param socket socket que se desea cerrar.
     */
    private static void cerrarSinError(Socket socket) {
        try {
            socket.close();
        } catch (IOException excepcion) {
            // No es relevante si falla el cierre
        }
    }
}
