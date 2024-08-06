package Inicio;

import Admin.GestionReservas;
import Estudi.menuEstudi;

/**
 * La clase `Main` es el punto de entrada para la aplicación.
 * <p>
 * Esta clase contiene el método principal {@code main}, que se ejecuta cuando se inicia la aplicación.
 * El método principal crea una instancia de la ventana de inicio de sesión y la hace visible para el usuario.
 * </p>
 */
public class Main {

    /**
     * El método principal de la aplicación.
     * <p>
     * Este método crea una instancia de la clase {@link Login} y establece su visibilidad en {@code true},
     * lo que muestra la ventana de inicio de sesión al usuario.
     * </p>
     *
     * @param args Los argumentos de línea de comandos, que no se utilizan en este caso.
     */
    public static void main(String[] args) {
        Login login = new Login();
        login.setVisible(true);
    }
}