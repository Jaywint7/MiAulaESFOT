package Admin;

import Inicio.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase que representa el menú principal del administrador.
 * Permite al administrador acceder a diferentes secciones de la aplicación.
 */
public class menuAdmin extends JFrame {
    private JButton gestionDeUsuariosButton;
    private JButton gestionDeReservasButton;
    private JButton gestionDeAulasYButton;
    private JPanel JPanel_menuAdmin;
    private JButton flechabtn;
    private JButton button1;
    private JLabel flechalbl;

    /**
     * Constructor de la clase menuAdmin.
     * Configura la interfaz gráfica y añade los listeners a los botones.
     */
    public menuAdmin() {
        super("Menú Administrador");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setContentPane(JPanel_menuAdmin);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configurarIconoFlechaAtras();

        flechabtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir la ventana de login y cerrar la ventana actual
                Inicio.Login login = new Login();
                login.setVisible(true);
                dispose();
            }
        });

        gestionDeUsuariosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir la ventana de gestión de usuarios y cerrar la ventana actual
                Admin.GestionUsu gestion = new GestionUsu();
                gestion.setVisible(true);
                dispose();
            }
        });

        gestionDeReservasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir la ventana de gestión de reservas y cerrar la ventana actual
                Admin.GestionReservas reservas = new GestionReservas();
                reservas.setVisible(true);
                dispose();
            }
        });

        gestionDeAulasYButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir la ventana de gestión de aulas y laboratorios y cerrar la ventana actual
                Admin.GestionAul_Lab aulLab = new GestionAul_Lab();
                aulLab.setVisible(true);
                dispose();
            }
        });
    }

    /**
     * Configura el icono del botón de flecha atrás.
     * Escala el icono y lo asigna al botón correspondiente.
     */
    public void configurarIconoFlechaAtras() {
        flechabtn.setSize(20, 20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        flechabtn.setIcon(new ImageIcon(img));
    }

}
