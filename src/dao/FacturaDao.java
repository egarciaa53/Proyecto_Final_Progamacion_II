/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import conexion.CreateConection;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.factura;
import model.factura_detalle;

/**
 *
 * @author edson
 */
public class FacturaDao {
    private final CreateConection connFactory = new CreateConection();
    private final Factura_detalleDao detalleDao = new Factura_detalleDao();
    
    public List<factura> obtenerTodos() {
        List<factura> lista = new ArrayList<>();
        String sql = "SELECT id_factura, no_fact, serie, id_cliente, id_empleado, fecha_fac "
                + "FROM factura ORDER BY id_factura";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new factura(rs.getInt("id_factura"), rs.getInt("no_fact"),
                        rs.getString("serie"), rs.getInt("id_cliente"),
                        rs.getInt("id_empleado"), rs.getString("fecha_fac")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public int obtenerSiguienteId() {
        String sql = "SELECT COALESCE(MAX(id_factura), 0) + 1 AS siguiente FROM factura";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("siguiente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public boolean guardarFactura(factura fac, List<factura_detalle> detalles) {
        String sql = "INSERT INTO factura (id_factura, no_fact, serie, id_cliente, id_empleado, fecha_fac) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connFactory.getConection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, fac.getId_factura());
                ps.setInt(2, fac.getNo_factura());
                ps.setString(3, fac.getSerie());
                ps.setInt(4, fac.getId_cliente());
                ps.setInt(5, fac.getId_empleado());
                ps.setDate(6, java.sql.Date.valueOf(fac.getFecha_factura()));
                ps.executeUpdate();
            }
            if (!detalleDao.guardarDetalles(conn, fac.getId_factura(), detalles)) {
                conn.rollback();
                return false;
            }
            conn.commit();
            return true;
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
