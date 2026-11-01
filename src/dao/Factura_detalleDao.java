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
import model.factura_detalle;


/**
 *
 * @author edson
 */
public class Factura_detalleDao {
    private final CreateConection connFactory = new CreateConection();
    public List<factura_detalle> obtenerPorFactura(int idFactura) {
        List<factura_detalle> lista = new ArrayList<>();
        String sql = "SELECT id_factura, id_linea, id_producto, cantidad FROM factura_detalle "
                + "WHERE id_factura = ? ORDER BY id_linea";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new factura_detalle(rs.getInt("id_factura"), rs.getInt("id_linea"),
                            rs.getInt("id_producto"), rs.getInt("cantidad")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public boolean guardarDetalles(Connection conn, int idFactura, List<factura_detalle> detalles)
            throws SQLException {
        String sql = "INSERT INTO factura_detalle (id_factura, id_linea, id_producto, cantidad) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (factura_detalle detalle : detalles) {
                ps.setInt(1, idFactura);
                ps.setInt(2, detalle.getId_linea());
                ps.setInt(3, detalle.getId_producto());
                ps.setInt(4, detalle.getCantidad());
                ps.addBatch();
            }
            int[] resultado = ps.executeBatch();
            return resultado.length == detalles.size();
        }
    }
}
