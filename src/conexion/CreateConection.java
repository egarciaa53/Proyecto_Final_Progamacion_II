/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author edson
 */

public class CreateConection {
    static Properties config = new Properties();
    String hostname = null;
    String port = null;
    String database = null;
    String username = null;
    String password = null;
    
    public CreateConection() {
        try (InputStream in = getClass().getResourceAsStream("/conexion/db_config.properties")) {
            if (in == null) {
                System.out.println("No se encontró el archivo db_config.properties");
                return;
            }
            config.load(in);
            loadProperties();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void loadProperties(){
        hostname = config.getProperty("hostname");
        port = config.getProperty("port");
        database = config.getProperty("database");
        username = config.getProperty("username");
        password = config.getProperty("password");
    }
    public Connection getConection()throws SQLException{
        Connection conn = null;
        try {
            String jdbcUrl = "jdbc:postgresql://" + this.hostname+":"+this.port + "/" + this.database;
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Conexion establecida");
            return conn;
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No se pudo conectar a la base de datos");
        }
        return null;
    }
}


