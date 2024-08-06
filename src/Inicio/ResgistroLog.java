package Inicio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * La clase `ResgistroLog` representa la ventana de registro de usuarios de la aplicación.
 * <p>
 * Esta clase extiende {@link JFrame} y proporciona una interfaz gráfica para que los usuarios
 * se registren en la aplicación. Los usuarios pueden seleccionar su tipo (Profesor o Estudiante),
 * y se validan los campos antes de insertar los datos en la base de datos.
 * </p>
 */
public class ResgistroLog extends JFrame {
    private JPanel JPanel_reg;
    private JButton resgitrarseButton;
    private JTextField txtNomreg;
    private JTextField txtApereg;
    private JTextField txtEmailreg;
    private JPasswordField txtContreg;
    private JComboBox<String> JCOmbo_reg;
    private JTextField txtClaveProf;
    private JLabel lblclave;
    private JButton regresarButton;
    private JButton button1;

    /**
     * Constructor de la clase `ResgistroLog`.
     * <p>
     * Configura la ventana principal de registro, incluyendo la inicialización de los componentes de
     * la interfaz gráfica y los manejadores de eventos para los botones y el combo box.
     * </p>
     */
    public ResgistroLog() {
        super("Bienvenida");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setContentPane(JPanel_reg);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configura el comportamiento del JComboBox para mostrar u ocultar el campo de clave del profesor
        JCOmbo_reg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eleccion = (String) JCOmbo_reg.getSelectedItem();
                switch (eleccion) {
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

        // Configura el botón de regresar con un ícono
        regresarButton.setSize(20, 20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        regresarButton.setIcon(new ImageIcon(img));

        // Configura el comportamiento del botón de regresar para volver a la ventana de inicio de sesión
        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login login = new Login();
                login.setVisible(true);
                dispose();
            }
        });

        // Configura el comportamiento del botón de registrarse para realizar el registro de usuario
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

    /**
     * Maneja el proceso de registro de un nuevo usuario en la base de datos.
     * <p>
     * Este método valida los campos ingresados y, si son correctos, inserta los datos del usuario en la base de datos.
     * Para los profesores, también se valida una clave especial. La contraseña es encriptada antes de ser almacenada.
     * </p>
     *
     * @throws SQLException Si ocurre un error al conectar o insertar datos en la base de datos.
     */
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

        // Validación de clave de profesor
        if (tipo_usuario.equals("Profesor")) {
            if (!claveProf.equals("EsfoT2024")) {
                JOptionPane.showMessageDialog(null, "Clave de profesor incorrecta.");
                return;
            }
        }

        // Encriptar la contraseña
        String ContraHash = ManejarHash.hashPassword(contraseña);

        Connection conectar = conexion();
        String sql = "INSERT INTO usuarios(nombre, apellido, email, contraseña, tipo_usuario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement st = conectar.prepareStatement(sql)) {
            st.setString(1, nombre);
            st.setString(2, apellido);
            st.setString(3, email);
            st.setString(4, ContraHash);
            st.setString(5, tipo_usuario);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "REGISTRO INSERTADO CORRECTAMENTE");
                txtNomreg.setText("");
                txtApereg.setText("");
                txtEmailreg.setText("");
                txtContreg.setText("");
                txtClaveProf.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "ERROR EN EL REGISTRO");
            }
        } finally {
            conectar.close();
        }
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
