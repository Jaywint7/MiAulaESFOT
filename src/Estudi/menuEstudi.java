package Estudi;

import Estudi.HistoReserva.historialReser;
import Estudi.ReservaAula.ReservarAula;
import Estudi.VerDisponi.verMisReservas;
import Inicio.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menuEstudi extends JFrame {
    private JPanel menu_Estudiante;
    private JButton verReservasButton;
    private JButton reservarAulasButton;
    private JButton resumenDeReservasRecientesButton;
    private JButton flechabtn;
    private JPanel JPanel_menuAdmin;
    private JButton button1;

    private int usuarioId;

    public menuEstudi(int usuarioId) {
        this.usuarioId = usuarioId;
        setSize(600,500);
        setLocationRelativeTo(null);
        setContentPane(menu_Estudiante);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        flechabtn.setSize(20,20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        flechabtn.setIcon(new ImageIcon(img));

        resumenDeReservasRecientesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Estudi.HistoReserva.historialReser historial = new historialReser(usuarioId);
                historial.setVisible(true);
                dispose();
            }
        });
        verReservasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Estudi.VerDisponi.verMisReservas ver = new verMisReservas(usuarioId);
                ver.setVisible(true);
                dispose();
            }
        });

        reservarAulasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Estudi.ReservaAula.ReservarAula reserva = new ReservarAula(usuarioId);
                reserva.setVisible(true);
                dispose();
            }
        });

        flechabtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login login  = new Login();
                login.setVisible(true);
                dispose();
            }
        });
    }
}
