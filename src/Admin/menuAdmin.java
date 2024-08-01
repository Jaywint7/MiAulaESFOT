package Admin;
import Inicio.Login;
import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;

public class menuAdmin extends JFrame{
    private JButton gestionDeUsuariosButton;
    private JButton gestionDeReservasButton;
    private JButton gestionDeAulasYButton;
    private JButton button4;
    private JPanel JPanel_menuAdmin;
    private JButton flechabtn;
    private JLabel flechalbl;

    public menuAdmin(){
        super("Menu Administrador");
        setSize(600,400);
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
    }

    public void flechaAtras(){
        flechabtn.setSize(20,20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        flechabtn.setIcon(new ImageIcon(img));
    }

}
