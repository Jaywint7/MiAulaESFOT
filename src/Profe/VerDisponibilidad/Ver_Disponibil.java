package Profe.VerDisponibilidad;

import Profe.menuProf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * La clase Ver_Disponibil permite a los usuarios ver y gestionar sus reservas de aulas y laboratorios.
 * Extiende JFrame para crear una interfaz gráfica de usuario.
 */
public class Ver_Disponibil extends JFrame {
    private JPanel JPanel_MisReservas;
    private JPanel MisReservas;
    private JPanel Det_Canc;
    private JTable tablaMisReservas;
    private JButton verDetallesButton;
    private JButton cancelarButton;
    private JScrollPane tablaMisRegistros;
    private JPanel Detalles;
    private JPanel Cancelar;
    private JTextField txtTipo;
    private JTextField txtNom;
    private JTextField txtCapacidad;
    private JTextField txtNumAuLab;
    private JTextField txtCarrera;
    private JTextField txtID;
    private JTextField txtIdDelete;
    private JButton regresarButton;
    private JLabel lblUsuario;

    private int usuarioId;

    /**
     * Constructor de la clase Ver_Disponibil.
     *
     * @param usuarioId ID del usuario que realiza las acciones.
     */
    public Ver_Disponibil(int usuarioId) {
        super("Ver Mis Reservas - Profesor");
        this.usuarioId = usuarioId;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(JPanel_MisReservas);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        regresarButton.setSize(20, 20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        regresarButton.setIcon(new ImageIcon(img));

        tablaMisReservas.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Reserva", "Aula/Lab ID", "Usuario ID", "Fecha de Reserva", "Hora de Inicio", "Hora de Fin", "Estado"}
        ));

        try {
            cargarRegistros();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Profe.menuProf menu = new menuProf(usuarioId);
                menu.setVisible(true);
                dispose();
            }
        });

        verDetallesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    verDetalles();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cancelar();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    /**
     * Carga los registros de reservas del usuario en la tabla.
     *
     * @throws SQLException Si ocurre un error al cargar los registros.
     */
    public void cargarRegistros() throws SQLException {
        Connection conectar = conexionLocal();
        String sql = "SELECT * FROM reservas WHERE usuario_id = ?";

        PreparedStatement st = conectar.prepareStatement(sql);
        st.setInt(1, usuarioId);
        ResultSet rs = st.executeQuery();
        DefaultTableModel model = (DefaultTableModel) tablaMisReservas.getModel();
        model.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("aula_lab_id"),
                    rs.getInt("usuario_id"),
                    rs.getDate("fecha_reserva"),
                    rs.getTime("hora_inicio"),
                    rs.getTime("hora_fin"),
                    rs.getString("estado")
            });
        }
        rs.close();
        st.close();
        conectar.close();
    }

    /**
     * Muestra los detalles del aula/laboratorio seleccionado.
     *
     * @throws SQLException Si ocurre un error al obtener los detalles.
     */
    public void verDetalles() throws SQLException {
        String id = txtID.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el Id para buscar");
            return;
        }

        Connection conectar = conexionLocal();
        String sql = "SELECT * FROM aulas_laboratorios WHERE Id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setInt(1, Integer.parseInt(id));
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            // Llenar los campos de texto con los datos obtenidos
            txtTipo.setText(rs.getString("Tipo"));
            txtNom.setText(rs.getString("Nombre"));
            txtCapacidad.setText(rs.getString("Capacidad"));
            txtNumAuLab.setText(rs.getString("NumeroAulLab"));
            txtCarrera.setText(rs.getString("Carrera"));

            // Habilitar el botón de actualización
            verDetallesButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró ningún registro con el ID proporcionado");
            // Deshabilitar el botón de actualización si no se encontraron datos
            verDetallesButton.setEnabled(false);
        }

        rs.close();
        st.close();
        conectar.close();
    }

    /**
     * Cancela una reserva y actualiza el estado del aula/laboratorio.
     *
     * @throws SQLException Si ocurre un error al cancelar la reserva.
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
