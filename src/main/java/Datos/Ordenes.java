package Datos;

import java.awt.HeadlessException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Ordenes {
    private int Id;
    private int ClienteId;
    private Date FechaOrden;
    private double MontoTotal;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public Date getFechaOrden() {
        return FechaOrden;
    }

    public void setFechaOrden(Date FechaOrden) {
        this.FechaOrden = FechaOrden;
    }

    public double getMontoTotal() {
        return MontoTotal;
    }

    public void setMontoTotal(double MontoTotal) {
        this.MontoTotal = MontoTotal;
    }

    public int getClienteId() {
        return ClienteId;
    }

    public void setClienteId(int ClienteId) {
        this.ClienteId = ClienteId;
    }

    public void cargarDatosID(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        comboBox.addItem("Seleccione un Cliente");
        String consulta = "SELECT ClienteID, Nombre, Apellido FROM Clientes;";
        try {
            ComunDB conexion = new ComunDB();
            CallableStatement cs = conexion.obtenerConexion().prepareCall(consulta);
            ResultSet rs = cs.executeQuery();
            
            while (rs.next()) {
                comboBox.addItem(rs.getInt("ClienteID") + " - " + rs.getString("Nombre") + " " + rs.getString("Apellido"));
            }
            
            rs.close();
            cs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos del JComboBox: " + e.toString());
        }
    }

    public void crear(JTextField paramFechaOrden, JTextField paramMontoTotal, JComboBox<String> paramClienteId) {
        double MontoTotal;
        try {
            MontoTotal = Double.parseDouble(paramMontoTotal.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: El valor de MontoTotal debe ser un número.");
            return;
        }

        String clienteIdStr = (String) paramClienteId.getSelectedItem();
        if (clienteIdStr == null || clienteIdStr.equals("Seleccione un Cliente")) {
            JOptionPane.showMessageDialog(null, "Seleccione un Cliente válido.");
            return;
        }

        int ClienteId = Integer.parseInt(clienteIdStr.split(" - ")[0]);

        String consulta = "INSERT INTO Ordenes (FechaOrden, ClienteId, MontoTotal) VALUES (?, ?, ?)";
        try {
            CallableStatement cs = ComunDB.obtenerConexion().prepareCall(consulta);

            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date parsedDate = inputDateFormat.parse(paramFechaOrden.getText());
            Date sqlDate = new Date(parsedDate.getTime());

            cs.setDate(1, sqlDate);
            cs.setInt(2, ClienteId);
            cs.setDouble(3, MontoTotal);

            cs.execute();

            JOptionPane.showMessageDialog(null, "Se insertó correctamente la orden");
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Error al convertir la fecha: Formato de fecha incorrecto.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar la orden: " + e.getMessage());
        }
    }

    public void seleccionarOrdenes(JTable paramTablaOrdenes, JTextField paramId, JTextField paramFechaOrden, JComboBox<String> paramClienteId, JTextField paramMontoTotal) {
        try {
            int fila = paramTablaOrdenes.getSelectedRow();

            if (fila >= 0) {
                paramId.setText(paramTablaOrdenes.getValueAt(fila, 0).toString());
                paramClienteId.setSelectedItem(paramTablaOrdenes.getValueAt(fila, 1).toString() + " - " + obtenerNombreCliente((int) paramTablaOrdenes.getValueAt(fila, 1)));
                paramFechaOrden.setText(paramTablaOrdenes.getValueAt(fila, 2).toString());
                paramMontoTotal.setText(paramTablaOrdenes.getValueAt(fila, 3).toString());
            } else {
                JOptionPane.showMessageDialog(null, "Ninguna fila seleccionada");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al seleccionar la fila: " + e.toString());
        }
    }

    private String obtenerNombreCliente(int clienteId) {
        String consulta = "SELECT Nombre, Apellido FROM Clientes WHERE ClienteID = ?";
        try (Connection con = ComunDB.obtenerConexion(); CallableStatement cs = con.prepareCall(consulta)) {
            cs.setInt(1, clienteId);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                return rs.getString("Nombre") + " " + rs.getString("Apellido");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el nombre del cliente: " + e.toString());
        }
        return "";
    }

    public void modificarOrdenes(JTable paramTablaOrdenes, JTextField paramFechaOrden, JComboBox<String> paramClienteId, JTextField paramMontoTotal) {
        try {
            int selectedRow = paramTablaOrdenes.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Ninguna fila seleccionada. Por favor, seleccione una fila para modificar.");
                return;
            }

            int Id = Integer.parseInt(paramTablaOrdenes.getValueAt(selectedRow, 0).toString());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date parsedDate = dateFormat.parse(paramFechaOrden.getText());
            Date sqlDate = new Date(parsedDate.getTime());

            String clienteIdStr = (String) paramClienteId.getSelectedItem();
            if (clienteIdStr == null || clienteIdStr.equals("Seleccione un Cliente")) {
                JOptionPane.showMessageDialog(null, "Seleccione un Cliente válido.");
                return;
            }

            int ClienteId = Integer.parseInt(clienteIdStr.split(" - ")[0]);
            BigDecimal MontoTotal = new BigDecimal(paramMontoTotal.getText());

            String consulta = "UPDATE Ordenes SET FechaOrden = ?, ClienteId = ?, MontoTotal = ? WHERE OrdenId = ?";
            try (Connection con = ComunDB.obtenerConexion(); CallableStatement cs = con.prepareCall(consulta)) {

                cs.setDate(1, sqlDate);
                cs.setInt(2, ClienteId);
                cs.setBigDecimal(3, MontoTotal);
                cs.setInt(4, Id);

                cs.execute();
                JOptionPane.showMessageDialog(null, "Orden modificada exitosamente");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al modificar la orden: " + e.toString());
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Error al convertir la fecha: Formato de fecha incorrecto.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: Formato de número incorrecto.");
        }
    }

     public void eliminarOrden(JTextField IdField) {
        try {
            int Id = Integer.parseInt(IdField.getText());
            String consulta = "DELETE FROM Ordenes WHERE OrdenId = ?"; 

            try (Connection con = ComunDB.obtenerConexion(); CallableStatement cs = con.prepareCall(consulta)) {
                cs.setInt(1, Id);
                cs.execute();
                JOptionPane.showMessageDialog(null, "Eliminación exitosa: " + Id);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error, no se puede eliminar el Id " + Id + ", error: " + e.toString());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: El ID de Orden debe ser un número válido.");
        }
    }
    
    public void mostrarOrdenes(JTable tablaOrdenes) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("OrdenId");
        modelo.addColumn("ClienteId");
        modelo.addColumn("FechaOrden");
        modelo.addColumn("MontoTotal");

        tablaOrdenes.setModel(modelo);

        String sql = "SELECT * FROM Ordenes;";

        try (Connection connection = ComunDB.obtenerConexion(); Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Object[] datos = new Object[4];
                datos[0] = rs.getInt("OrdenId");
                datos[1] = rs.getInt("ClienteId");
                datos[2] = rs.getDate("FechaOrden");
                datos[3] = rs.getDouble("MontoTotal");

                modelo.addRow(datos);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar los registros de Ordenes: " + e.getMessage());
        }
    }
}
