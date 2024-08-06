package Admin;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Clase para manejar la seguridad de las contraseñas utilizando hashing.
 * Utiliza la biblioteca BCrypt para generar hashes de contraseñas y verificar contraseñas.
 */
public class ManejarHash {

    /**
     * Genera un hash de la contraseña proporcionada utilizando BCrypt.
     *
     * @param plainPassword La contraseña en texto plano que se desea hashear.
     * @return El hash de la contraseña.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifica si la contraseña en texto plano coincide con el hash de contraseña almacenado.
     *
     * @param plainTextPassword La contraseña en texto plano proporcionada por el usuario.
     * @param hashedPassword El hash de la contraseña almacenado en la base de datos.
     * @return true si la contraseña coincide con el hash, false en caso contrario.
     */
    public static boolean verifContra(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            throw new IllegalArgumentException("La contraseña en texto plano y el hash de contraseña no pueden ser nulos.");
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}