package com.tiendaropa.venta.gui;


import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.modelo.LineaCarrito;
import com.tiendaropa.venta.servicio.ServicioBusqueda;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Panel que muestra tabla de prendas disponibles y permite agregar al carrito.
 --------------------------------------------------------------------------
 * Obtiene las prendas del servicio de búsqueda según los filtros.
 * Permite seleccionar y agregar prendas al carrito.
 ------------------------
 * @author Liseth Briones
 */
public class PanelDisponibles extends JPanel{

    // ========== ATRIBUTOS ==========

    private Carrito carrito;                    // Referencia al carrito
    private VentanaCarrito ventanaCarrito;      // Referencia a ventana principal
    private PanelBusqueda panelBusqueda;        // Referencia al panel de búsqueda

    private JTable tblPrendas;                  // Tabla de prendas
    private DefaultTableModel modeloTabla;      // Modelo de datos de la tabla
    private JButton btnAgregar;                 // Botón para agregar al carrito

    /** Etiqueta que muestra el resultado de la búsqueda. */
    private JLabel lblResultado;

    /** Lista de prendas mostradas actualmente, en orden de las filas. */
    private List<Prenda> prendasActuales = new ArrayList<>();

    /** Número de la carga más reciente; permite descartar resultados de búsquedas anteriores. */
    private int cargaActual = 0;



    // ========== CONSTRUCTOR ==========

    /**
     * Crea el panel de disponibles.
     *
     * @param carrito Carrito del cliente
     * @param ventanaCarrito Referencia a ventana principal
     */
    public PanelDisponibles(Carrito carrito, VentanaCarrito ventanaCarrito) {
        this.carrito = carrito;
        this.ventanaCarrito = ventanaCarrito;

        // Configurar el panel
        configurarPanel();

        // Crear componentes
        crearComponentes();

        // Organizar componentes
        organizarComponentes();
    }


    // ========== CONFIGURACIÓN DEL PANEL ==========

    /**
     * Configura las propiedades básicas del panel.
     */
    private void configurarPanel() {
        // Usar BorderLayout
        setLayout(new BorderLayout(5, 5));

        // Fondo blanco
        setBackground(Color.WHITE);

        // Borde con título
        setBorder(BorderFactory.createTitledBorder("Prendas Disponibles"));
    }


    // ========== CREACIÓN DE COMPONENTES ==========

    /**
     * Crea la tabla y el botón de agregar.
     */

    private void crearComponentes() {
        // CREAR TABLA
        crearTabla();

        // BOTÓN AGREGAR
        btnAgregar = new JButton("Agregar al Carrito");
        btnAgregar.addActionListener(e -> agregarAlCarrito());
    }

    /**
     * Crea la tabla con columnas de prendas.
     */
    private void crearTabla() {
        // Nombres de columnas
        String[] columnas = {"Código", "Tipo", "Talla", "Estado", "Precio (¢)"};

        // Crear modelo de tabla vacío
        modeloTabla = new DefaultTableModel(columnas, 0) {
            /**
             * Impide editar las celdas: la tabla solo muestra prendas.
             *
             * @param row    fila de la celda.
             * @param column columna de la celda.
             * @return siempre {@code false}.
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        // Crear tabla con el modelo
        tblPrendas = new JTable(modeloTabla);

        // Configurar ancho de columnas
        tblPrendas.getColumnModel().getColumn(0).setPreferredWidth(80);   // Código
        tblPrendas.getColumnModel().getColumn(1).setPreferredWidth(100);  // Tipo
        tblPrendas.getColumnModel().getColumn(2).setPreferredWidth(60);   // Talla
        tblPrendas.getColumnModel().getColumn(3).setPreferredWidth(100);  // Estado
        tblPrendas.getColumnModel().getColumn(4).setPreferredWidth(100);  // Precio

        // Permitir solo seleccionar una fila a la vez
        tblPrendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    // ========== ORGANIZACIÓN DE COMPONENTES ==========

    /**
     * Organiza los componentes en el panel.
     */
    private void organizarComponentes() {
        // Etiqueta del resultado de la búsqueda en la parte superior
        lblResultado = new JLabel(" ");
        add(lblResultado, BorderLayout.NORTH);

        // Tabla en el centro (con scroll), aseguramos un alto mínimo
        JScrollPane scrollPane = new JScrollPane(tblPrendas);
        scrollPane.setMinimumSize(new Dimension(0, 220));
        add(scrollPane, BorderLayout.CENTER);

        // Botón en la parte inferior
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnAgregar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    // ========== LÓGICA DE BÚSQUEDA Y RECARGA ==========

    /**
     * Recarga la tabla con los resultados de la búsqueda.
     * Se llama desde VentanaCarrito cuando presionan "Buscar".
     *
     * <p>La consulta a la base de datos se ejecuta en segundo plano con un
     * {@link SwingWorker} para no congelar la ventana. Mientras tanto se
     * muestra un mensaje de carga y se deshabilita el botón de agregar.</p>
     *
     * @param conMensaje si es {@code true}, muestra un mensaje emergente
     *                   indicando cuántas prendas coincidieron con la búsqueda.
     */
    public void recargar(boolean conMensaje) {
        // Verificar que panelBusqueda está conectado
        if (panelBusqueda == null) {
            return;
        }

        // Obtener servicio de búsqueda
        ServicioBusqueda servicio = panelBusqueda.getServicioBusqueda();

        if (servicio == null) {
            return;
        }

        // Los filtros se leen aquí, en el hilo de Swing, antes de iniciar la consulta
        String tipo = panelBusqueda.getTipo();
        String talla = panelBusqueda.getTalla();
        String estado = panelBusqueda.getEstado();
        double precioMin = panelBusqueda.getPrecioMin();
        double precioMax = panelBusqueda.getPrecioMax();

        int numeroCarga = ++cargaActual;
        modeloTabla.setRowCount(0);
        prendasActuales = new ArrayList<>();
        lblResultado.setText("Cargando prendas desde la base de datos...");
        btnAgregar.setEnabled(false);

        new SwingWorker<List<Prenda>, Void>() {
            /**
             * Busca las prendas disponibles en MySQL, fuera del hilo de Swing.
             *
             * @return prendas que cumplen los filtros.
             */
            @Override
            protected List<Prenda> doInBackground() {
                // Consulta a MySQL en un hilo de trabajo, fuera del hilo de Swing
                return servicio.buscarAvanzado(tipo, talla, estado, precioMin, precioMax);
            }

            /**
             * Muestra las prendas encontradas, en el hilo de Swing.
             */
            @Override
            protected void done() {
                // done() se ejecuta en el hilo de Swing: aquí sí se puede tocar la tabla
                if (numeroCarga != cargaActual) {
                    return; // Ya se pidió una búsqueda más reciente
                }
                btnAgregar.setEnabled(true);
                try {
                    mostrarPrendas(get(), conMensaje);
                } catch (InterruptedException excepcion) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException excepcion) {
                    lblResultado.setText("No se pudieron cargar las prendas. Verifique que MySQL esté disponible.");
                }
            }
        }.execute();
    }

    /**
     * Muestra en la tabla las prendas obtenidas de la búsqueda.
     *
     * @param prendas    prendas que cumplen los filtros.
     * @param conMensaje si es {@code true}, indica cuántas prendas se encontraron.
     */
    private void mostrarPrendas(List<Prenda> prendas, boolean conMensaje) {
        prendasActuales = prendas;

        // Mostrar el resultado de la búsqueda solo cuando el usuario pulsa "Filtrar"
        if (conMensaje) {
            if (prendasActuales.isEmpty()) {
                lblResultado.setText("No se encontraron prendas con los filtros seleccionados.");
            } else {
                lblResultado.setText("Búsqueda realizada: " + prendasActuales.size() + " prenda(s) encontrada(s).");
            }
        } else {
            lblResultado.setText(" ");
        }

        // Agregar cada prenda a la tabla
        for (Prenda prenda : prendasActuales) {
            agregarFilaPrenda(prenda);
        }
    }


    /**
     * Agrega la prenda seleccionada al carrito.
     */
    private void agregarAlCarrito() {
        // No modificar el carrito mientras se está registrando una venta
        if (ventanaCarrito.hayVentaEnCurso()) {
            JOptionPane.showMessageDialog(this,
                    "Espere a que termine de registrarse la venta actual.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que hay una fila seleccionada
        int filaSeleccionada = tblPrendas.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor selecciona una prenda",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que el índice sea válido en la lista de prendas actuales
        if (filaSeleccionada < 0 || filaSeleccionada >= prendasActuales.size()) {
            JOptionPane.showMessageDialog(this,
                    "La prenda seleccionada ya no está disponible",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener la prenda real por su índice en la lista de prendas actuales
        Prenda prendaSeleccionada = prendasActuales.get(filaSeleccionada);

        // Crear la línea del carrito
        LineaCarrito linea = new LineaCarrito(prendaSeleccionada);

        // Agregar la línea al carrito
        boolean agregada = carrito.agregarLinea(linea);

        if (agregada) {
            JOptionPane.showMessageDialog(this,
                    "Prenda agregada: " + prendaSeleccionada.getCodigo(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            // Actualizar panel del carrito
            ventanaCarrito.actualizarCarrito();

        } else {
            JOptionPane.showMessageDialog(this,
                    "La prenda ya se encuentra en el carrito",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // ========== MÉTODO PARA AGREGAR FILAS A LA TABLA ==========

    /**
     * Agrega una fila a la tabla con datos de una prenda.
     *
     * @param prenda Prenda a mostrar en la tabla
     */
    private void agregarFilaPrenda(Prenda prenda) {
        Object[] fila = {
                prenda.getCodigo(),
                prenda.getTipo().toString(),
                prenda.getTalla().toString(),
                prenda.getEstado().toString(),
                String.format("¢%,.2f", prenda.getPrecio())
        };
        modeloTabla.addRow(fila);
    }

    // ========== GETTERS ==========

    /**
     * Establece el panel de búsqueda del que se obtienen los filtros.
     *
     * @param panelBusqueda panel de búsqueda a asociar.
     */
    public void setPanelBusqueda(PanelBusqueda panelBusqueda) {
        this.panelBusqueda = panelBusqueda;
    }
}
