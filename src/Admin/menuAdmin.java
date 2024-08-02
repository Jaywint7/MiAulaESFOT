package Admin;
import Inicio.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menuAdmin extends JFrame{
    private JButton gestionDeUsuariosButton;
    private JButton gestionDeReservasButton;
    private JButton gestionDeAulasYButton;
    private JButton gestionarAdministradoresButton;
    private JPanel JPanel_menuAdmin;
    private JButton flechabtn;
    private JButton button1;
    private JLabel flechalbl;

    public menuAdmin(){
        super("Menu Administrador");
        setSize(600,500);
        setLocationRelativeTo(null);
        setContentPane(JPanel_menuAdmin);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        flechaAtras();

        flechabtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Inicio.Login login = new Login();
                login.setVisible(true);
                dispose();
            }
        });

        gestionDeUsuariosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GestionUsu gestion = new GestionUsu();
                gestion.setVisible(true);
                dispose();
            }
        });
        gestionDeReservasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GestionReservas reservas = new GestionReservas();
                reservas.setVisible(true);
                dispose();
            }
        });
        gestionDeAulasYButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GestionAul_Lab aulLab = new GestionAul_Lab();
                aulLab.setVisible(true);
                dispose();
            }
        });
    }

    public void flechaAtras(){
        flechabtn.setSize(20,20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        flechabtn.setIcon(new ImageIcon(img));
    }

}
