/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import conexion.CreateConection;
import model.departamento;

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
public class DepartamentoDao {
    private final CreateConection connFactory = new CreateConection();

    public List<departamento> obtenerTodos(){
        List<departamento> lista = new ArrayList<>();
        String sql = "SELECT id_departamento, descripcion FROM departamento ORDER BY id_departamento";
        try (Connection conn = connFactory.getConection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    lista.add(new departamento(rs.getInt("id_departamento"), rs.getString("descripcion")));
                } 
        }catch (SQLException e) {e.printStackTrace();}
        return lista;
    }
    public boolean guardar(departamento depto) {
        String sql = "INSERT INTO departamento (id_departamento, descripcion) VALUES (?, ?)";
        try (Connection conn = connFactory.getConection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, depto.getId_departamento());
            ps.setString(2, depto.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
     public boolean actualizar(departamento depto) {
        String sql = "UPDATE departamento SET descripcion = ? WHERE id_departamento = ?";
        try (Connection conn = connFactory.getConection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, depto.getDescripcion());
            ps.setInt(2, depto.getId_departamento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false;
        }
    }
    public boolean eliminar(int idDepartamento) {
        String sql = "DELETE FROM departamento WHERE id_departamento = ?";
        try (Connection conn = connFactory.getConection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDepartamento);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false;
        }
    }
}
