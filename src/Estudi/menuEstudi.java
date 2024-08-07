package Estudi;

import Estudi.HistoReserva.historialReser;
import Estudi.ReservaAula.ReservarAula;
import Estudi.VerDisponi.verMisReservas;
import Inicio.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * La clase `menuEstudi` representa el menú principal para el usuario con rol de Estudiante en la aplicación.
 * <p>
 * Esta clase extiende {@link JFrame} y proporciona una interfaz gráfica para que el estudiante pueda
 * gestionar reservas, ver sus reservas y acceder a su historial de reservas.
 * </p>
 */
public class menuEstudi extends JFrame {
    private JPanel menu_Estudiante;
    private JButton verReservasButton;
    private JButton reservarAulasButton;
    private JButton resumenDeReservasRecientesButton;
    private JButton flechabtn;
    private JPanel JPanel_menuAdmin;
    private JButton button1;

    private int usuarioId;

    /**
     * Constructor de la clase `menuEstudi`.
     * <p>
     * Configura la ventana principal del menú para el estudiante, incluyendo la inicialización de los componentes
     * de la interfaz gráfica y los manejadores de eventos para los botones.
     * </p>
     *
     * @param usuarioId El ID del usuario, que se utiliza para identificar al estudiante en las acciones posteriores.
     */
    public menuEstudi(int usuarioId) {
        super("Menú Estudiante");
        this.usuarioId = usuarioId;
        setSize(600, 500);
        setLocationRelativeTo(null);
        setContentPane(menu_Estudiante);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configura el botón de regresar con un ícono
        flechabtn.setSize(20, 20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        flechabtn.setIcon(new ImageIcon(img));

        // Configura el comportamiento del botón de resumen de reservas recientes
        resumenDeReservasRecientesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Estudi.HistoReserva.historialReser historial = new historialReser(usuarioId);
                historial.setVisible(true);
                dispose();
            }
        });

        // Configura el comportamiento del botón para ver reservas
        verReservasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Estudi.VerDisponi.verMisReservas ver = new verMisReservas(usuarioId);
                ver.setVisible(true);
                dispose();
            }
        });

        // Configura el comportamiento del botón para reservar aulas
        reservarAulasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Estudi.ReservaAula.ReservarAula reserva = new ReservarAula(usuarioId);
                reserva.setVisible(true);
                dispose();
            }
        });

        // Configura el comportamiento del botón de regresar para volver a la ventana de inicio de sesión
        flechabtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Inicio.Login login  = new Login();
                login.setVisible(true);
                dispose();
            }
        });
    }
}
