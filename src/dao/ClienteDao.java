/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import conexion.CreateConection;
import model.cliente;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author edson
 */
public class ClienteDao {
    private final CreateConection connFactory = new CreateConection();
    
    public List<cliente> obtenerTodos() {
        List<cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, apellido, razon_social, nit, direccion, "
                + "telefono, email, fecha_ingreso, estatus FROM cliente ORDER BY id_cliente";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { Date fechaIngreso = rs.getDate("fecha_ingreso");
                lista.add(new cliente(rs.getInt("id_cliente"), rs.getString("nombre"), 
                        rs.getString("apellido"), rs.getString("razon_social"), 
                        rs.getString("nit"), rs.getString("direccion"),
                        rs.getString("telefono"), rs.getString("email"),
                        fechaIngreso == null ? "" : fechaIngreso.toString(), rs.getInt("estatus") == 1));
            }
        } catch (SQLException e) { e.printStackTrace();
            }
        return lista;
    }
    public boolean guardar(cliente cl) {
        String sql = "INSERT INTO cliente (id_cliente, nombre, apellido, razon_social, nit, "
                + "direccion, telefono, email, fecha_ingreso, estatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connFactory.getConection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cl.getId_cliente());
            ps.setString(2, cl.getNombre());
            ps.setString(3, cl.getApellido());
            ps.setString(4, cl.getRazon_social());
            ps.setString(5, cl.getNit());
            ps.setString(6, cl.getDireccion());
            ps.setString(7, cl.getTelefono());
            ps.setString(8, cl.getEmail());
            ps.setDate(9, Date.valueOf(cl.getFecha_ingreso()));
            ps.setInt(10, cl.isEstatus() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean actualizar(cliente cl) {
        String sql = "UPDATE cliente SET nombre = ?, apellido = ?, razon_social = ?, nit = ?, "
                + "direccion = ?, telefono = ?, email = ?, fecha_ingreso = ?, estatus = ? "
                + "WHERE id_cliente = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cl.getNombre());
            ps.setString(2, cl.getApellido());
            ps.setString(3, cl.getRazon_social());
            ps.setString(4, cl.getNit());
            ps.setString(5, cl.getDireccion());
            ps.setString(6, cl.getTelefono());
            ps.setString(7, cl.getEmail());
            ps.setDate(8, Date.valueOf(cl.getFecha_ingreso()));
            ps.setInt(9, cl.isEstatus() ? 1 : 0);
            ps.setInt(10, cl.getId_cliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminar(int idCliente) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
