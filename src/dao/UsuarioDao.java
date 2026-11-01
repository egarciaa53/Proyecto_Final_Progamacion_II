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
import model.usuario;

/**
 *
 * @author edson
 */
public class UsuarioDao {
    private final CreateConection connFactory = new CreateConection();
    
    public List<usuario> obtenerTodos() {
        List<usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, usuario, contrasena, fecha_ingreso, "
                + "fecha_ultima_modificacion, id_empleado, estatus FROM usuario ORDER BY id_usuario";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new usuario(rs.getInt("id_usuario"), rs.getString("usuario"),
                        rs.getString("contrasena"), rs.getString("fecha_ingreso"),
                        rs.getString("fecha_ultima_modificacion"), rs.getInt("id_empleado"),
                        rs.getInt("estatus") == 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public boolean guardar(usuario user) {
        String sql = "INSERT INTO usuario (id_usuario, usuario, contrasena, fecha_ingreso, "
                + "fecha_ultima_modificacion, id_empleado, estatus) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getId_usuario());
            ps.setString(2, user.getUsuario());
            ps.setString(3, user.getContraseña());
            ps.setDate(4, java.sql.Date.valueOf(user.getFecha_ingreso()));
            ps.setDate(5, java.sql.Date.valueOf(user.getFecha_ultima_modificacion()));
            ps.setInt(6, user.getId_empleado());
            ps.setInt(7, user.isEstatus() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean actualizar(usuario user, String usuarioAnterior, String contrasenaAnterior) {
        String sql = "UPDATE usuario SET id_usuario = ?, usuario = ?, contrasena = ?, "
                + "fecha_ingreso = ?, fecha_ultima_modificacion = ?, id_empleado = ?, estatus = ? "
                + "WHERE usuario = ? AND contrasena = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getId_usuario());
            ps.setString(2, user.getUsuario());
            ps.setString(3, user.getContraseña());
            ps.setDate(4, java.sql.Date.valueOf(user.getFecha_ingreso()));
            ps.setDate(5, java.sql.Date.valueOf(user.getFecha_ultima_modificacion()));
            ps.setInt(6, user.getId_empleado());
            ps.setInt(7, user.isEstatus() ? 1 : 0);
            ps.setString(8, usuarioAnterior);
            ps.setString(9, contrasenaAnterior);
            return ps.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminar(String nombreUsuario, String contrasena) {
        String sql = "DELETE FROM usuario WHERE usuario = ? AND contrasena = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public usuario validarLogin(String nombreUsuario, String contrasena) {
        String sql = "SELECT id_usuario, usuario, contrasena, fecha_ingreso, "
                + "fecha_ultima_modificacion, id_empleado, estatus FROM usuario "
                + "WHERE usuario = ? AND contrasena = ? AND estatus = 1";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new usuario(rs.getInt("id_usuario"), rs.getString("usuario"),
                            rs.getString("contrasena"), rs.getString("fecha_ingreso"),
                            rs.getString("fecha_ultima_modificacion"), rs.getInt("id_empleado"),
                            rs.getInt("estatus") == 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
