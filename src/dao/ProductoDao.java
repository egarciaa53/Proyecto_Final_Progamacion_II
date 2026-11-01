/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import conexion.CreateConection;
import model.producto;

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
public class ProductoDao {
    private final CreateConection connFactory = new CreateConection();
    
    public List<producto> obtenerTodos() {
        List<producto> lista = new ArrayList<>();
        String sql = "SELECT id_producto, descripcion, stock, stock_minimo, precio_unitario, "
                + "id_proveedor FROM producto ORDER BY id_producto";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new producto(rs.getInt("id_producto"), rs.getString("descripcion"),
                        rs.getInt("stock"), rs.getInt("stock_minimo"),
                        rs.getDouble("precio_unitario"), rs.getInt("id_proveedor")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public boolean guardar(producto prod) {
        String sql = "INSERT INTO producto (id_producto, descripcion, stock, stock_minimo, "
                + "precio_unitario, id_proveedor) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, prod.getId_producto());
            ps.setString(2, prod.getDescripcion());
            ps.setInt(3, prod.getStock());
            ps.setInt(4, prod.getStock_minimo());
            ps.setDouble(5, prod.getPrecio_unitario());
            ps.setInt(6, prod.getId_proveedor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean actualizar(producto prod) {
        String sql = "UPDATE producto SET descripcion = ?, stock = ?, stock_minimo = ?, "
                + "precio_unitario = ?, id_proveedor = ? WHERE id_producto = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prod.getDescripcion());
            ps.setInt(2, prod.getStock());
            ps.setInt(3, prod.getStock_minimo());
            ps.setDouble(4, prod.getPrecio_unitario());
            ps.setInt(5, prod.getId_proveedor());
            ps.setInt(6, prod.getId_producto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminar(int idProducto) {
        String sql = "DELETE FROM producto WHERE id_producto = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
