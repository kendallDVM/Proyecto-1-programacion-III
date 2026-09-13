package com.tiendaropa.catalogo.gui;

import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.repositorio.RepositorioPrendas;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel que muestra las prendas del catálogo en una tabla {@link JTable}
 * e incluye un campo de texto para filtrar los resultados.
 *
 * <p>El filtrado se delega en el repositorio a través del método
 * {@code buscarPorTexto}, que internamente hace uso de Java Streams.</p>
 */
public class PanelTabla extends JPanel {

    /** Repositorio del cual se obtienen los datos a mostrar. */
    private final RepositorioPrendas repositorio;

    /** Campo de texto para introducir el criterio de filtrado. */
    private JTextField campoFiltro;

    /** Tabla que visualiza las prendas registradas. */
    private JTable tabla;

    /** Modelo de tabla editable con las columnas del catálogo. */
    private DefaultTableModel modelo;

    /** Lista de prendas cargadas, sincronizada en orden con las filas. */
    private List<Prenda> prendasCargadas = new ArrayList<>();

    /**
     * Construye el panel, inicializa sus componentes y carga los datos.
     *
     * @param repositorio repositorio contra el que se consultan las prendas.
     */
    public PanelTabla(RepositorioPrendas repositorio) {
        this.repositorio = repositorio;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Prendas del catálogo"));

        construirFiltro();
        construirTabla();

        add(construirBarraFiltro(), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        actualizarTabla();
    }

    /**
     * Inicializa los componentes relacionados con el filtro de búsqueda.
     */
    private void construirFiltro() {
        campoFiltro = new JTextField(18);
        campoFiltro.setToolTipText("Filtrar por código, tipo, talla, estado o precio");
        campoFiltro.addActionListener(evento -> actualizarTabla());
    }

    /**
     * Construye la tabla y su modelo de columnas para las prendas.
     */
    private void construirTabla() {
        modelo = new DefaultTableModel(new Object[]{"Código", "Tipo", "Talla", "Estado", "Precio"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabla.setFillsViewportHeight(true);
    }

    /**
     * Arma la barra superior que contiene el campo de búsqueda y sus botones.
     *
     * @return panel con el filtro y los botones Filtrar / Limpiar.
     */
    private JPanel construirBarraFiltro() {
        JPanel barra = new JPanel(new BorderLayout(8, 8));

        JButton botonFiltrar = new JButton("Filtrar");
        botonFiltrar.addActionListener(evento -> actualizarTabla());

        JButton botonLimpiar = new JButton("Limpiar");
        botonLimpiar.addActionListener(evento -> {
            campoFiltro.setText("");
            actualizarTabla();
        });

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonFiltrar);
        panelBotones.add(botonLimpiar);

        barra.add(new JLabel("Buscar: "), BorderLayout.WEST);
        barra.add(campoFiltro, BorderLayout.CENTER);
        barra.add(panelBotones, BorderLayout.EAST);
        return barra;
    }

    /**
     * Actualiza la tabla con las prendas que coinciden con el filtro actual.
     */
    public void actualizarTabla() {
        String filtro = campoFiltro.getText().trim();
        List<Prenda> lista = filtro.isEmpty()
                ? repositorio.obtenerTodas()
                : repositorio.buscarPorTexto(filtro);
        cargarLista(lista);
    }

    /**
     * Carga (o recarga) una lista de prendas dentro de la tabla.
     *
     * @param lista lista de prendas a visualizar.
     */
    private void cargarLista(List<Prenda> lista) {
        prendasCargadas = new ArrayList<>(lista);
        modelo.setRowCount(0);
        for (Prenda prenda : lista) {
            modelo.addRow(new Object[]{
                    prenda.getCodigo(),
                    prenda.getTipo(),
                    prenda.getTalla(),
                    prenda.getEstado(),
                    prenda.getPrecio()
            });
        }
    }

    /**
     * Devuelve la prenda correspondiente a la fila seleccionada de la tabla.
     *
     * @return la prenda seleccionada o {@code null} si no hay ninguna selección.
     */
    public Prenda obtenerPrendaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= prendasCargadas.size()) {
            return null;
        }
        return prendasCargadas.get(fila);
    }

    /**
     * Devuelve la tabla interna para permitir configurarla desde el exterior.
     *
     * @return la tabla de prendas del panel.
     */
    public JTable getTabla() {
        return tabla;
    }
}