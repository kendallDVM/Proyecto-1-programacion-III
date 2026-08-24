package com.tiendaropa.catalogo.gui;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.catalogo.repositorio.RepositorioPrendas;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;

/**
 * Ventana principal del catálogo de la tienda de ropa de segunda mano.
 *
 * <p>Es el componente de más alto nivel de la interfaz. Integra el
 * {@link PanelFormulario} (para el alta, baja y modificación) y el
 * {@link PanelTabla} (para la consulta y filtrado). Además coordina la
 * selección de una fila para cargar sus datos en el formulario.</p>
 */
public class VentanaCatalogo extends JFrame {

    /** Repositorio único compartido por todos los paneles de la ventana. */
    private final RepositorioPrendas repositorio;

    /** Panel de formulario para capturar y editar prendas. */
    private PanelFormulario panelFormulario;

    /** Panel de tabla que lista y filtra las prendas. */
    private PanelTabla panelTabla;

    /**
     * Construye la ventana principal, organiza los paneles y carga los datos.
     */
    public VentanaCatalogo() {
        this.repositorio = new RepositorioPrendas();

        setTitle("Catálogo - Tienda de Ropa de Segunda Mano");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 540);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        construirPaneles();
        cargarDatosEjemplo();
    }

    /**
     * Crea los paneles de formulario y tabla y los integra en la ventana.
     */
    private void construirPaneles() {
        panelTabla = new PanelTabla(repositorio);
        panelFormulario = new PanelFormulario(repositorio, () -> panelTabla.actualizarTabla());

        add(panelFormulario, BorderLayout.WEST);
        add(panelTabla, BorderLayout.CENTER);

        panelTabla.getTabla().getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                Prenda seleccionada = panelTabla.obtenerPrendaSeleccionada();
                if (seleccionada != null) {
                    panelFormulario.setPrenda(seleccionada);
                }
            }
        });
    }

    /**
     * Inserta unas prendas de ejemplo para que la tabla no esté vacía
     * en el primer arranque y se pueda comprobar el funcionamiento.
     */
    private void cargarDatosEjemplo() {
        registrar(new Prenda("P-001", TipoPrenda.CAMISA, Talla.M, EstadoPrenda.BUEN_ESTADO, 12.50));
        registrar(new Prenda("P-002", TipoPrenda.PANTALON, Talla.L, EstadoPrenda.COMO_NUEVO, 18.00));
        registrar(new Prenda("P-003", TipoPrenda.VESTIDO, Talla.S, EstadoPrenda.ACEPTABLE, 14.75));
        registrar(new Prenda("P-004", TipoPrenda.SUETER, Talla.XL, EstadoPrenda.BUEN_ESTADO, 16.20));
        panelTabla.actualizarTabla();
    }

    /**
     * Registra una prenda de ejemplo ignorando duplicados.
     *
     * @param prenda prenda de ejemplo que se desea cargar.
     */
    private void registrar(Prenda prenda) {
        if (repositorio.buscarPorCodigo(prenda.getCodigo()) == null) {
            repositorio.registrar(prenda);
        }
    }

    /**
     * Punto de entrada de la aplicación. Lanza la ventana en el hilo de eventos.
     *
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception excepcion) {
                // Se mantiene el look and feel por defecto si falla el cambio.
            }
            new VentanaCatalogo().setVisible(true);
        });
    }
}