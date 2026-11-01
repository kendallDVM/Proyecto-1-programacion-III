package com.tiendaropa.seguridad.servicio;

import com.tiendaropa.seguridad.modelo.Usuario;
import com.tiendaropa.seguridad.repositorio.IGestionUsuarios;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Verifica las credenciales de los usuarios de la aplicación.
 *
 * <p>Las contraseñas se protegen con PBKDF2 (HMAC-SHA256), un algoritmo de
 * derivación de claves incluido en Java. Cada hash usa una sal aleatoria y
 * muchas iteraciones, de modo que dos contraseñas iguales producen hashes
 * distintos y adivinarlas por fuerza bruta es costoso.</p>
 *
 * <p>Formato del hash guardado en la base de datos:
 * {@code pbkdf2_sha256$iteraciones$salBase64$hashBase64}.</p>
 */
public class ServicioAutenticacion {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "pbkdf2_sha256";
    private static final int ITERACIONES = 120_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;

    /** Repositorio del que se obtienen los usuarios. */
    private final IGestionUsuarios gestionUsuarios;

    /**
     * Crea el servicio con el repositorio de usuarios a utilizar.
     *
     * @param gestionUsuarios repositorio de usuarios.
     * @throws IllegalArgumentException si el repositorio es nulo.
     */
    public ServicioAutenticacion(IGestionUsuarios gestionUsuarios) {
        if (gestionUsuarios == null) {
            throw new IllegalArgumentException("El repositorio de usuarios no puede ser nulo.");
        }
        this.gestionUsuarios = gestionUsuarios;
    }

    /**
     * Verifica un nombre de usuario y una contraseña.
     *
     * <p>Al terminar, el contenido del arreglo de la contraseña se borra.</p>
     *
     * @param username   nombre de usuario escrito.
     * @param contrasena contraseña escrita.
     * @return el usuario autenticado, o {@code null} si el usuario no existe o
     *         la contraseña no coincide.
     * @throws com.tiendaropa.infraestructura.PersistenciaException si no se
     *         puede consultar la base de datos.
     */
    public Usuario autenticar(String username, char[] contrasena) {
        try {
            Usuario usuario = gestionUsuarios.buscarPorUsername(username);
            if (usuario == null || contrasena == null) {
                return null;
            }
            return verificarContrasena(contrasena, usuario.getPasswordHash()) ? usuario : null;
        } finally {
            if (contrasena != null) {
                Arrays.fill(contrasena, '\0');
            }
        }
    }

    /**
     * Genera el hash de una contraseña con una sal aleatoria nueva.
     *
     * <p>Se usa para crear usuarios; ver {@link GeneradorHashContrasena}.</p>
     *
     * @param contrasena contraseña en texto plano.
     * @return hash en el formato {@code pbkdf2_sha256$iteraciones$sal$hash}.
     */
    public static String generarHash(char[] contrasena) {
        byte[] sal = new byte[BYTES_SAL];
        new SecureRandom().nextBytes(sal);
        byte[] hash = derivar(contrasena, sal, ITERACIONES);
        Base64.Encoder base64 = Base64.getEncoder();
        return PREFIJO + "$" + ITERACIONES + "$" + base64.encodeToString(sal) + "$" + base64.encodeToString(hash);
    }

    /**
     * Comprueba si una contraseña corresponde a un hash guardado.
     *
     * <p>La comparación se hace en tiempo constante para no revelar
     * información a través del tiempo de respuesta.</p>
     *
     * @param contrasena    contraseña escrita por el usuario.
     * @param hashGuardado  hash almacenado en la base de datos.
     * @return {@code true} si la contraseña es correcta.
     */
    public static boolean verificarContrasena(char[] contrasena, String hashGuardado) {
        if (hashGuardado == null) {
            return false;
        }
        String[] partes = hashGuardado.split("\\$");
        if (partes.length != 4 || !PREFIJO.equals(partes[0])) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(contrasena, sal, iteraciones);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException excepcion) {
            return false; // Hash con formato inválido
        }
    }

    /**
     * Aplica PBKDF2 a una contraseña con la sal y las iteraciones indicadas.
     *
     * @param contrasena  contraseña en texto plano.
     * @param sal         sal aleatoria.
     * @param iteraciones número de iteraciones del algoritmo.
     * @return bytes del hash resultante.
     * @throws IllegalStateException si el algoritmo no está disponible en la JVM.
     */
    private static byte[] derivar(char[] contrasena, byte[] sal, int iteraciones) {
        PBEKeySpec especificacion = new PBEKeySpec(contrasena, sal, iteraciones, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacion).getEncoded();
        } catch (GeneralSecurityException excepcion) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña.", excepcion);
        } finally {
            especificacion.clearPassword();
        }
    }
}
