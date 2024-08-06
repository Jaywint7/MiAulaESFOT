package Inicio;

import org.mindrot.jbcrypt.BCrypt;

/**
 * La clase `ManejarHash` proporciona métodos para gestionar la encriptación de contraseñas utilizando
 * el algoritmo de hash BCrypt.
 * <p>
 * BCrypt es un algoritmo de hash de contraseñas diseñado para proteger contra ataques de fuerza bruta
 * mediante la incorporación de un "sal" (salt) y el ajuste de un costo computacional.
 * </p>
 */
public class ManejarHash {

    /**
     * Genera un hash de la contraseña proporcionada.
     * <p>
     * Este método utiliza el algoritmo BCrypt para crear un hash seguro de la contraseña. El hash
     * incluye un "sal" (salt) aleatorio, lo que aumenta la seguridad.
     * </p>
     *
     * @param plainPassword La contraseña en texto plano que se desea encriptar.
     * @return Un string que representa el hash de la contraseña.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifica si la contraseña proporcionada coincide con el hash almacenado.
     * <p>
     * Este método compara la contraseña en texto plano con el hash utilizando el algoritmo BCrypt.
     * Retorna verdadero si la contraseña es correcta, o falso en caso contrario.
     * </p>
     *
     * @param plainTextPassword La contraseña en texto plano que se desea verificar.
     * @param hashedPassword El hash de la contraseña almacenado que se desea comparar.
     * @return {@code true} si la contraseña en texto plano coincide con el hash almacenado,
     *         {@code false} en caso contrario.
     */
    public static boolean verifContra(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}