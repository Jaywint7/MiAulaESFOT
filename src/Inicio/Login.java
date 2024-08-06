package Inicio;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import Admin.menuAdmin;
import Estudi.menuEstudi;
import Profe.menuProf;
import org.mindrot.jbcrypt.BCrypt;

public class Login extends JFrame {
    private JComboBox<String> comboUsu;
    private JTextField txtUsuLogAdmin;
    private JPasswordField txtPwsdLogAdmin;
    private JButton ingresarButton;
    private JPasswordField txtPwsdLogProf;
    private JTextField txtUsuLogProf;
    private JPanel JPanel_Prof;
    private JPanel JPanel_Admin;
    private JPanel JPanel_Login;
    private JTextField txtLogEstud;
    private JPasswordField txtpwsEst;
    private JPanel JPanel_Est;
    private JButton registreseButton;

    public Login(){
        super("Bienvenida");
        setSize(600,500);
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
                        JPanel_Est.setVisible(false);
                        txtUsuLogProf.setText("");
                        txtPwsdLogProf.setText("");
                        txtLogEstud.setText("");
                        txtpwsEst.setText("");
                        break;
                    case "Profesor":
                        JPanel_Admin.setVisible(false);
                        JPanel_Prof.setVisible(true);
                        JPanel_Est.setVisible(false);
                        txtUsuLogAdmin.setText("");
                        txtPwsdLogAdmin.setText("");
                        txtLogEstud.setText("");
                        txtpwsEst.setText("");
                        break;
                    case "Estudiante":
                        JPanel_Est.setVisible(true);
                        JPanel_Admin.setVisible(false);
                        JPanel_Prof.setVisible(false);
                        txtUsuLogAdmin.setText("");
                        txtPwsdLogAdmin.setText("");
                        txtUsuLogProf.setText("");
                        txtPwsdLogProf.setText("");
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "ERROR");
                        break;
                }
            }
        });

        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    iniciar();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        registreseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResgistroLog registro = new ResgistroLog();
                registro.setVisible(true);
                dispose();
            }
        });
    }

    public void iniciar() throws SQLException {
        String eleccion = (String) comboUsu.getSelectedItem(); // Tipo de usuario seleccionado en JComboBox
        Connection conectar = conexion();

        String user;
        String pswd;

        // Definir el campo de usuario y contraseña dependiendo del tipo de usuario
        if ("Administrador".equals(eleccion)) {
            user = txtUsuLogAdmin.getText().trim();
            pswd = new String(txtPwsdLogAdmin.getPassword()).trim();
        } else if ("Profesor".equals(eleccion)) {
            user = txtUsuLogProf.getText().trim();
            pswd = new String(txtPwsdLogProf.getPassword()).trim();
        } else if ("Estudiante".equals(eleccion)) {
            user = txtLogEstud.getText().trim();
            pswd = new String(txtpwsEst.getPassword()).trim();
        } else {
            JOptionPane.showMessageDialog(null, "ERROR - Selección de usuario no válida");
            return;
        }

        if (user.isEmpty() || pswd.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
            return;
        }

        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (PreparedStatement st = conectar.prepareStatement(sql)) {
            st.setString(1, user);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    String contraAlma = rs.getString("contraseña");
                    String tipoUsuario = rs.getString("tipo_usuario");
                    int usuarioId = rs.getInt("id");

                    if (BCrypt.checkpw(pswd, contraAlma)) {
                        // Verificar que el tipo de usuario coincida con la selección
                        if (eleccion.equals(tipoUsuario)) {
                            switch (tipoUsuario) {
                                case "Administrador":
                                    JOptionPane.showMessageDialog(null, "CREDENCIALES CORRECTAS - " + tipoUsuario);
                                    new menuAdmin().setVisible(true);
                                    break;
                                case "Profesor":
                                    JOptionPane.showMessageDialog(null, "CREDENCIALES CORRECTAS - " + tipoUsuario);
                                    new menuProf(usuarioId).setVisible(true);
                                    break;
                                case "Estudiante":
                                    JOptionPane.showMessageDialog(null, "CREDENCIALES CORRECTAS - " + tipoUsuario);
                                    new Estudi.menuEstudi(usuarioId).setVisible(true);
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(null, "ERROR DE CREDENCIALES - Tipo de usuario incorrecto");
                                    break;
                            }
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "CREDENCIALES INCORRECTAS - Tipo de usuario no coincide");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "CREDENCIALES INCORRECTAS - Contraseña");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "CREDENCIALES INCORRECTAS - Usuario no encontrado");
                }
            }
        }

        conectar.close();
    }

    public Connection conexion() throws SQLException {
        String url = "jdbc:mysql://bwhrnrxq2kqlsgfno7nj-mysql.services.clever-cloud.com:3306/bwhrnrxq2kqlsgfno7nj";
        String user = "uptlyedjy2kfhb4h";
        String password = "2bmWngRsgFBMmwDLpwPV";
        return DriverManager.getConnection(url, user, password);
    }
}
