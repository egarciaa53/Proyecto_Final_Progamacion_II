/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import conexion.CreateConection;
import model.proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author edson
 */

public class ProveedorDao {
    private final CreateConection connFactory = new CreateConection();
    
    public List<proveedor> obtenerTodos() {
        List<proveedor> lista = new ArrayList<>();
        String sql = "SELECT id_proveedor, empresa, direccion, telefono, nit, ciudad, pais, "
                + "contacto, email, telefono_contacto, estatus FROM proveedor ORDER BY id_proveedor";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new proveedor(rs.getInt("id_proveedor"), rs.getString("empresa"),
                        rs.getString("direccion"), rs.getInt("telefono"), rs.getString("nit"),
                        rs.getString("ciudad"), rs.getString("pais"), rs.getString("contacto"),
                        rs.getString("email"), rs.getInt("telefono_contacto"),
                        rs.getInt("estatus") == 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public boolean guardar(proveedor prov) {
        String sql = "INSERT INTO proveedor (id_proveedor, empresa, direccion, telefono, nit, ciudad, "
                + "pais, contacto, email, telefono_contacto, estatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, prov.getId_proveedor());
            ps.setString(2, prov.getEmpresa());
            ps.setString(3, prov.getDireccion());
            ps.setLong(4, prov.getTelefono());
            ps.setString(5, prov.getNit());
            ps.setString(6, prov.getCiudad());
            ps.setString(7, prov.getPais());
            ps.setString(8, prov.getContacto());
            ps.setString(9, prov.getEmail());
            ps.setLong(10, prov.getTelefono_contacto());
            ps.setInt(11, prov.isEstatus() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean actualizar(proveedor prov) {
        String sql = "UPDATE proveedor SET empresa = ?, direccion = ?, telefono = ?, nit = ?, "
                + "ciudad = ?, pais = ?, contacto = ?, email = ?, telefono_contacto = ?, estatus = ? "
                + "WHERE id_proveedor = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prov.getEmpresa());
            ps.setString(2, prov.getDireccion());
            ps.setLong(3, prov.getTelefono());
            ps.setString(4, prov.getNit());
            ps.setString(5, prov.getCiudad());
            ps.setString(6, prov.getPais());
            ps.setString(7, prov.getContacto());
            ps.setString(8, prov.getEmail());
            ps.setLong(9, prov.getTelefono_contacto());
            ps.setInt(10, prov.isEstatus() ? 1 : 0);
            ps.setInt(11, prov.getId_proveedor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminar(int idProveedor) {
        String sql = "DELETE FROM proveedor WHERE id_proveedor = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
