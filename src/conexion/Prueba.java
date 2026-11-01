/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

import java.sql.SQLException;

/**
 *
 * @author edson
 */
public class Prueba {
    public static void main(String []args) throws SQLException{
        CreateConection conexion = new CreateConection();
        conexion.getConection();
    }
}