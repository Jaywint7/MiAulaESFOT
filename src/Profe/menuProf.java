package Profe;

import Inicio.Login;
import Profe.HistorialReserv.Historial;
import Profe.ReservAula_Lab.ReservaAula_Lab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menuProf extends JFrame {
    private JPanel JPanel_menuAdmin;
    private JButton verReservasButton;
    private JButton reservarAulasYLaboratoriosButton;
    private JButton resumenDeReservasRecientesButton;
    private JButton flechabtn;
    private JButton button1;
    public menuProf() {
        setSize(600,500);
        setLocationRelativeTo(null);
        setContentPane(JPanel_menuAdmin);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        flechabtn.setSize(20,20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        flechabtn.setIcon(new ImageIcon(img));

        flechabtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Inicio.Login login = new Login();
                login.setVisible(true);
                dispose();
            }
        });

        resumenDeReservasRecientesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Historial resumen = new Historial();
                resumen.setVisible(true);
                dispose();
            }
        });

        verReservasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        reservarAulasYLaboratoriosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReservaAula_Lab reserva = new ReservaAula_Lab();
                reserva.setVisible(true);
                dispose();
            }
        });
    }

}
