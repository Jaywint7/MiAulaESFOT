package Inicio;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import Admin.menuAdmin;
import Profe.menuProf;
import org.mindrot.jbcrypt.BCrypt;

/**
 * La clase `Login` representa la ventana de inicio de sesión de la aplicación.
 * <p>
 * Esta clase extiende {@link JFrame} y proporciona una interfaz gráfica para que los usuarios
 * ingresen sus credenciales. Dependiendo del tipo de usuario seleccionado (Administrador, Profesor o Estudiante),
 * se mostrarán diferentes paneles de entrada. También maneja el proceso de autenticación y la navegación
 * hacia las ventanas correspondientes tras una autenticación exitosa.
 * </p>
 */
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

    /**
     * Constructor de la clase `Login`.
     * <p>
     * Configura la ventana principal de inicio de sesión, incluyendo la inicialización de los componentes de
     * la interfaz gráfica y los manejadores de eventos para los botones y el combo box.
     * </p>
     */
    public Login(){
        super("Bienvenida");
        setSize(600,500);
        setLocationRelativeTo(null);
        setContentPane(JPanel_Login);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Configura el comportamiento del JComboBox para mostrar el panel correspondiente según el tipo de usuario seleccionado
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

        // Configura el comportamiento del botón de ingreso para realizar la autenticación
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

        // Configura el comportamiento del botón de registro para abrir la ventana de registro
        registreseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Inicio.ResgistroLog registro = new ResgistroLog();
                registro.setVisible(true);
                dispose();
            }
        });
    }

    /**
     * Maneja el proceso de inicio de sesión verificando las credenciales del usuario.
     * <p>
     * Este método se conecta a la base de datos, verifica las credenciales proporcionadas y, si son correctas,
     * dirige al usuario a la ventana correspondiente (Administrador, Profesor o Estudiante). También maneja
     * los casos en que las credenciales son incorrectas.
     * </p>
     *
     * @throws SQLException Si ocurre un error al conectar o consultar la base de datos.
     */
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

    /**
     * Establece la conexión con la base de datos.
     * <p>
     * Este método utiliza los detalles de la conexión (URL, usuario y contraseña) para conectarse a la base de datos
     * MySQL que almacena la información de los usuarios.
     * </p>
     *
     * @return La conexión establecida con la base de datos.
     * @throws SQLException Si ocurre un error al conectar a la base de datos.
     */
    public Connection conexion() throws SQLException {
        String url = "jdbc:mysql://bwhrnrxq2kqlsgfno7nj-mysql.services.clever-cloud.com:3306/bwhrnrxq2kqlsgfno7nj";
        String user = "uptlyedjy2kfhb4h";
        String password = "2bmWngRsgFBMmwDLpwPV";
        return DriverManager.getConnection(url, user, password);
    }
}
