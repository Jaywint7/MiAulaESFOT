package Inicio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResgistroLog extends JFrame{
    private JPanel JPanel_reg;
    private JButton resgitrarseButton;
    private JTextField txtNomreg;
    private JTextField txtApereg;
    private JTextField txtEmailreg;
    private JPasswordField txtContreg;
    private JComboBox JCOmbo_reg;
    private JTextField txtClaveProf;
    private JLabel lblclave;
    private JButton regresarButton;
    private JButton button1;

    public ResgistroLog(){
        super("Bienvenida");
        setSize(600,500);
        setLocationRelativeTo(null);
        setContentPane(JPanel_reg);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JCOmbo_reg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eleccion = (String) JCOmbo_reg.getSelectedItem();
                switch (eleccion){
                    case "Profesor":
                        lblclave.setVisible(true);
                        txtClaveProf.setVisible(true);
                        break;
                    case "Estudiante":
                        lblclave.setVisible(false);
                        txtClaveProf.setVisible(false);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "ERROR");
                        break;
                }
            }
        });
        regresarButton.setSize(20,20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        regresarButton.setIcon(new ImageIcon(img));

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login login = new Login();
                login.setVisible(true);
                dispose();
            }
        });
        resgitrarseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    registrar();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void registrar() throws SQLException {
        String nombre = txtNomreg.getText().trim();
        String apellido = txtApereg.getText().trim();
        String email = txtEmailreg.getText().trim();
        String contraseña = new String(txtContreg.getPassword()).trim();
        String tipo_usuario = (String) JCOmbo_reg.getSelectedItem();
        String claveProf = txtClaveProf.getText().trim();

        // Validaciones de campos vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || contraseña.isEmpty() || tipo_usuario == null) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            return;
        }

        if (tipo_usuario.equals("Profesor")) {
            if (!claveProf.equals("EsfoT2024")) {
                JOptionPane.showMessageDialog(null, "Clave de profesor incorrecta.");
                return;
            }
        }

        String ContraHash = ManejarHash.hashPassword(contraseña);

        Connection conectar = conexion();
        String sql = "INSERT INTO usuarios(nombre, apellido, email, contraseña, tipo_usuario)value(?,?,?,?,?)";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setString(1,nombre);
        st.setString(2,apellido);
        st.setString(3,email);
        st.setString(4,ContraHash);
        st.setString(5,tipo_usuario);

        int rowsAffected=st.executeUpdate();
        if ( (rowsAffected > 0)) {
            JOptionPane.showMessageDialog(null, "REGISTRO INSERTADO CORRECTAMENTE");
            txtNomreg.setText("");
            txtApereg.setText("");
            txtEmailreg.setText("");
            txtContreg.setText("");
            txtClaveProf.setText("");
        }else{
            JOptionPane.showMessageDialog(null, "EROR EN EL REGISTRO");
        }
        st.close();
        conectar.close();
    }

    public Connection conexion() throws SQLException {
        String url = "jdbc:mysql://bwhrnrxq2kqlsgfno7nj-mysql.services.clever-cloud.com:3306/bwhrnrxq2kqlsgfno7nj";
        String user = "uptlyedjy2kfhb4h";
        String password = "2bmWngRsgFBMmwDLpwPV";
        return DriverManager.getConnection(url, user, password);
    }
}
