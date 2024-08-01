package Admin;

import org.mindrot.jbcrypt.BCrypt;

public class ManejarHash {
    // Método para generar un hash de la contraseña
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Método para verificar la contraseña ingresada con el hash almacenado
    public static boolean verifContra(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}