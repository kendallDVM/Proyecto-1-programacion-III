package com.tiendaropa.catalogo.gui;

import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.repositorio.RepositorioPrendas;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    /** Acción para volver a la tienda al cerrar la administración. */
    private final Runnable accionAlVolver;

    /** Panel de formulario para capturar y editar prendas. */
    private PanelFormulario panelFormulario;

    /** Panel de tabla que lista y filtra las prendas. */
    private PanelTabla panelTabla;

    /**
     * Construye la ventana principal, organiza los paneles y carga los datos.
     *
     * @param repositorio repositorio compartido de prendas que usarán los paneles.
     * @param accionAlVolver acción que se ejecuta al cerrar la administración
     *                       para volver a la tienda.
     */
    public VentanaCatalogo(RepositorioPrendas repositorio, Runnable accionAlVolver) {

        this.repositorio = repositorio;
        this.accionAlVolver = accionAlVolver;

        setTitle("Administración - Tienda de Ropa de Segunda Mano");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 540);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        construirPaneles();

        // Si se cierra esta ventana (con la X), volver a la tienda
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                if (accionAlVolver != null) {
                    accionAlVolver.run();
                }
            }
        });
    }

    /**
     * Crea los paneles de formulario y tabla y los integra en la ventana.
     */
    private void construirPaneles() {
        panelTabla = new PanelTabla(repositorio);
        panelFormulario = new PanelFormulario(repositorio, () -> panelTabla.actualizarTabla());

        add(panelFormulario, BorderLayout.WEST);
        add(panelTabla, BorderLayout.CENTER);

        JButton botonVolver = new JButton("Volver a la Tienda");
        botonVolver.addActionListener(e -> volverATienda());
        add(botonVolver, BorderLayout.SOUTH);

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
     * Cierra la administración y vuelve a mostrar la tienda.
     */
    private void volverATienda() {
        dispose();
        if (accionAlVolver != null) {
            accionAlVolver.run();
        }
    }
}