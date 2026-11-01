/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author edson
 */
public class usuario {
    private int id_usuario;
    private String usuario;
    private String contrasena;
    private String fecha_ingreso;
    private String fecha_ultima_modificacion;
    private int id_empleado;
    private boolean estatus;

    public usuario() {
    }

    public usuario(int id_usuario, String usuario, String contraseña, String fecha_ingreso, String fecha_ultima_modificacion, int id_empleado, boolean estatus) {
        this.id_usuario = id_usuario;
        this.usuario = usuario;
        this.contrasena = contraseña;
        this.fecha_ingreso = fecha_ingreso;
        this.fecha_ultima_modificacion = fecha_ultima_modificacion;
        this.id_empleado = id_empleado;
        this.estatus = estatus;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseña() {
        return contrasena;
    }

    public void setContraseña(String contraseña) {
        this.contrasena = contraseña;
    }

    public String getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(String fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }

    public String getFecha_ultima_modificacion() {
        return fecha_ultima_modificacion;
    }

    public void setFecha_ultima_modificacion(String fecha_ultima_modificacion) {
        this.fecha_ultima_modificacion = fecha_ultima_modificacion;
    }

    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public boolean isEstatus() {
        return estatus;
    }

    public void setEstatus(boolean estatus) {
        this.estatus = estatus;
    }
    
    
}
