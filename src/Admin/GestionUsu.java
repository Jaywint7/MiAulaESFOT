package Admin;

import Inicio.ManejarHash;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Clase que gestiona la administración de usuarios.
 */
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
    private JComboBox<String> ComboEdit;
    private JTextField txtNomAdd;
    private JTextField txtApeAdd;
    private JTextField txtEmailAdd;
    private JPasswordField txtContAdd;
    private JComboBox<String> ComboAdd;
    private JTextField txtidDelete;
    private JButton regresarButton;
    private JPanel JPanel_Add;
    private JComboBox<String> ComboFiltrado;
    private JScrollPane JScroll_tabla;

    /**
     * Constructor de la clase GestionUsu.
     * Configura la interfaz gráfica y añade los listeners a los botones.
     */
    public GestionUsu() {
        super("Gestión de Usuarios");
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

        regresarButton.setSize(20, 20);
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

    /**
     * Añade un nuevo usuario a la base de datos.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
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

        String ContraHash = Admin.ManejarHash.hashPassword(contraseña);

        Connection conectar = conexion();
        String sql = "INSERT INTO usuarios(nombre, apellido, email, contraseña, tipo_usuario) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setString(1, nombre);
        st.setString(2, apellido);
        st.setString(3, email);
        st.setString(4, ContraHash);
        st.setString(5, tipo_usuario);

        int rowsAffected = st.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Registro insertado correctamente");
            txtNomAdd.setText("");
            txtApeAdd.setText("");
            txtEmailAdd.setText("");
            txtContAdd.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Error en el registro");
        }
        st.close();
        conectar.close();
    }

    /**
     * Busca usuarios en la base de datos según el filtro proporcionado.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public void buscar() throws SQLException {
        Connection conectar = conexion();
        String sql;
        PreparedStatement st;

        String filtro = txtidSearch.getText().trim();
        String eleccion = (String) ComboFiltrado.getSelectedItem();

        if (filtro.isEmpty()) {
            // Si está vacío, buscar todos los registros
            sql = "SELECT * FROM usuarios";
            st = conectar.prepareStatement(sql);
        } else {
            switch (eleccion) {
                case "Id":
                    sql = "SELECT * FROM usuarios WHERE id = ?";
                    st = conectar.prepareStatement(sql);
                    st.setInt(1, Integer.parseInt(filtro));
                    break;
                case "Nombre":
                    sql = "SELECT * FROM usuarios WHERE nombre LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                case "Apellido":
                    sql = "SELECT * FROM usuarios WHERE apellido LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                case "Email":
                    sql = "SELECT * FROM usuarios WHERE email LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                case "Tipo de Usuario":
                    sql = "SELECT * FROM usuarios WHERE tipo_usuario LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Selección no válida");
                    return;
            }
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

    /**
     * Carga todos los registros de usuarios desde la base de datos.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public void cargarRegistros() throws SQLException {
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

    /**
     * Edita los datos de un usuario existente en la base de datos.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
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
        st.setString(4, ContraHash);
        st.setString(5, tipo_usuario);
        st.setInt(6, Integer.parseInt(id));

        int rowsAffected = st.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Registro actualizado correctamente");
            txtNomEdit.setText("");
            txtApeEdit.setText("");
            txtEmailEdit.setText("");
            txtContraEdit.setText("");
            txtidEdit.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Error al actualizar el registro");
        }
        st.close();
        conectar.close();
    }

    /**
     * Elimina un usuario de la base de datos.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public void eliminar() throws SQLException {
        String id = txtidDelete.getText().trim();

        // Validación de campo vacío
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo ID es obligatorio.");
            return;
        }

        Connection conectar = conexion();
        String sql = "DELETE FROM usuarios WHERE id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setInt(1, Integer.parseInt(id));

        int rowsAffected = st.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Registro eliminado correctamente");
            txtidDelete.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Error al eliminar el registro");
        }
        st.close();
        conectar.close();
    }

    /**
     * Completa los campos de edición con los datos de un usuario buscado.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public void completar() throws SQLException {
        String id = txtidEdit.getText().trim();

        // Validación de campo vacío
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo ID es obligatorio.");
            return;
        }

        Connection conectar = conexion();
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setInt(1, Integer.parseInt(id));

        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            txtNomEdit.setText(rs.getString("nombre"));
            txtApeEdit.setText(rs.getString("apellido"));
            txtEmailEdit.setText(rs.getString("email"));
            txtContraEdit.setText(rs.getString("contraseña"));
            ComboEdit.setSelectedItem(rs.getString("tipo_usuario"));
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró un usuario con el ID especificado.");
        }
        rs.close();
        st.close();
        conectar.close();
    }

    /**
     * Establece la conexión con la base de datos MySQL.
     * @return Conexión a la base de datos.
     * @throws SQLException Si ocurre un error al conectar con la base de datos.
     */

    public Connection conexion() throws SQLException {
        String url = "jdbc:mysql://bwhrnrxq2kqlsgfno7nj-mysql.services.clever-cloud.com:3306/bwhrnrxq2kqlsgfno7nj";
        String user = "uptlyedjy2kfhb4h";
        String password = "2bmWngRsgFBMmwDLpwPV";
        return DriverManager.getConnection(url, user, password);
    }
}
