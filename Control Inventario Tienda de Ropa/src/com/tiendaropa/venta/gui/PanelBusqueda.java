package com.tiendaropa.venta.gui;



import com.tiendaropa.venta.servicio.ServicioBusqueda;
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
    private JComboBox<String> cmbTipo;          // ComboBox para tipo de prenda
    private JComboBox<String> cmbTalla;         // ComboBox para talla
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

        // Organizar componentes en el panel
        organizarComponentes();
    }



    // ========== CONFIGURACIÓN DEL PANEL ==========

    //Configura las propiedades básicas del panel.
    private void configurarPanel() {
        // Usar FlowLayout para organizar componentes en fila
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Fondo blanco
        setBackground(Color.WHITE);

        // Borde para mejor visualización
        setBorder(BorderFactory.createTitledBorder("Búsqueda de Prendas"));
    }

    // ========== CREACIÓN DE COMPONENTES ==========


     // Crea todos los componentes de búsqueda
    private void crearComponentes() {
        // TIPO DE PRENDA
        JLabel lblTipo = new JLabel("Tipo:");
        cmbTipo = new JComboBox<>();
        cmbTipo.addItem("");  // Opción vacía (sin filtro)
        cmbTipo.addItem("PANTALON");
        cmbTipo.addItem("BLUSA");
        cmbTipo.addItem("CHAQUETA");
        cmbTipo.addItem("FALDA");
        cmbTipo.addItem("VESTIDO");
        cmbTipo.addItem("CAMISETA");

        // TALLA
        JLabel lblTalla = new JLabel("Talla:");
        cmbTalla = new JComboBox<>();
        cmbTalla.addItem("");  // Opción vacía (sin filtro)
        cmbTalla.addItem("XS");
        cmbTalla.addItem("S");
        cmbTalla.addItem("M");
        cmbTalla.addItem("L");
        cmbTalla.addItem("XL");
        cmbTalla.addItem("XXL");

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

    // ========== ORGANIZACIÓN DE COMPONENTES ==========

    /**
     * Los componentes ya están organizados en crearComponentes(),
     * así que este método solo es un placeholder por ahora.
     */
    private void organizarComponentes() {
        // Los componentes ya fueron agregados en crearComponentes()
        // usando el FlowLayout del panel
    }

    // ========== LÓGICA DE BÚSQUEDA ==========

    /**
     * Se ejecuta cuando el usuario presiona el botón "Buscar".
     * Obtiene los valores de los filtros y llama a ventanaCarrito
     * para actualizar el panel de disponibles.
     */

    private void buscar() {
        // Obtener valores de los filtros
        String tipo = (String) cmbTipo.getSelectedItem();
        String talla = (String) cmbTalla.getSelectedItem();

        // Obtener precios (convertir de String a double)
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

        // Guardar los filtros en variables de instancia para que PanelDisponibles los use
        // (veremos esto cuando creemos PanelDisponibles)

        // Actualizar panel de disponibles
        ventanaCarrito.actualizarDisponibles();
    }

    // ========== GETTERS (para que otros paneles accedan a los filtros) ==========


    // Retorna el tipo de prenda seleccionado.
    public String getTipo() {
        String tipo = (String) cmbTipo.getSelectedItem();
        return (tipo != null && !tipo.isEmpty()) ? tipo : null;
    }

     //Retorna la talla seleccionada.
    public String getTalla() {
        String talla = (String) cmbTalla.getSelectedItem();
        return (talla != null && !talla.isEmpty()) ? talla : null;
    }


    //Retorna el precio mínimo ingresado.
    public double getPrecioMin() {
        try {
            String valor = txtPrecioMin.getText();
            return (valor != null && !valor.isEmpty()) ? Double.parseDouble(valor) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    //Retorna el precio máximo ingresado.
    public double getPrecioMax() {
        try {
            String valor = txtPrecioMax.getText();
            return (valor != null && !valor.isEmpty()) ? Double.parseDouble(valor) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
