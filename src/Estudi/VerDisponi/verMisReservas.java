package Estudi.VerDisponi;

import Estudi.menuEstudi;
import Profe.menuProf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

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

    public verMisReservas(int usuarioId){

        this.usuarioId = usuarioId;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(JPanel_MisReservas);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        regresarButton.setSize(20,20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        regresarButton.setIcon(new ImageIcon(img));

        tablaMisReservas_Est.setModel(new DefaultTableModel(
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
                Estudi.menuEstudi menu = new menuEstudi(usuarioId);
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

    public void cargarRegistros() throws SQLException {
        Connection conectar = conexionLocal();
        String sql = "SELECT * FROM reservas WHERE usuario_id = ? AND aula_lab_id IN (SELECT Id FROM aulas_laboratorios WHERE Tipo = 'Aula') ";

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

    public void verDetalles() throws SQLException {
        String id = txtID.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el Id para buscar");
            return;
        }

        Connection conectar = conexionLocal();
        String sql = "SELECT * FROM aulas_laboratorios WHERE Id = ? AND Tipo = 'Aula'";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setString(1, id);
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
            JOptionPane.showMessageDialog(null, "NO SE ENCONTRO NINGUN REGISTRO CON EL ID PROPORCIONADO");
            // Deshabilitar el botón de actualización si no se encontraron datos
            verDetallesButton.setEnabled(false);
        }

        rs.close();
        st.close();
        conectar.close();
    }

    public void cancelar() throws SQLException {
        String idReserva = txtIdDelete.getText();

        Connection conectar = conexionLocal();
        String sql = "SELECT aula_lab_id FROM reservas WHERE id = ?";
        PreparedStatement stSelect = conectar.prepareStatement(sql);
        stSelect.setString(1, idReserva);
        ResultSet rs = stSelect.executeQuery();

        if (rs.next()) {
            int aulaLabId = rs.getInt("aula_lab_id");

            // Eliminar la reserva
            String sqlDelete = "DELETE FROM reservas WHERE id = ?";
            PreparedStatement stDelete = conectar.prepareStatement(sqlDelete);
            stDelete.setString(1, idReserva);
            int rowsAffected = stDelete.executeUpdate();

            if (rowsAffected > 0) {
                // Actualizar el estado del aula/laboratorio a "Disponible"
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

        stSelect.close();
        conectar.close();
    }

    public void actualizarEstadoAulaLab(int aulaLabId, String nuevoEstado) throws SQLException {
        Connection conectar = conexionLocal();
        String sql = "UPDATE aulas_laboratorios SET Estado = ? WHERE Id = ? AND Tipo = 'Aula'";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setString(1, nuevoEstado);
        st.setInt(2, aulaLabId);
        st.executeUpdate();
        st.close();
        conectar.close();
    }

    public Connection conexionLocal() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/miaulaesfot";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    public Connection conexionNube() throws SQLException {
        String url = "jdbc:mysql://bwhrnrxq2kqlsgfno7nj-mysql.services.clever-cloud.com:3306/bwhrnrxq2kqlsgfno7nj";
        String user = "uptlyedjy2kfhb4h";
        String password = "2bmWngRsgFBMmwDLpwPV";
        return DriverManager.getConnection(url, user, password);
    }
}
