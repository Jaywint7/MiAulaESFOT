package Admin;

import Inicio.ManejarHash;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static Admin.ManejarHash.hashPassword;

public class GestionUsu extends JFrame {
    private JPanel JPanel_GestionUsu;
    private JButton añadirUsuarioButton;
    private JTable tablaGestUsu;
    private JButton editarButton;
    private JButton eliminarButton;
    private JButton completarButton;
    private JButton buscarButton;
    private JTextField txtidSearch;
    private JPanel JPanel_EditAdmin;
    private JPanel JPanel_BorrAdmin;
    private JTextField txtNomEdit;
    private JTextField txtApeEdit;
    private JTextField txtEmailEdit;
    private JPasswordField txtContraEdit;
    private JTextField txtidEdit;
    private JComboBox ComboEdit;
    private JTextField txtNomAdd;
    private JTextField txtApeAdd;
    private JTextField txtEmailAdd;
    private JPasswordField txtContAdd;
    private JComboBox ComboAdd;
    private JTextField txtidDelete;
    private JButton regresarButton;
    private JPanel JPanel_Add;
    private JComboBox comboBox1;
    private JScrollPane JScroll_tabla;

    public GestionUsu(){
        super("Gestion de Usuarios");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(JPanel_GestionUsu);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tablaGestUsu.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Id", "Nombre", "Apellido", "Email", "Contraseña", "Tipo de Usuario"}
        ));

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buscar();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        try {
            cargarRegistros();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        añadirUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    añadir();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
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
                menuAdmin menu = new menuAdmin();
                menu.setVisible(true);
                dispose();
            }
        });

        completarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    completar();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    editar();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    eliminar();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void añadir() throws SQLException {
        String nombre = txtNomAdd.getText().trim();
        String apellido = txtApeAdd.getText().trim();
        String email = txtEmailAdd.getText().trim();
        String contraseña = new String(txtContAdd.getPassword()).trim();
        String tipo_usuario = (String) ComboAdd.getSelectedItem();

        // Validaciones de campos vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || contraseña.isEmpty() || tipo_usuario == null) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            return;
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
            txtNomAdd.setText("");
            txtApeAdd.setText("");
            txtEmailAdd.setText("");
            txtContAdd.setText("");
        }else{
            JOptionPane.showMessageDialog(null, "EROR EN EL REGISTRO");
        }
        st.close();
        conectar.close();
    }


    public void buscar() throws SQLException {
        Connection conectar = conexion();
        String sql;
        PreparedStatement st;
        if (txtidSearch.getText().trim().isEmpty()) {
            // Si está vacío, buscar todos los registros
            sql = "SELECT * FROM usuarios";
            st = conectar.prepareStatement(sql);
        } else {
            // Si tiene un valor, buscar por id
            int id = Integer.parseInt(txtidSearch.getText());
            sql = "SELECT * FROM usuarios WHERE id = ?";
            st = conectar.prepareStatement(sql);
            st.setInt(1, id);
        }

        ResultSet rs = st.executeQuery();
        DefaultTableModel model = (DefaultTableModel) tablaGestUsu.getModel();
        model.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("contraseña"),
                    rs.getString("tipo_usuario")
            });
        }

        rs.close();
        st.close();
        conectar.close();
    }

    public void cargarRegistros() throws SQLException{
        Connection conectar = conexion();
        String sql = "SELECT * FROM usuarios";

        PreparedStatement st = conectar.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        DefaultTableModel model = (DefaultTableModel) tablaGestUsu.getModel();
        model.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("contraseña"),
                    rs.getString("tipo_usuario")
            });
        }
        rs.close();
        st.close();
        conectar.close();
    }

    public void editar() throws SQLException {
        String id = txtidEdit.getText();
        String nombre = txtNomEdit.getText().trim();
        String apellido = txtApeEdit.getText().trim();
        String email = txtEmailEdit.getText().trim();
        String contraseña = new String(txtContraEdit.getPassword()).trim();
        String tipo_usuario = (String) ComboEdit.getSelectedItem();

        // Validaciones de campos vacíos
        if (id.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || contraseña.isEmpty() || tipo_usuario == null) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            return;
        }

        String ContraHash = ManejarHash.hashPassword(contraseña);

        Connection conectar = conexion();
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, contraseña = ?, tipo_usuario = ? WHERE id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);

        st.setString(1, nombre);
        st.setString(2, apellido);
        st.setString(3, email);
        st.setString(4, hashPassword(contraseña)); // Hashing the password
        st.setString(5, tipo_usuario);
        st.setString(6, id);

        int rowsAffected = st.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "REGISTRO ACTUALIZADO CORRECTAMENTE");
            txtNomEdit.setText("");
            txtApeEdit.setText("");
            txtEmailEdit.setText("");
            txtContraEdit.setText("");
            txtidEdit.setText("");
            ComboEdit.setSelectedItem(null);
        } else {
            JOptionPane.showMessageDialog(null, "NO SE ENCONTRO NINGUN REGISTRO CON EL ID PROPORCIONADO");
        }

        st.close();
        conectar.close();
    }

    public void completar() throws SQLException {
        String id = txtidEdit.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el Id para buscar");
            return;
        }

        Connection conectar = conexion();
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setString(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            // Llenar los campos de texto con los datos obtenidos
            txtNomEdit.setText(rs.getString("nombre"));
            txtApeEdit.setText(rs.getString("apellido"));
            txtEmailEdit.setText(rs.getString("email"));
            txtContraEdit.setText(rs.getString("contraseña"));
            String tipoUsuario = rs.getString("tipo_usuario");
            ComboEdit.setSelectedItem(tipoUsuario);

            // Habilitar el botón de actualización
            editarButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(null, "NO SE ENCONTRO NINGUN REGISTRO CON EL ID PROPORCIONADO");
            // Deshabilitar el botón de actualización si no se encontraron datos
            editarButton.setEnabled(false);
        }

        rs.close();
        st.close();
        conectar.close();
    }

    public void eliminar() throws SQLException {
        String id = txtidDelete.getText();

        Connection conectar = conexion();
        String sql = "DELETE FROM usuarios WHERE id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);

        st.setString(1, id);

        int rowsAffected = st.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "REGISTRO ELIMINADO CORRECTAMENTE");
        } else {
            JOptionPane.showMessageDialog(null, "NO SE ENCONTRO NINGUN REGISTRO CON El ID PROPORCIONADO");
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
