package com.tiendaropa.seguridad.modelo;

/**
 * Cuenta de acceso a la aplicación (por ejemplo, el administrador de la tienda).
 *
 * <p>No debe confundirse con la cuenta {@code tienda_app} de MySQL: esta clase
 * representa a las personas que inician sesión en el programa. La contraseña
 * nunca se guarda en texto plano, solo su hash.</p>
 */
public class Usuario {

    /** Rol que permite entrar a la administración del catálogo. */
    public static final String ROL_ADMIN = "ADMIN";

    /** Nombre de usuario único. */
    private final String username;

    /** Hash de la contraseña, en el formato de {@code ServicioAutenticacion}. */
    private final String passwordHash;

    /** Rol del usuario, que define a qué partes del sistema puede entrar. */
    private final String rol;

    /**
     * Crea un usuario con sus datos tal como están en la base de datos.
     *
     * @param username     nombre de usuario.
     * @param passwordHash hash de la contraseña.
     * @param rol          rol del usuario, por ejemplo {@link #ROL_ADMIN}.
     */
    public Usuario(String username, String passwordHash, String rol) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    /**
     * Devuelve el nombre de usuario.
     *
     * @return nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Devuelve el hash de la contraseña.
     *
     * @return hash de la contraseña (nunca la contraseña original).
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Devuelve el rol del usuario.
     *
     * @return rol del usuario.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Indica si el usuario puede entrar a la administración.
     *
     * @return {@code true} si su rol es {@link #ROL_ADMIN}.
     */
    public boolean esAdministrador() {
        return ROL_ADMIN.equals(rol);
    }
}
