package Profe.ReservAula;

import Profe.menuProf;
import com.toedter.calendar.JDateChooser;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;

/**
 * La clase ReservaAula_Lab permite a los usuarios reservar aulas y laboratorios.
 * Extiende JFrame para crear una interfaz gráfica de usuario.
 */
public class ReservaAula_Lab extends JFrame {
    private JPanel JPanelAul_Lab;
    private JPanel JPanel_Reserva;
    private JPanel JPanel_TabladeAulasDisp;
    private JPanel JPanel_Form;
    private JComboBox<String> ComboTipo;
    private JDateChooser DateReserva;
    private JSpinner SpinHInicio;
    private JSpinner SpinHFin;
    private JButton confirmarReservaButton;
    private JTable tablaAulasLaboratorios;
    private JButton regresarButton;
    private JTextField txtIdReserva;

    private int usuarioId;

    /**
     * Inicializa los componentes de la interfaz gráfica.
     */
    private void createUIComponents() {
        DateReserva = new JDateChooser();
        DateReserva.setDateFormatString("yyyy-MM-dd");

        // Inicialización de JSpinner para horas de inicio
        SpinnerDateModel modelHInicio = new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY);
        SpinHInicio = new JSpinner(modelHInicio);
        JSpinner.DateEditor editorHInicio = new JSpinner.DateEditor(SpinHInicio, "HH:mm");
        SpinHInicio.setEditor(editorHInicio);

        // Inicialización de JSpinner para horas de fin
        SpinnerDateModel modelFin = new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY);
        SpinHFin = new JSpinner(modelFin);
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(SpinHFin, "HH:mm");
        SpinHFin.setEditor(editorFin);
    }

    /**
     * Constructor de la clase ReservaAula_Lab.
     *
     * @param usuarioId ID del usuario que realiza la reserva.
     */
    public ReservaAula_Lab(int usuarioId) {
        this.usuarioId = usuarioId;
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(JPanelAul_Lab);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tablaAulasLaboratorios.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Tipo", "Nombre", "Capacidad", "Número", "Carrera", "Estado"}
        ));

        confirmarReservaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    confReserva();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al realizar la reserva: " + ex.getMessage());
                }
            }
        });

        try {
            cargarRegistros();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        regresarButton.setSize(20, 20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        regresarButton.setIcon(new ImageIcon(img));

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Profe.menuProf menu = new menuProf(usuarioId);
                menu.setVisible(true);
                dispose();
            }
        });
    }

    /**
     * Confirma la reserva solicitando la verificación del usuario.
     *
     * @throws SQLException Si ocurre un error al verificar el usuario o realizar la reserva.
     */
    public void confReserva() throws SQLException {
        JTextField usuarioEmailField = new JTextField();
        JPasswordField contraseñaField = new JPasswordField();

        Object[] message = {
                "Correo:", usuarioEmailField,
                "Contraseña:", contraseñaField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Verificación de Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String email = usuarioEmailField.getText().trim();
            String contraseña = new String(contraseñaField.getPassword());

            if (email.isEmpty() || contraseña.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el correo y la contraseña del usuario para confirmar la reserva.");
                return;
            }

            if (!validarUsuario(email, contraseña)) {
                JOptionPane.showMessageDialog(null, "Correo o contraseña incorrectos.");
                return;
            }

            ingresrDatosReserva(email);
        }
    }

    /**
     * Ingresa los datos de la reserva en la base de datos.
     *
     * @param email Correo del usuario que realiza la reserva.
     * @throws SQLException Si ocurre un error al ingresar los datos de la reserva.
     */
    public void ingresrDatosReserva(String email) throws SQLException {
        int usuarioId = obtenerIdUsuario(email);
        if (usuarioId == -1) {
            JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
            return;
        }

        int aulaLabId;
        try {
            aulaLabId = Integer.parseInt(txtIdReserva.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de Aula/Laboratorio inválido.");
            return;
        }

        Date fecha = DateReserva.getDate();
        if (fecha == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una fecha válida.");
            return;
        }
        java.sql.Date sqlDate = new java.sql.Date(fecha.getTime());
        java.sql.Time horaInicio = new java.sql.Time(((Date) SpinHInicio.getValue()).getTime());
        java.sql.Time horaFin = new java.sql.Time(((Date) SpinHFin.getValue()).getTime());

        if (horaInicio.after(horaFin)) {
            JOptionPane.showMessageDialog(null, "La hora de inicio debe ser antes de la hora de fin.");
            return;
        }

        if (!verificarDisponibilidad(aulaLabId)) {
            JOptionPane.showMessageDialog(null, "El aula/laboratorio no está disponible.");
            return;
        }

        Connection conectar = conexionLocal();
        String sqlReserva = "INSERT INTO reservas (aula_lab_id, usuario_id, fecha_reserva, hora_inicio, hora_fin, estado) VALUES (?, ?, ?, ?, ?, 'Pendiente')";
        PreparedStatement stReserva = conectar.prepareStatement(sqlReserva);

        stReserva.setInt(1, aulaLabId);
        stReserva.setInt(2, usuarioId);
        stReserva.setDate(3, sqlDate);
        stReserva.setTime(4, horaInicio);
        stReserva.setTime(5, horaFin);

        stReserva.executeUpdate();

        String sqlUpdate = "UPDATE aulas_laboratorios SET Estado = 'Ocupado' WHERE Id = ?";
        PreparedStatement stUpdate = conectar.prepareStatement(sqlUpdate);
        stUpdate.setInt(1, aulaLabId);
        stUpdate.executeUpdate();

        stReserva.close();
        stUpdate.close();
        conectar.close();

        JOptionPane.showMessageDialog(null, "Reserva realizada con éxito.");
    }

    /**
     * Valida las credenciales del usuario.
     *
     * @param email       Correo del usuario.
     * @param contraseña  Contraseña del usuario.
     * @return true si las credenciales son válidas, false en caso contrario.
     * @throws SQLException Si ocurre un error al validar las credenciales.
     */
    public boolean validarUsuario(String email, String contraseña) throws SQLException {
        Connection conexionNube = conexionNube();
        String sql = "SELECT contraseña FROM usuarios WHERE email = ?";
        PreparedStatement st = conexionNube.prepareStatement(sql);
        st.setString(1, email);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            String contraAlma = rs.getString("contraseña");
            rs.close();
            st.close();
            conexionNube.close();
            return BCrypt.checkpw(contraseña, contraAlma);
        } else {
            rs.close();
            st.close();
            conexionNube.close();
            return false;
        }
    }

    /**
     * Obtiene el ID del usuario a partir de su correo.
     *
     * @param email Correo del usuario.
     * @return ID del usuario o -1 si el usuario no existe.
     * @throws SQLException Si ocurre un error al obtener el ID del usuario.
     */
    public int obtenerIdUsuario(String email) throws SQLException {
        Connection conexionNube = conexionNube();
        String sql = "SELECT id FROM usuarios WHERE email = ?";
        PreparedStatement st = conexionNube.prepareStatement(sql);
        st.setString(1, email);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("id");
            rs.close();
            st.close();
            conexionNube.close();
            return id;
        } else {
            rs.close();
            st.close();
            conexionNube.close();
            return -1;
        }
    }

    /**
     * Verifica si un aula/laboratorio está disponible.
     *
     * @param aulaLabId ID del aula/laboratorio.
     * @return true si está disponible, false en caso contrario.
     * @throws SQLException Si ocurre un error al verificar la disponibilidad.
     */
    public boolean verificarDisponibilidad(int aulaLabId) throws SQLException {
        Connection conectar = conexionLocal();
        String sql = "SELECT Estado FROM aulas_laboratorios WHERE Id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setInt(1, aulaLabId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            String estado = rs.getString("Estado");
            rs.close();
            st.close();
            conectar.close();
            return estado.equals("Disponible");
        } else {
            rs.close();
            st.close();
            conectar.close();
            return false;
        }
    }

    /**
     * Carga los registros de aulas y laboratorios disponibles en la tabla.
     *
     * @throws SQLException Si ocurre un error al cargar los registros.
     */
    public void cargarRegistros() throws SQLException {
        Connection conectar = conexionLocal();
        String sql = "SELECT * FROM aulas_laboratorios WHERE Estado = 'Disponible'";
        PreparedStatement st = conectar.prepareStatement(sql);

        ResultSet rs = st.executeQuery();
        DefaultTableModel modelotabla = (DefaultTableModel) tablaAulasLaboratorios.getModel();
        modelotabla.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos

        while (rs.next()) {
            modelotabla.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getString("Tipo"),
                    rs.getString("Nombre"),
                    rs.getInt("Capacidad"),
                    rs.getInt("NumeroAulLab"),
                    rs.getString("Carrera"),
                    rs.getString("Estado")
            });
        }

        rs.close();
        st.close();
        conectar.close();
    }

    /**
     * Establece la conexión con la base de datos en la nube.
     *
     * @return Conexión a la base de datos en la nube.
     * @throws SQLException Si ocurre un error al conectar.
     */
    public Connection conexionNube() throws SQLException {
        String url = "jdbc:mysql://bwhrnrxq2kqlsgfno7nj-mysql.services.clever-cloud.com:3306/bwhrnrxq2kqlsgfno7nj";
        String user = "uptlyedjy2kfhb4h";
        String password = "2bmWngRsgFBMmwDLpwPV";
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Establece la conexión con la base de datos local.
     *
     * @return Conexión a la base de datos local.
     * @throws SQLException Si ocurre un error al conectar.
     */
    public Connection conexionLocal() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/miaulaesfot";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }
}
