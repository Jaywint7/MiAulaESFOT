package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * La clase GestionAul_Lab proporciona una interfaz gráfica para la gestión de aulas y laboratorios.
 * Extiende JFrame y permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los registros de
 * aulas y laboratorios en la base de datos.
 */
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

    /**
     * Constructor de la clase GestionAul_Lab.
     * Configura la interfaz gráfica, inicializa los componentes y establece los eventos de los botones.
     */
    public GestionAul_Lab(){
        super("Gestion de Usuarios");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(JPanel_GestAulLab);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuración del botón regresar
        regresarButton.setSize(20,20);
        ImageIcon icon = new ImageIcon("img/flechaAtras.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        regresarButton.setIcon(new ImageIcon(img));

        // Configuración de la tabla
        tablaGestAul_Lab.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Id", "Tipo", "Nombre", "Capacidad", "Numero de Aula o Laboratorio", "Carrera", "Estado"}
        ));

        // Asignación de eventos a los botones
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

        // Carga inicial de registros en la tabla
        try {
            cargarRegistros();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Añade un nuevo aula o laboratorio a la base de datos.
     * Toma los datos de los campos de texto y los combo boxes, realiza validaciones y ejecuta una consulta SQL INSERT.
     * @throws SQLException si ocurre un error en la conexión a la base de datos o en la ejecución de la consulta.
     */
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

        // Validación del ingreso de números
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
            String sql = "INSERT INTO aulas_laboratorios (Tipo, Nombre, Capacidad, NumeroAulLab, Carrera, Estado) VALUES (?, ?, ?, ?, ?, ?)";
            st = conectar.prepareStatement(sql);
            st.setString(1, tipo);
            st.setString(2, nombre);
            st.setInt(3, capacidadInt);
            st.setInt(4, numAulLabInt);
            st.setString(5, carrera);
            st.setString(6, estado);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "REGISTRO INSERTADO CORRECTAMENTE");
                // Limpiar campos
                ComboTipoAdd.setSelectedItem(null);
                txtNomAdd.setText("");
                txtCapaAdd.setText("");
                txtNumAuLabAdd.setText("");
                ComboCarrAdd.setSelectedItem(null);
                ComboEstadAdd.setSelectedItem(null);
            } else {
                JOptionPane.showMessageDialog(null, "ERROR EN EL REGISTRO");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la conexion a la base de datos: " + e.getMessage());
        } finally {
            if (st != null) st.close();
            if (conectar != null) conectar.close();
        }
    }

    /**
     * Busca registros en la tabla de aulas y laboratorios basados en un filtro.
     * Toma el filtro y el tipo de búsqueda seleccionados y actualiza la tabla con los resultados de la consulta.
     * @throws SQLException si ocurre un error en la conexión a la base de datos o en la ejecución de la consulta.
     */
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
                    throw new IllegalStateException("Unexpected value: " + eleccion);
            }
        }

        ResultSet rs = st.executeQuery();
        DefaultTableModel model = (DefaultTableModel) tablaGestAul_Lab.getModel();
        model.setRowCount(0); // Limpiar tabla

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

    /**
     * Edita los datos de un aula o laboratorio seleccionado.
     * Toma los datos de los campos de texto y los combo boxes, realiza validaciones y ejecuta una consulta SQL UPDATE.
     * @throws SQLException si ocurre un error en la conexión a la base de datos o en la ejecución de la consulta.
     */
    public void editar() throws SQLException {
        String id = txtidEdit.getText().trim();
        String tipo = (String) ComboTipoEdit.getSelectedItem();
        String nombre = txtNomEdit.getText().trim();
        String capacidad = txtCapaEdit.getText().trim();
        String carrera = (String) ComboCarrEdit.getSelectedItem();
        String numAulLab = txtNumAuLabEdit.getText().trim();
        String estado = (String) ComboEstadEdit.getSelectedItem();

        // Validaciones de campos vacíos
        if (id.isEmpty() || tipo == null || nombre.isEmpty() || capacidad.isEmpty() || carrera == null || numAulLab.isEmpty() || estado == null) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            return;
        }

        // Validación del ingreso de números
        int idInt;
        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe ser un numero.");
            return;
        }

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
            String sql = "UPDATE aulas_laboratorios SET Tipo = ?, Nombre = ?, Capacidad = ?, NumeroAulLab = ?, Carrera = ?, Estado = ? WHERE Id = ?";
            st = conectar.prepareStatement(sql);
            st.setString(1, tipo);
            st.setString(2, nombre);
            st.setInt(3, capacidadInt);
            st.setInt(4, numAulLabInt);
            st.setString(5, carrera);
            st.setString(6, estado);
            st.setInt(7, idInt);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "REGISTRO EDITADO CORRECTAMENTE");
                // Limpiar campos
                txtidEdit.setText("");
                ComboTipoEdit.setSelectedItem(null);
                txtNomEdit.setText("");
                txtCapaEdit.setText("");
                txtNumAuLabEdit.setText("");
                ComboCarrEdit.setSelectedItem(null);
                ComboEstadEdit.setSelectedItem(null);
            } else {
                JOptionPane.showMessageDialog(null, "ERROR EN LA EDICIÓN");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la conexion a la base de datos: " + e.getMessage());
        } finally {
            if (st != null) st.close();
            if (conectar != null) conectar.close();
        }
    }

    /**
     * Completa los datos de un aula o laboratorio en los campos de edición basados en el ID proporcionado.
     * @throws SQLException si ocurre un error en la conexión a la base de datos o en la ejecución de la consulta.
     */
    public void completar() throws SQLException {
        String id = txtidEdit.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un ID.");
            return;
        }

        int idInt;
        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe ser un numero.");
            return;
        }

        Connection conectar = conexion();
        String sql = "SELECT * FROM aulas_laboratorios WHERE Id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setInt(1, idInt);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            ComboTipoEdit.setSelectedItem(rs.getString("Tipo"));
            txtNomEdit.setText(rs.getString("Nombre"));
            txtCapaEdit.setText(String.valueOf(rs.getInt("Capacidad")));
            txtNumAuLabEdit.setText(String.valueOf(rs.getInt("NumeroAulLab")));
            ComboCarrEdit.setSelectedItem(rs.getString("Carrera"));
            ComboEstadEdit.setSelectedItem(rs.getString("Estado"));
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró ningún registro con el ID proporcionado.");
        }

        rs.close();
        st.close();
        conectar.close();
    }

    /**
     * Elimina un aula o laboratorio de la base de datos basado en el ID proporcionado.
     * @throws SQLException si ocurre un error en la conexión a la base de datos o en la ejecución de la consulta.
     */
    public void eliminar() throws SQLException {
        String id = txtidDelete.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un ID.");
            return;
        }

        int idInt;
        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe ser un numero.");
            return;
        }

        Connection conectar = conexion();
        String sql = "DELETE FROM aulas_laboratorios WHERE Id = ?";
        PreparedStatement st = conectar.prepareStatement(sql);
        st.setInt(1, idInt);

        int rowsAffected = st.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "REGISTRO ELIMINADO CORRECTAMENTE");
            // Limpiar campos
            txtidDelete.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "ERROR EN LA ELIMINACIÓN");
        }

        st.close();
        conectar.close();
    }

    /**
     * Carga todos los registros de aulas y laboratorios desde la base de datos y los muestra en la tabla.
     * @throws SQLException si ocurre un error en la conexión a la base de datos o en la ejecución de la consulta.
     */
    private void cargarRegistros() throws SQLException {
        Connection conectar = conexion();
        String sql = "SELECT * FROM aulas_laboratorios";
        PreparedStatement st = conectar.prepareStatement(sql);
        ResultSet rs = st.executeQuery();

        DefaultTableModel model = (DefaultTableModel) tablaGestAul_Lab.getModel();
        model.setRowCount(0); // Limpiar tabla

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

    /**
     * Establece una conexión con la base de datos MySQL.
     * @return la conexión a la base de datos.
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    private Connection conexion() throws SQLException {
        // Reemplace estos valores con los detalles de su base de datos
        String url = "jdbc:mysql://localhost:3306/miaulaesfot";
        String usuario = "root";
        String contraseña = "";

        return DriverManager.getConnection(url, usuario, contraseña);
    }
}
