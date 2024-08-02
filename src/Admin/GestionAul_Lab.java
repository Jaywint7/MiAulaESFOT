package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GestionAul_Lab extends JFrame {
    private JPanel JPanel_GestAulLab;
    private JPanel JPanel_EditAdmin;
    private JButton completarButton;
    private JTextField txtidEdit;
    private JTextField txtNomEdit;
    private JTextField txtCapaEdit;
    private JTextField txtNumAuLabEdit;
    private JComboBox ComboEstadEdit;
    private JButton editarButton;
    private JPanel JPanel_Add;
    private JTextField txtNomAdd;
    private JTextField txtCapaAdd;
    private JTextField txtNumAuLabAdd;
    private JComboBox ComboEstadAdd;
    private JButton añadirAulaOLaboratorioButton;
    private JTextField txtidSearch;
    private JButton buscarButton;
    private JPanel JPanel_BorrAdmin;
    private JTextField txtidDelete;
    private JButton eliminarButton;
    private JButton regresarButton;
    private JComboBox ComboFiltrado;
    private JScrollPane JScroll_tabla;
    private JTable tablaGestAul_Lab;
    private JComboBox ComboTipoAdd;
    private JComboBox ComboCarrAdd;
    private JComboBox ComboTipoEdit;
    private JComboBox ComboCarrEdit;

    public GestionAul_Lab(){
        super("Gestion de Usuarios");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(JPanel_GestAulLab);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        regresarButton.setSize(20,20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        regresarButton.setIcon(new ImageIcon(img));

        tablaGestAul_Lab.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Id", "Tipo", "Nombre", "Capacidad", "Numero de Aula o Laboratorio", "Carrera", "Estado"}
        ));

        añadirAulaOLaboratorioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    añadir();
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

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuAdmin menu = new menuAdmin();
                menu.setVisible(true);
                dispose();
            }
        });
    }

    public void añadir() throws SQLException {
        String tipo = (String) ComboTipoAdd.getSelectedItem();
        String nombre = txtNomAdd.getText().trim();
        String capacidad = txtCapaAdd.getText().trim();
        String carrera = (String) ComboCarrAdd.getSelectedItem();
        String numAulLab = txtNumAuLabAdd.getText().trim();
        String estado = (String) ComboEstadAdd.getSelectedItem();

        // Validaciones de campos vacíos
        if (tipo == null || nombre.isEmpty() || capacidad.isEmpty() || carrera == null || numAulLab.isEmpty() || estado == null) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            return;
        }

        // Validación del ingreso de nunmeros
        int capacidadInt;
        try {
            capacidadInt = Integer.parseInt(capacidad);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "La capacidad debe ser un numero.");
            return;
        }

        int numAulLabInt;
        try {
            numAulLabInt = Integer.parseInt(numAulLab);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El numero de aula/laboratorio debe ser un numero.");
            return;
        }

        Connection conectar = null;
        PreparedStatement st = null;

        try {
            conectar = conexion();
            String sql = "INSERT INTO aulas_laboratorios (Tipo, Nombre, Capacidad, NumeroAulLab, Carrera, Estado)value(?,?,?,?,?,?)";
            st = conectar.prepareStatement(sql);
            st.setString(1, tipo);
            st.setString(2, nombre);
            st.setInt(3, capacidadInt);
            st.setInt(4, numAulLabInt);
            st.setString(5, carrera);
            st.setString(6, estado);

            int rowsAffected = st.executeUpdate();
            if ((rowsAffected > 0)) {
                JOptionPane.showMessageDialog(null, "REGISTRO INSERTADO CORRECTAMENTE");
                ComboTipoAdd.setSelectedItem(null);
                txtNomAdd.setText("");
                txtCapaAdd.setText("");
                txtNumAuLabAdd.setText("");
                ComboCarrAdd.setSelectedItem(null);
                ComboEstadAdd.setSelectedItem(null);
            } else {
                JOptionPane.showMessageDialog(null, "EROR EN EL REGISTRO");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la conexion a la base de datos: " + e.getMessage());
        } finally {
            if (st != null) st.close();
            if (conectar != null) conectar.close();
        }
    }

    public void buscar() throws SQLException {
        Connection conectar = conexion();
        String sql;
        PreparedStatement st;

        String filtro = txtidSearch.getText().trim();
        String eleccion = (String) ComboFiltrado.getSelectedItem();

        if (filtro.isEmpty()) {
            // Si está vacío, buscar todos los registros
            sql = "SELECT * FROM aulas_laboratorios";
            st = conectar.prepareStatement(sql);
        } else {
            switch (eleccion) {
                case "Id":
                    sql = "SELECT * FROM aulas_laboratorios WHERE Id = ?";
                    st = conectar.prepareStatement(sql);
                    st.setInt(1, Integer.parseInt(filtro));
                    break;
                case "Tipo":
                    sql = "SELECT * FROM aulas_laboratorios WHERE Tipo LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                case "Nombre":
                    sql = "SELECT * FROM aulas_laboratorios WHERE Nombre LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                case "Capacidad":
                    sql = "SELECT * FROM aulas_laboratorios WHERE Capacidad LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                case "Numero de Aula o Laboratorio":
                    sql = "SELECT * FROM aulas_laboratorios WHERE NumeroAulLab LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                case "Carrera":
                    sql = "SELECT * FROM aulas_laboratorios WHERE Carrera LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                case "Estado":
                    sql = "SELECT * FROM aulas_laboratorios WHERE Estado LIKE ?";
                    st = conectar.prepareStatement(sql);
                    st.setString(1, "%" + filtro + "%");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Seleccion no valida");
                    return;
            }
        }

        ResultSet rs = st.executeQuery();
        DefaultTableModel model = (DefaultTableModel) tablaGestAul_Lab.getModel();
        model.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt("Id"),
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

    public void cargarRegistros() throws SQLException{
        Connection conectar = conexion();
        String sql = "SELECT * FROM aulas_laboratorios";

        PreparedStatement st = conectar.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        DefaultTableModel model = (DefaultTableModel) tablaGestAul_Lab.getModel();
        model.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt("Id"),
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

    public void editar() throws SQLException {
        String id = txtidEdit.getText();
        String tipo = (String) ComboTipoEdit.getSelectedItem();
        String nombre = txtNomEdit.getText().trim();
        String capacidadStr = txtCapaEdit.getText().trim();
        String carrera = (String) ComboCarrEdit.getSelectedItem();
        String numAulLabStr = txtNumAuLabEdit.getText().trim();
        String estado = (String) ComboEstadEdit.getSelectedItem();

        // Validaciones de campos vacíos
        if (tipo == null || nombre.isEmpty() || capacidadStr.isEmpty() || carrera == null || numAulLabStr.isEmpty() || estado == null) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            return;
        }

        int capacidad;
        int numAulLab;
        try {
            capacidad = Integer.parseInt(capacidadStr);
            numAulLab = Integer.parseInt(numAulLabStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "La capacidad y el numero de aula/laboratorio deben ser numeros.");
            return;
        }

        Connection conectar = conexion();
        String sql = "UPDATE aulas_laboratorios SET Tipo = ?, Nombre = ?, Capacidad = ?, NumeroAulLab = ?, Carrera = ?, Estado = ? WHERE Id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);

        st.setString(1, tipo);
        st.setString(2, nombre);
        st.setInt(3, capacidad);
        st.setInt(4, numAulLab);
        st.setString(5, carrera);
        st.setString(6, estado);
        st.setInt(7, Integer.parseInt(id));


        int rowsAffected = st.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "REGISTRO ACTUALIZADO CORRECTAMENTE");
            txtidEdit.setText("");
            ComboTipoEdit.setSelectedItem(null);
            txtNomEdit.setText("");
            txtCapaEdit.setText("");
            txtNumAuLabEdit.setText("");
            ComboCarrEdit.setSelectedItem(null);
            ComboEstadEdit.setSelectedItem(null);
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
        String sql = "SELECT * FROM aulas_laboratorios WHERE Id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setString(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            // Llenar los campos de texto con los datos obtenidos
            String tipo = rs.getString("Tipo");
            ComboTipoEdit.setSelectedItem(tipo);
            txtNomEdit.setText(rs.getString("Nombre"));
            txtCapaEdit.setText(String.valueOf(rs.getInt("Capacidad"))); // Convertir a String para setText
            txtNumAuLabEdit.setText(String.valueOf(rs.getInt("NumeroAulLab")));
            String carrera = rs.getString("Carrera");
            ComboCarrEdit.setSelectedItem(carrera);
            String estado = rs.getString("Estado");
            ComboEstadEdit.setSelectedItem(estado);

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
        String sql = "DELETE FROM aulas_laboratorios WHERE Id = ?";
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
        String url = "jdbc:mysql://localhost:3306/miaulaesfot";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }
}
