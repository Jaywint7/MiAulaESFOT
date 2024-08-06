package Profe.HistorialReserv;

import Admin.menuAdmin;
import Profe.menuProf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Historial extends JFrame {
    private JPanel JPanel_Historial;
    private JScrollPane ScrollUsuario;
    private JTable tableHistorial;
    private JButton regresarButton;
    private int usuarioId;

    public Historial(int usuarioId){
        this.usuarioId = usuarioId;
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(JPanel_Historial);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicializar el modelo de la tabla
        tableHistorial.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Id", "Aula/Lab ID", "Usuario ID", "Fecha de Reserva", "Hora Inicio", "Hora Fin", "Nombre Usuario", "Apellido Usuario", "Carrera"}
        ));

        // Cargar los registros combinados y los usuarios de la nube
        cargarRegistrosCombinados();
        try {
            cargarUsuariosNube();
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar usuarios de la nube", e);
        }

        // Configurar el botón de regresar
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
     * Carga los usuarios desde la base de datos en la nube.
     * @return Un mapa que asocia el ID de usuario con un array de sus datos (nombre, apellido, tipo de usuario).
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public Map<Integer, String[]> cargarUsuariosNube() throws SQLException {
        Connection con = conexionNube();
        String query = "SELECT id, nombre, apellido, tipo_usuario FROM usuarios";
        Statement stmt = null;
        ResultSet rs = null;
        Map<Integer, String[]> usuariosMap = new HashMap<>();

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String tipoUsuario = rs.getString("tipo_usuario");
                usuariosMap.put(id, new String[]{nombre, apellido, tipoUsuario});
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            con.close();
        }

        return usuariosMap;
    }

    /**
     * Carga los registros combinados desde la base de datos local y los usuarios de la base de datos en la nube.
     */
    public void cargarRegistrosCombinados() {
        Connection conLocal = null;
        Statement stmtLocal = null;
        ResultSet rsLocal = null;

        try {
            // Cargar usuarios desde la nube
            Map<Integer, String[]> usuariosMap = cargarUsuariosNube();

            // Conectar a la base de datos local
            conLocal = conexionLocal();
            String queryLocal = "SELECT r.id AS ReservaID, " +
                    "       r.aula_lab_id AS AulaLabID, " +
                    "       r.usuario_id AS UsuarioID, " +
                    "       r.fecha_reserva AS FechaReserva, " +
                    "       r.hora_inicio AS HoraInicio, " +
                    "       r.hora_fin AS HoraFin, " +
                    "       al.Nombre AS NombreAulaLab, " +
                    "       al.Capacidad AS Capacidad, " +
                    "       al.Carrera AS Carrera " +
                    "FROM reservas r " +
                    "JOIN aulas_laboratorios al ON r.aula_lab_id = al.Id";

            stmtLocal = conLocal.createStatement();
            rsLocal = stmtLocal.executeQuery(queryLocal);

            DefaultTableModel model = (DefaultTableModel) tableHistorial.getModel();
            model.setRowCount(0); // Limpiar filas existentes

            while (rsLocal.next()) {
                int reservaID = rsLocal.getInt("ReservaID");
                int aulaLabID = rsLocal.getInt("AulaLabID");
                int usuarioID = rsLocal.getInt("UsuarioID");
                Date fechaReserva = rsLocal.getDate("FechaReserva");
                Time horaInicio = rsLocal.getTime("HoraInicio");
                Time horaFin = rsLocal.getTime("HoraFin");
                String nombreAulaLab = rsLocal.getString("NombreAulaLab");
                int capacidad = rsLocal.getInt("Capacidad");
                String carrera = rsLocal.getString("Carrera");

                // Obtener los datos del usuario de la base de datos en la nube
                String[] usuarioData = usuariosMap.get(usuarioID);
                String nombreUsuario = usuarioData != null ? usuarioData[0] : "Desconocido";
                String apellidoUsuario = usuarioData != null ? usuarioData[1] : "Desconocido";

                model.addRow(new Object[]{reservaID, aulaLabID, usuarioID, fechaReserva, horaInicio, horaFin, nombreUsuario, apellidoUsuario, carrera});
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Maneja el error de manera adecuada
        } finally {
            try {
                if (rsLocal != null) rsLocal.close();
                if (stmtLocal != null) stmtLocal.close();
                if (conLocal != null) conLocal.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Establece una conexión con la base de datos en la nube.
     * @return La conexión establecida.
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public Connection conexionNube() throws SQLException {
        String url = "jdbc:mysql://bwhrnrxq2kqlsgfno7nj-mysql.services.clever-cloud.com:3306/bwhrnrxq2kqlsgfno7nj";
        String user = "uptlyedjy2kfhb4h";
        String password = "2bmWngRsgFBMmwDLpwPV";
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Establece una conexión con la base de datos local.
     * @return La conexión establecida.
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public Connection conexionLocal() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/miaulaesfot";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }
}