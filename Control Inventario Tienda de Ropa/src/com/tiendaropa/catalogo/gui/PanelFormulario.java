package com.tiendaropa.catalogo.gui;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.catalogo.repositorio.RepositorioPrendas;
import com.tiendaropa.catalogo.utilidades.ValidadorPrendas;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 * Panel de formulario que permite registrar, actualizar y eliminar prendas
 * del catálogo de la tienda de ropa de segunda mano.
 *
 * <p>Los datos se capturan mediante {@link JComboBox} para los atributos
 * enumerados (tipo, talla y estado) y {@link JTextField} para el código y
 * el precio. Cada acción valida los datos con {@link ValidadorPrendas}
 * antes de operar sobre el repositorio.</p>
 */
public class PanelFormulario extends JPanel {

    /** Repositorio sobre el que se realizan las operaciones CRUD. */
    private final RepositorioPrendas repositorio;

    /** Acción de notificación invocada tras una modificación de datos. */
    private final Runnable accionAlModificar;

    /** Campo para capturar el código único de la prenda. */
    private JTextField campoCodigo;

    /** Desplegable para seleccionar el tipo de prenda. */
    private JComboBox<TipoPrenda> comboTipo;

    /** Desplegable para seleccionar la talla. */
    private JComboBox<Talla> comboTalla;

    /** Desplegable para seleccionar el estado de conservación. */
    private JComboBox<EstadoPrenda> comboEstado;

    /** Campo para capturar el precio de venta. */
    private JTextField campoPrecio;

    /**
     * Construye el panel, inicializa los campos y conecta los botones.
     *
     * @param repositorio      repositorio contra el que se ejecutan las operaciones.
     * @param accionAlModificar acción ejecutada tras un cambio de datos.
     */
    public PanelFormulario(RepositorioPrendas repositorio, Runnable accionAlModificar) {
        this.repositorio = repositorio;
        this.accionAlModificar = accionAlModificar;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        construirFormulario();
        construirBotones();
    }

    /**
     * Construye la grilla de campos del formulario con sus etiquetas.
     */
    private void construirFormulario() {
        JPanel grilla = new JPanel(new GridLayout(5, 2, 10, 10));
        grilla.setBorder(BorderFactory.createTitledBorder("Datos de la prenda"));

        campoCodigo = new JTextField(10);
        comboTipo = new JComboBox<>(TipoPrenda.values());
        comboTalla = new JComboBox<>(Talla.values());
        comboEstado = new JComboBox<>(EstadoPrenda.values());
        campoPrecio = new JTextField(10);

        grilla.add(new JLabel("Código:"));
        grilla.add(campoCodigo);
        grilla.add(new JLabel("Tipo:"));
        grilla.add(comboTipo);
        grilla.add(new JLabel("Talla:"));
        grilla.add(comboTalla);
        grilla.add(new JLabel("Estado:"));
        grilla.add(comboEstado);
        grilla.add(new JLabel("Precio en colones:"));
        grilla.add(campoPrecio);

        add(grilla, BorderLayout.CENTER);
    }

    /**
     * Construye la fila de botones y enlaza cada uno con su acción.
     */
    private void construirBotones() {
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton botonCrear = new JButton("Crear");
        botonCrear.addActionListener(e -> accionCrear());

        JButton botonActualizar = new JButton("Actualizar");
        botonActualizar.addActionListener(e -> accionActualizar());

        JButton botonEliminar = new JButton("Eliminar");
        botonEliminar.addActionListener(e -> accionEliminar());

        JButton botonLimpiar = new JButton("Limpiar");
        botonLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(botonCrear);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonLimpiar);

        add(panelBotones, BorderLayout.SOUTH);
    }

/**
     * Registra una nueva prenda en el catálogo previa validación.
     *
     * <p>Construye la prenda desde el formulario, valida sus datos (código
     * único, campos obligatorios y precio positivo) y la añade al repositorio.
     * Tras el éxito limpia el formulario y notifica el cambio.</p>
     */
    private void accionCrear() {
        try {
            Prenda nueva = construirPrenda();
            ValidadorPrendas.validar(repositorio, nueva, true);
            if (repositorio.registrar(nueva)) {
                mostrarInfo("Prenda registrada con código '" + nueva.getCodigo() + "'.");
                limpiarFormulario();
                accionAlModificar.run();
            } else {
                mostrarError("No se pudo registrar la prenda. Revise los datos.");
            }
        } catch (IllegalArgumentException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    /**
     * Actualiza los datos de una prenda existente en el catálogo.
     *
     * <p>Construye la prenda desde el formulario y, tras validarla,
     * reemplaza los datos del registro cuyo código coincide en el
     * repositorio. Notifica el cambio si la actualización es correcta.</p>
     */
    private void accionActualizar() {
        try {
            Prenda actualizada = construirPrenda();
            ValidadorPrendas.validar(repositorio, actualizada, false);
            if (!repositorio.actualizar(actualizada)) {
                throw new IllegalArgumentException(
                        "No existe una prenda con el código '" + actualizada.getCodigo() + "'.");
            }
            mostrarInfo("Prenda actualizada correctamente.");
            limpiarFormulario();
            accionAlModificar.run();
        } catch (IllegalArgumentException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    /**
     * Elimina del catálogo la prenda cuyo código se indica en el formulario.
     *
     * <p>Si el código está vacío se muestra un mensaje de aviso. En caso
     * contrario se intenta eliminar y se notifica el resultado.</p>
     */
    private void accionEliminar() {
        String codigo = campoCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Indique el código de la prenda que desea eliminar.");
            return;
        }
        if (!repositorio.eliminar(codigo)) {
            mostrarError("No existe una prenda con el código '" + codigo + "'.");
            return;
        }
        mostrarInfo("Prenda con código '" + codigo + "' eliminada.");
        limpiarFormulario();
        accionAlModificar.run();
    }

    /**
     * Construye un objeto {@link Prenda} con los valores del formulario.
     *
     * @return nueva prenda con los datos capturados en el formulario.
     * @throws IllegalArgumentException si el precio no es un número válido.
     */
    private Prenda construirPrenda() {
        double precio;
        try {
            precio = Double.parseDouble(campoPrecio.getText().trim());
        } catch (NumberFormatException excepcion) {
            throw new IllegalArgumentException("El precio debe ser un número válido.");
        }
        return new Prenda(
                campoCodigo.getText().trim(),
                (TipoPrenda) comboTipo.getSelectedItem(),
                (Talla) comboTalla.getSelectedItem(),
                (EstadoPrenda) comboEstado.getSelectedItem(),
                precio
        );
    }

    /**
     * Carga los datos de una prenda seleccionada dentro del formulario.
     *
     * @param prenda prenda cuyos datos se desean mostrar en el formulario.
     */
    public void setPrenda(Prenda prenda) {
        if (prenda == null) {
            return;
        }
        campoCodigo.setText(prenda.getCodigo());
        comboTipo.setSelectedItem(prenda.getTipo());
        comboTalla.setSelectedItem(prenda.getTalla());
        comboEstado.setSelectedItem(prenda.getEstado());
        campoPrecio.setText(String.valueOf(prenda.getPrecio()));
    }

    /**
     * Limpia todos los campos del formulario dejándolos por defecto.
     */
    public void limpiarFormulario() {
        campoCodigo.setText("");
        campoPrecio.setText("");
        comboTipo.setSelectedIndex(0);
        comboTalla.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
    }

    /**
     * Muestra un mensaje de error al usuario mediante un diálogo.
     *
     * @param mensaje texto descriptivo que se desea presentar.
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de validación",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje informativo al usuario mediante un diálogo.
     *
     * @param mensaje texto descriptivo que se desea presentar.
     */
    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Catálogo",
                JOptionPane.INFORMATION_MESSAGE);
    }
}