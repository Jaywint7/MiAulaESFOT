package Estudi.VerDisponi;

import Estudi.menuEstudi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Clase verMisReservas permite a los usuarios ver y gestionar sus reservas.
 */
public class verMisReservas extends JFrame {
    private JPanel JPanel_MisReservas;
    private JPanel MisReservas;
    private JScrollPane tablaMisRegistros;
    private JTable tablaMisReservas_Est;
    private JPanel Det_Canc;
    private JButton verDetallesButton;
    private JButton cancelarButton;
    private JPanel Detalles;
    private JTextField txtTipo;
    private JTextField txtNom;
    private JTextField txtCapacidad;
    private JTextField txtNumAuLab;
    private JTextField txtCarrera;
    private JPanel Cancelar;
    private JTextField txtIdDelete;
    private JTextField txtID;
    private JButton regresarButton;
    private JLabel lblUsuario;
    private JPanel JPanel_VermisReservasEst;

    private int usuarioId;

    /**
     * Constructor de la clase verMisReservas.
     * @param usuarioId ID del usuario que accede a sus reservas.
     */
    public verMisReservas(int usuarioId) {
        super("Ver Mis Reservas - Estudiante");
        this.usuarioId = usuarioId;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(JPanel_MisReservas);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        configurarBotonRegresar();
        configurarTablaMisReservas();

        try {
            cargarRegistros();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        configurarEventosBotones();
    }

    /**
     * Configura el botón de regresar.
     */
    private void configurarBotonRegresar() {
        regresarButton.setSize(20, 20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        regresarButton.setIcon(new ImageIcon(img));

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Estudi.menuEstudi menu = new menuEstudi(usuarioId);
                menu.setVisible(true);
                dispose();
            }
        });
    }

    /**
     * Configura la tabla de mis reservas.
     */
    private void configurarTablaMisReservas() {
        tablaMisReservas_Est.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Reserva", "Aula/Lab ID", "Usuario ID", "Fecha de Reserva", "Hora de Inicio", "Hora de Fin", "Estado"}
        ));
    }

    /**
     * Configura los eventos de los botones.
     */
    private void configurarEventosBotones() {
        verDetallesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    verDetalles();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al ver detalles: " + ex.getMessage());
                }
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cancelar();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al cancelar reserva: " + ex.getMessage());
                }
            }
        });
    }

    /**
     * Carga los registros de las reservas del usuario.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public void cargarRegistros() throws SQLException {
        Connection conectar = conexionLocal();
        String sql = "SELECT * FROM reservas WHERE usuario_id = ? AND aula_lab_id IN (SELECT Id FROM aulas_laboratorios WHERE Tipo = 'Aula')";

        PreparedStatement st = conectar.prepareStatement(sql);
        st.setInt(1, usuarioId);
        ResultSet rs = st.executeQuery();
        DefaultTableModel model = (DefaultTableModel) tablaMisReservas_Est.getModel();
        model.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("aula_lab_id"),
                    rs.getString("usuario_id"),
                    rs.getString("fecha_reserva"),
                    rs.getString("hora_inicio"),
                    rs.getString("hora_fin"),
                    rs.getString("estado")
            });
        }
        rs.close();
        st.close();
        conectar.close();
    }

    /**
     * Muestra los detalles de una reserva específica.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public void verDetalles() throws SQLException {
        String id = txtID.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el ID para buscar.");
            return;
        }

        Connection conectar = conexionLocal();
        String sql = "SELECT * FROM aulas_laboratorios WHERE Id = ? AND Tipo = 'Aula'";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setString(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            txtTipo.setText(rs.getString("Tipo"));
            txtNom.setText(rs.getString("Nombre"));
            txtCapacidad.setText(rs.getString("Capacidad"));
            txtNumAuLab.setText(rs.getString("NumeroAulLab"));
            txtCarrera.setText(rs.getString("Carrera"));
            verDetallesButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró ningún registro con el ID proporcionado.");
            verDetallesButton.setEnabled(false);
        }

        rs.close();
        st.close();
        conectar.close();
    }

    /**
     * Cancela una reserva y actualiza el estado del aula/laboratorio.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public void cancelar() throws SQLException {
        String idReserva = txtIdDelete.getText();

        if (idReserva.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el ID de la reserva para cancelar.");
            return;
        }

        Connection conectar = conexionLocal();
        String sqlSelect = "SELECT aula_lab_id, usuario_id FROM reservas WHERE id = ?";
        PreparedStatement stSelect = conectar.prepareStatement(sqlSelect);
        stSelect.setString(1, idReserva);
        ResultSet rs = stSelect.executeQuery();

        if (rs.next()) {
            int aulaLabId = rs.getInt("aula_lab_id");
            int usuarioIdReserva = rs.getInt("usuario_id");

            // Verificar si la reserva pertenece al usuario actual
            if (usuarioIdReserva != this.usuarioId) {
                JOptionPane.showMessageDialog(null, "No puede cancelar reservas de otros usuarios.");
                rs.close();
                stSelect.close();
                conectar.close();
                return;
            }

            String sqlDelete = "DELETE FROM reservas WHERE id = ?";
            PreparedStatement stDelete = conectar.prepareStatement(sqlDelete);
            stDelete.setString(1, idReserva);
            int rowsAffected = stDelete.executeUpdate();

            if (rowsAffected > 0) {
                String sqlUpdate = "UPDATE aulas_laboratorios SET Estado = 'Disponible' WHERE Id = ? AND Tipo = 'Aula'";
                PreparedStatement stUpdate = conectar.prepareStatement(sqlUpdate);
                stUpdate.setInt(1, aulaLabId);
                stUpdate.executeUpdate();
                stUpdate.close();

                JOptionPane.showMessageDialog(null, "Reserva cancelada y estado del aula/laboratorio actualizado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró ninguna reserva con el ID proporcionado.");
            }

            stDelete.close();
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró ninguna reserva con el ID proporcionado.");
        }

        rs.close();
        stSelect.close();
        conectar.close();
    }

    /**
     * Establece la conexión a la base de datos local.
     * @return Conexión a la base de datos.
     * @throws SQLException Si ocurre un error al conectar a la base de datos.
     */
    public Connection conexionLocal() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/miaulaesfot";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }
}
