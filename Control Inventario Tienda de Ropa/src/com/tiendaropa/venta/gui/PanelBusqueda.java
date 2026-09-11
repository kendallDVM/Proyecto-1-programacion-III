package com.tiendaropa.venta.gui;



import com.tiendaropa.venta.servicio.ServicioBusqueda;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.catalogo.modelo.Talla;
import javax.swing.*;
import java.awt.*;



/**
 * Panel de búsqueda con filtros para encontrar prendas.
 ------------------------------------------------------
 * Contiene filtros por tipo, talla y rango de precio.
 * Al presionar "Buscar", actualiza el panel de disponibles.
 --------------------------------------------------------
 * @author Liseth Briones
 */
public class PanelBusqueda extends JPanel {

                                    //Atributos
    private ServicioBusqueda servicioBusqueda;  // Servicio para búsquedas
    private VentanaCarrito ventanaCarrito;      // Referencia a ventana principal

    // Componentes de búsqueda
    private JComboBox<TipoPrenda> cmbTipo;      // ComboBox para tipo de prenda
    private JComboBox<Talla> cmbTalla;          // ComboBox para talla
    private JTextField txtPrecioMin;            // Campo precio mínimo
    private JTextField txtPrecioMax;            // Campo precio máximo
    private JButton btnBuscar;                  // Botón para buscar


    // ========== CONSTRUCTOR ==========

    /**
     * Crea el panel de búsqueda con inyección de dependencias.
     ---------------------------------------------------------
     * @param servicioBusqueda Servicio de búsqueda
     * @param ventanaCarrito Referencia a la ventana principal
     */
    public PanelBusqueda(ServicioBusqueda servicioBusqueda, VentanaCarrito ventanaCarrito) {
        this.servicioBusqueda = servicioBusqueda;
        this.ventanaCarrito = ventanaCarrito;

        // Configurar el panel
        configurarPanel();

        // Crear componentes
        crearComponentes();
    }



    // ========== CONFIGURACIÓN DEL PANEL ==========

    /**
     * Configura las propiedades básicas del panel.
     */
    private void configurarPanel() {
        // Usar FlowLayout para organizar componentes en fila
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Fondo blanco
        setBackground(Color.WHITE);

        // Borde para mejor visualización
        setBorder(BorderFactory.createTitledBorder("Búsqueda de Prendas"));
    }

    // ========== CREACIÓN DE COMPONENTES ==========

    /**
     * Crea todos los componentes del panel de búsqueda.
     */
    private void crearComponentes() {
        // TIPO DE PRENDA
        JLabel lblTipo = new JLabel("Tipo:");
        cmbTipo = new JComboBox<>();
        cmbTipo.addItem(null);
        for (TipoPrenda tipo : TipoPrenda.values()) {
            cmbTipo.addItem(tipo);
        }

        // TALLA
        JLabel lblTalla = new JLabel("Talla:");
        cmbTalla = new JComboBox<>();
        cmbTalla.addItem(null);
        for (Talla talla : Talla.values()) {
            cmbTalla.addItem(talla);
        }

        // PRECIO MÍNIMO
        JLabel lblPrecioMin = new JLabel("Precio Min (¢):");
        txtPrecioMin = new JTextField(10);
        txtPrecioMin.setText("0");


        // PRECIO MÁXIMO
        JLabel lblPrecioMax = new JLabel("Precio Max (¢):");
        txtPrecioMax = new JTextField(10);
        txtPrecioMax.setText("0");

        // BOTÓN BUSCAR
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());  // Listener para el botón

        // Agregar componentes al panel
        add(lblTipo);
        add(cmbTipo);
        add(lblTalla);
        add(cmbTalla);
        add(lblPrecioMin);
        add(txtPrecioMin);
        add(lblPrecioMax);
        add(txtPrecioMax);
        add(btnBuscar);
    }

    // ========== LÓGICA DE BÚSQUEDA ==========

    /**
     * Se ejecuta cuando el usuario presiona el botón "Buscar".
     *
     * <p>Valida los campos de precio y actualiza el panel de prendas
     * disponibles invocando a la ventana principal.</p>
     */
    private void buscar() {

        // Validar precios (convertir de String a double)
        double precioMin = 0;
        double precioMax = 0;

        try {
            if (!txtPrecioMin.getText().isEmpty()) {
                precioMin = Double.parseDouble(txtPrecioMin.getText());
            }
            if (!txtPrecioMax.getText().isEmpty()) {
                precioMax = Double.parseDouble(txtPrecioMax.getText());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese precios válidos (números)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Actualizar panel de disponibles
        ventanaCarrito.actualizarDisponibles();
    }

    // ========== GETTERS (para que otros paneles accedan a los filtros) ==========

    /**
     * Devuelve el tipo de prenda seleccionado en el filtro.
     *
     * @return nombre del tipo de prenda, o {@code null} si no se seleccionó ninguno.
     */
    public String getTipo() {
        TipoPrenda tipo = (TipoPrenda) cmbTipo.getSelectedItem();
        return (tipo != null) ? tipo.name() : null;
    }

    /**
     * Devuelve la talla seleccionada en el filtro.
     *
     * @return nombre de la talla, o {@code null} si no se seleccionó ninguna.
     */
    public String getTalla() {
        Talla talla = (Talla) cmbTalla.getSelectedItem();
        return (talla != null) ? talla.name() : null;
    }

    /**
     * Devuelve el precio mínimo ingresado en el filtro.
     *
     * @return precio mínimo, o {@code 0} si el campo está vacío o es inválido.
     */
    public double getPrecioMin() {
        try {
            String valor = txtPrecioMin.getText();
            return (valor != null && !valor.isEmpty()) ? Double.parseDouble(valor) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Devuelve el precio máximo ingresado en el filtro.
     *
     * @return precio máximo, o {@code 0} si el campo está vacío o es inválido.
     */
    public double getPrecioMax() {
        try {
            String valor = txtPrecioMax.getText();
            return (valor != null && !valor.isEmpty()) ? Double.parseDouble(valor) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Devuelve el servicio de búsqueda asociado al panel.
     *
     * @return el {@link ServicioBusqueda} del panel.
     */
    public ServicioBusqueda getServicioBusqueda() {
        return servicioBusqueda;
    }

}
