/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author MINEDUCYT
 */
public class Ordenes {
    private int Id;
    private int ClienteId;
    private Date fehaOrden;
    private double MontoTotal;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public Date getFehaOrden() {
        return fehaOrden;
    }

    public void setFehaOrden(Date fehaOrden) {
        this.fehaOrden = fehaOrden;
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
    
    public void crear(JTextField paramFechaOrden, JTextField paramMontoTotal) {
        
         String consulta = "INSERT INTO Ordenes (FechaOrden, MOntoTotal) VALUES (?, ?)";
    try {
        CallableStatement cs = ComunDB.obtenerConexion().prepareCall(consulta);

        // Convertir la fecha al formato adecuado para la base de datos
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date parsedDate = inputDateFormat.parse(paramFechaOrden.getText());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = outputDateFormat.format(parsedDate);

        // Establecer los valores de los parámetros
        cs.setString(1, formattedDate);
        cs.setDouble(3, MontoTotal);
       

        // Ejecutar la consulta
        cs.execute();

        JOptionPane.showMessageDialog(null, "Se insertó correctamente la orden");
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Error: Los valores de FechaOrden y MontoTotal deben ser números.");
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(null, "Error al convertir la fecha: Formato de fecha incorrecto.");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al insertar la orden: " + e.getMessage());
    }
    }
    
     public void mostrarOrdenes(JTable tablaOrdenes) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Id");
        modelo.addColumn("ClienteId");
        modelo.addColumn("FechaOrden");
        modelo.addColumn("MontoTotal");
       

        tablaOrdenes.setModel(modelo);

        String sql = "SELECT * FROM Ordenes;";

        try (Connection connection = ComunDB.obtenerConexion(); Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Object[] datos = new Object[4];
                datos[0] = rs.getInt("Id");
                datos[1] = rs.getInt("ClienteId");
                datos[2] = rs.getDate("FechaOrden");
                datos[3] = rs.getDouble("MontoTotal");
               

                modelo.addRow(datos);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar los registros de Ordenes: " + e.getMessage());
        }
}
     
      public void SeleccionarOrdenes(JTable paramTablaOrdenes, JTextField paramId, JTextField paramFechaOrden, JComboBox<String> paramClienteId, JTextField MontoTotal) {
      
          try {
            int fila = paramTablaOrdenes.getSelectedRow();
            
            
            if (fila >= 0) { // Verifica que se ha seleccionado una fila válida
                paramId.setText(paramTablaOrdenes.getValueAt(fila, 0).toString());
                paramClienteId.setSelectedItem(paramTablaOrdenes.getValueAt(fila, 1).toString());

                // Si paramClienteId es un JComboBox y quieres establecer un elemento seleccionado, usa setSelectedItem
                Object valorClienteId = paramTablaOrdenes.getValueAt(fila, 2);
                if (valorClienteId != null) {
                    paramClienteId.setSelectedItem( valorClienteId.toString());
                } else {
                    paramClienteId.setSelectedItem(null); // Establecer el JComboBox como nulo si el valor es nulo
                }

                MontoTotal.setText(paramTablaOrdenes.getValueAt(fila, 3).toString());
               
            } else {
                JOptionPane.showMessageDialog(null, "Ninguna fila seleccionada");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al seleccionar la fila: " + e.toString());
        }
    }
      
      public void ModificarOrdenes(JTable paramTablaOrdenes, JTextField paramFechaOrdenes, JTextField paramMontoTotal, JComboBox<String> paramClienteId) {
        try {
            // Verificar que se ha seleccionado una fila
            int selectedRow = paramTablaOrdenes.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Niguna fila seleccionada. Por favor seleccione una fila.");
                return;
            }

            // Recuperar Id de la fila seleccionada
            int Id = (int) paramTablaOrdenes.getValueAt(selectedRow, 0);
            setId(Id);

            // Convertir cadena de fecha a java.sql.Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date parsedDate = dateFormat.parse(paramFechaOrdenes.getText());
            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
            setFechaOrden(sqlDate);

            // Extraer valor de JComboBox y convertirlo a entero
            int clienteId = Integer.parseInt((String) paramClienteId.getSelectedItem());
            setClienteId(clienteId);

            // Convertir el monto total a double
            double montoTotal = Double.parseDouble(paramMontoTotal.getText());
            setMontoTotal(montoTotal);

            // Consulta para actualizar la orden
            String consulta = "UPDATE Ordenes SET FechaOrden = ?, MontoTotal = ?, ClienteId = ? WHERE OrdenId = ?;";

            try (Connection connection = ComunDB.obtenerConexion();
                 CallableStatement cs = connection.prepareCall(consulta)) {

                // Establecer los valores de los parámetros
                cs.setDate(1, getFechaOrden());
                cs.setDouble(2, getMontoTotal());
                cs.setInt(3, getClienteId());
                cs.setInt(4, getId());

                // Ejecutar la consulta
                cs.execute();
                JOptionPane.showMessageDialog(null, "Modificación exitosa");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al Modificar, error: " + e.toString());
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Error al convertir la fecha: Formato de fecha incorrecto.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: Formato de número incorrecto.");
        }
    }
}

       
       

