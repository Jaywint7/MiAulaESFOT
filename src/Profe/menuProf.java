package Profe;

import Inicio.Login;
import Profe.HistorialReserv.Historial;
import Profe.ReservAula.ReservaAula_Lab;
import Profe.VerDisponibilidad.Ver_Disponibil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * La clase `menuProf` representa el menú principal para el usuario con rol de Profesor en la aplicación.
 * <p>
 * Esta clase extiende {@link JFrame} y proporciona una interfaz gráfica para que el profesor pueda
 * gestionar reservas, ver disponibilidad y acceder a su historial de reservas.
 * </p>
 */
public class menuProf extends JFrame {
    private JPanel JPanel_menuAdmin;
    private JButton verReservasButton;
    private JButton reservarAulasYLaboratoriosButton;
    private JButton resumenDeReservasRecientesButton;
    private JButton flechabtn;
    private JButton button1;

    private int usuarioId;

    /**
     * Constructor de la clase `menuProf`.
     * <p>
     * Configura la ventana principal del menú para el profesor, incluyendo la inicialización de los componentes
     * de la interfaz gráfica y los manejadores de eventos para los botones.
     * </p>
     *
     * @param usuarioId El ID del usuario, que se utiliza para identificar al profesor en las acciones posteriores.
     */
    public menuProf(int usuarioId) {
        super("Menú Profesor");
        this.usuarioId = usuarioId;
        setSize(600, 500);
        setLocationRelativeTo(null);
        setContentPane(JPanel_menuAdmin);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configura el botón de regresar con un ícono
        flechabtn.setSize(20, 20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        flechabtn.setIcon(new ImageIcon(img));

        // Configura el comportamiento del botón de regresar para volver a la ventana de inicio de sesión
        flechabtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Inicio.Login login = new Login();
                login.setVisible(true);
                dispose();
            }
        });

        // Configura el comportamiento del botón de resumen de reservas recientes
        resumenDeReservasRecientesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Historial resumen = new Historial(usuarioId);
                resumen.setVisible(true);
                dispose();
            }
        });

        // Configura el comportamiento del botón para ver disponibilidad
        verReservasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ver_Disponibil ver = new Ver_Disponibil(usuarioId);
                ver.setVisible(true);
                dispose();
            }
        });

        // Configura el comportamiento del botón para reservar aulas y laboratorios
        reservarAulasYLaboratoriosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReservaAula_Lab reserva = new ReservaAula_Lab(usuarioId);
                reserva.setVisible(true);
                dispose();
            }
        });
    }
}
