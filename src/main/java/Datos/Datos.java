/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Datos;

import java.awt.HeadlessException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author MINEDUCYT
 */
public class Datos {

    private int Id;
    private String Nombre;
    private String Apellido;
    private String Correo;
    private String Telefono;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String Apellido) {
        this.Apellido = Apellido;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String Correo) {
        this.Correo = Correo;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public void crear(JTextField paramNombre, JTextField paramApellido, JTextField paramCorreo, JTextField paramTelefono) {

        // Establecer valores en el objeto
        setNombre(paramNombre.getText());
        setApellido(paramApellido.getText());
        setCorreo(paramCorreo.getText());
        setTelefono(paramTelefono.getText());

        // Insertar la cuenta en la base de datos
        String consulta = "INSERT INTO Clientes (Nombre, Apellido, Correo, Telefono) VALUES (?, ?, ?, ?);";
        try {
            CallableStatement cs = ComunDB.obtenerConexion().prepareCall(consulta);
            cs.setString(1, getNombre());
            cs.setString(2, getApellido());
            cs.setString(3, getCorreo());
            cs.setString(4, getTelefono());
            cs.execute();

            JOptionPane.showMessageDialog(null, "Se insertó correctamente");
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar: " + e.toString());
        }
    }

    public void MostrarCliente(JTable paramTabla) {

        DefaultTableModel modelo = new DefaultTableModel();

        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(modelo);
        paramTabla.setRowSorter(ordenarTabla);

        var sql = "";
        modelo.addColumn("Id");
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellido");
        modelo.addColumn("Correo");
        modelo.addColumn("Telefono");

        paramTabla.setModel(modelo);

        sql = "SELECT * FROM Clientes;";

        String[] datos = new String[5];
        Statement st;
        try {
            st = ComunDB.obtenerConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString(1);
                datos[1] = rs.getString(2);
                datos[2] = rs.getString(3);
                datos[3] = rs.getString(4);
                datos[4] = rs.getString(5);
                // datos[5] = rs.getString(6);

                modelo.addRow(datos);

            }
            paramTabla.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar los registros: " + e.toString());
        }

    }

    public void SeleccionarCliente(JTable paramTablaCliente, JTextField paramClienteID, JTextField paramNombre, JTextField paramApellido, JTextField paramCorreo, JTextField paramTelefono) {
        try {
            int fila = paramTablaCliente.getSelectedRow();
            if (fila >= 0) { // Verifica que se ha seleccionado una fila válida
                paramClienteID.setText(paramTablaCliente.getValueAt(fila, 0).toString());
                paramNombre.setText(paramTablaCliente.getValueAt(fila, 1).toString());
                paramApellido.setText(paramTablaCliente.getValueAt(fila, 2).toString());
                paramCorreo.setText(paramTablaCliente.getValueAt(fila, 3).toString());
                paramTelefono.setText(paramTablaCliente.getValueAt(fila, 4).toString());
            } else {
                JOptionPane.showMessageDialog(null, "Ninguna fila seleccionada");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al seleccionar la fila: " + e.toString());
        }
    }

    

    public void Editar(JTextField paramNombre, JTextField paramApellido, JTextField paramCorreo, JTextField paramTelefono, int clienteId) {
        setNombre(paramNombre.getText());
        setApellido(paramApellido.getText());
        setCorreo(paramCorreo.getText());
        setTelefono(paramTelefono.getText());

        String consulta = "UPDATE Clientes SET Nombre = ?, Apellido = ?, Correo = ?, Telefono = ? WHERE ClienteId = ?;";

        try (Connection conn = ComunDB.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(consulta)) {

            // Configuramos los valores de los parámetros
            ps.setString(1, getNombre());
            ps.setString(2, getApellido());
            ps.setString(3, getCorreo());
            ps.setString(4, getTelefono());
            ps.setInt(5, clienteId); // Configura el quinto parámetro (ClienteId)

            // Ejecutamos la actualización
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Modificación exitosa");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al Modificar, error: " + e.toString());
        }
    }

    public void EliminarCliente(JTextField paramId) {
        ComunDB conexion = new ComunDB();
        String consulta = "DELETE FROM Clientes WHERE ClienteId = ?";
        try {
            int Id = Integer.parseInt(paramId.getText()); // Obtener el texto y convertirlo a int
            CallableStatement cs = conexion.obtenerConexion().prepareCall(consulta);
            cs.setInt(1, Id); // Usar el valor int aquí
            cs.execute();
            JOptionPane.showMessageDialog(null, "Eliminación exitosa del movimiento con ID: " + Id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: El MovimientoID debe ser un número.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el movimiento con ID " + Id + ", error: " + e.toString());
        }
    }
}