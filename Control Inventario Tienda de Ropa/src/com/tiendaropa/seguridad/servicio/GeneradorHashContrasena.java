package com.tiendaropa.seguridad.servicio;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridLayout;

/**
 * Herramienta para crear usuarios de la aplicación.
 *
 * <p>Pide un nombre de usuario y una contraseña, y muestra la instrucción
 * {@code INSERT} con el hash de la contraseña, lista para ejecutarla en MySQL
 * Workbench con una cuenta administrativa. Así la contraseña nunca se escribe
 * en texto plano en la base de datos ni en el código.</p>
 */
public final class GeneradorHashContrasena {

    /**
     * Constructor privado: la clase solo se usa a través de {@link #main(String[])}.
     */
    private GeneradorHashContrasena() {
    }

    /**
     * Pide los datos del usuario y muestra la instrucción SQL para crearlo.
     *
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        JTextField campoUsuario = new JTextField(12);
        JPasswordField campoContrasena = new JPasswordField(12);
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Usuario:"));
        panel.add(campoUsuario);
        panel.add(new JLabel("Contraseña:"));
        panel.add(campoContrasena);

        int opcion = JOptionPane.showConfirmDialog(null, panel, "Crear usuario administrador",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        String usuario = campoUsuario.getText().trim();
        char[] contrasena = campoContrasena.getPassword();
        if (opcion != JOptionPane.OK_OPTION || usuario.isEmpty() || contrasena.length == 0) {
            return;
        }
        if (!usuario.matches("[A-Za-z0-9_.-]{1,50}")) {
            JOptionPane.showMessageDialog(null,
                    "El usuario solo puede tener letras, números, punto, guion y guion bajo (máximo 50).");
            return;
        }

        String hash = ServicioAutenticacion.generarHash(contrasena);
        java.util.Arrays.fill(contrasena, '\0');
        String sql = "INSERT INTO tienda_ropa.Usuario (username, password_hash, rol)\n"
                + "VALUES ('" + usuario + "', '" + hash + "', 'ADMIN');";
        JTextField resultado = new JTextField(sql.replace("\n", " "));
        resultado.setEditable(false);
        JOptionPane.showMessageDialog(null, new Object[]{
                "Copie esta instrucción y ejecútela en MySQL Workbench (como root):", resultado
        }, "Instrucción SQL", JOptionPane.INFORMATION_MESSAGE);
        System.out.println(sql);
    }
}
