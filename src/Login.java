import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JComboBox comboUsu;
    private JTextField txtUsuLogAdmin;
    private JPasswordField txtPwsdLogAdmin;
    private JLabel txtPswdLog;
    private JButton ingresarButton;
    private JPasswordField txtPwsdLogProf;
    private JTextField txtUsuLogProf;
    private JPanel JPanel_Prof;
    private JPanel JPanel_Admin;
    private JPanel JPanel_Login;

    public Login(){
        super("Bienvenida");
        setSize(400,400);
        setLocationRelativeTo(null);
        setContentPane(JPanel_Login);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        comboUsu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eleccion = (String) comboUsu.getSelectedItem();
                switch (eleccion){
                    case "Administrador":
                        JPanel_Admin.setVisible(true);
                        JPanel_Admin.setSize(500,100);
                        JPanel_Prof.setVisible(false);
                        break;
                    case "Profesor":
                        JPanel_Admin.setVisible(false);
                        JPanel_Prof.setVisible(true);
                        JPanel_Prof.setSize(500,100);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "ERROR");
                        break;
                }
            }
        });
    }
}
