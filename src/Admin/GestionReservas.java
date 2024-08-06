package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que gestiona las reservas de aulas y laboratorios.
 */
public class GestionReservas extends JFrame {

    private JPanel JPanel_Historial;
    private JScrollPane ScrollUsuario;
    private JTable tableHistorial;
    private JButton regresarButton;
    private JTextField txtConfReserv;
    private JButton confirmarbutton;

    /**
     * Constructor de la clase GestionReservas.
     * Configura la interfaz gráfica y añade los listeners a los botones.
     */
    public GestionReservas() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(JPanel_Historial);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tableHistorial.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Id", "Aula/Lab ID", "Usuario ID", "Fecha de Reserva", "Hora Inicio", "Hora Fin", "Nombre Usuario", "Apellido Usuario", "Carrera", "Estado"}
        ));

        cargarRegistrosCombinados();

        try {
            cargarUsuariosNube();
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
                Admin.menuAdmin menu = new menuAdmin();
                menu.setVisible(true);
                dispose();
            }
        });

        confirmarbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    aprobarReserva();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    /**
     * Aprueba una reserva actualizando su estado en la base de datos.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public void aprobarReserva() throws SQLException {
        String idReserva = txtConfReserv.getText().trim();
        if (idReserva.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el ID de la reserva para aprobar.");
            return;
        }

        int idReserv;
        try {
            idReserv = Integer.parseInt(idReserva);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de reserva inválido.");
            return;
        }

        Connection conectar = conexionLocal();
        String sqlUpdate = "UPDATE reservas SET estado = 'Aprobado' WHERE id = ?";
        PreparedStatement stUpdate = conectar.prepareStatement(sqlUpdate);
        stUpdate.setInt(1, idReserv);

        int filasAfectadas = stUpdate.executeUpdate();
        stUpdate.close();
        conectar.close();

        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(null, "Reserva aprobada con éxito.");
            cargarRegistrosCombinados();
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró una reserva con el ID proporcionado.");
        }
    }

    /**
     * Carga los datos de los usuarios desde la base de datos en la nube.
     * @return Un mapa con los datos de los usuarios.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
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
     * Carga los registros de reservas y combina la información de las tablas locales y de la nube.
     */
    public void cargarRegistrosCombinados() {
        Connection conLocal = null;
        Statement stmtLocal = null;
        ResultSet rsLocal = null;

        try {
            Map<Integer, String[]> usuariosMap = cargarUsuariosNube();

            conLocal = conexionLocal();
            String queryLocal = "SELECT r.id AS ReservaID, " +
                    "       r.aula_lab_id AS AulaLabID, " +
                    "       r.usuario_id AS UsuarioID, " +
                    "       r.fecha_reserva AS FechaReserva, " +
                    "       r.hora_inicio AS HoraInicio, " +
                    "       r.hora_fin AS HoraFin, " +
                    "       r.estado AS Estado, " +
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
                String estado = rsLocal.getString("Estado");

                String[] usuarioData = usuariosMap.get(usuarioID);
                String nombreUsuario = usuarioData != null ? usuarioData[0] : "Desconocido";
                String apellidoUsuario = usuarioData != null ? usuarioData[1] : "Desconocido";

                String estadoMostrar = "Pendiente".equals(estado) ? "Aprobado" : estado;

                model.addRow(new Object[]{reservaID, aulaLabID, usuarioID, fechaReserva, horaInicio, horaFin, nombreUsuario, apellidoUsuario, carrera, estadoMostrar});
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
     * Establece la conexión con la base de datos en la nube.
     * @return La conexión con la base de datos.
     * @throws SQLException Si ocurre un error al conectar con la base de datos.
     */
    public Connection conexionNube() throws SQLException {
        String url = "jdbc:mysql://bwhrnrxq2kqlsgfno7nj-mysql.services.clever-cloud.com:3306/bwhrnrxq2kqlsgfno7nj";
        String user = "uptlyedjy2kfhb4h";
        String password = "2bmWngRsgFBMmwDLpwPV";
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Establece la conexión con la base de datos local.
     * @return La conexión con la base de datos.
     * @throws SQLException Si ocurre un error al conectar con la base de datos.
     */
    public Connection conexionLocal() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/miaulaesfot";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }
}
