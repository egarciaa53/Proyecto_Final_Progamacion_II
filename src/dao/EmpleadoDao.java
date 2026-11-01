/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import conexion.CreateConection;
import model.empleado;

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
public class EmpleadoDao {
    private final CreateConection connFactory = new CreateConection();
    
    public List<empleado> obtenerTodos() {
        List<empleado> lista = new ArrayList<>();
        String sql = "SELECT id_empleado, primer_nombre, segundo_nombre, primer_apellido, "
                + "segundo_apellido, nit, salario, estatus, id_departamento "
                + "FROM empleado ORDER BY id_empleado";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new empleado(rs.getInt("id_empleado"),
                        rs.getString("primer_nombre"), rs.getString("segundo_nombre"),
                        rs.getString("primer_apellido"), rs.getString("segundo_apellido"),
                        rs.getString("nit"), rs.getDouble("salario"),
                        rs.getInt("estatus") == 1, rs.getInt("id_departamento")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean guardar(empleado emp) {
        String sql = "INSERT INTO empleado (id_empleado, primer_nombre, segundo_nombre, "
                + "primer_apellido, segundo_apellido, nit, salario, estatus, id_departamento) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, emp.getId_empleado());
            ps.setString(2, emp.getPrimer_nombre());
            ps.setString(3, emp.getSegundo_nombre());
            ps.setString(4, emp.getPrimer_apellido());
            ps.setString(5, emp.getSegundo_apellido());
            ps.setString(6, emp.getNit());
            ps.setDouble(7, emp.getSalario());
            ps.setInt(8, emp.isEstatus() ? 1 : 0);
            ps.setInt(9, emp.getId_departamento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean actualizar(empleado emp) {
        String sql = "UPDATE empleado SET primer_nombre = ?, segundo_nombre = ?, "
                + "primer_apellido = ?, segundo_apellido = ?, nit = ?, salario = ?, "
                + "estatus = ?, id_departamento = ? WHERE id_empleado = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getPrimer_nombre());
            ps.setString(2, emp.getSegundo_nombre());
            ps.setString(3, emp.getPrimer_apellido());
            ps.setString(4, emp.getSegundo_apellido());
            ps.setString(5, emp.getNit());
            ps.setDouble(6, emp.getSalario());
            ps.setInt(7, emp.isEstatus() ? 1 : 0);
            ps.setInt(8, emp.getId_departamento());
            ps.setInt(9, emp.getId_empleado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminar(int idEmpleado) {
        String sql = "DELETE FROM empleado WHERE id_empleado = ?";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
