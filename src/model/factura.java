/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author edson
 */
public class factura {
    private int id_factura;
    private int no_factura;
    private String serie;
    private int id_cliente;
    private int id_empleado;
    private String fecha_factura;
    
    public factura() {
    }

    public factura(int id_factura, int no_factura, String serie, int id_cliente, int id_empleado, String fecha_factura) {
        this.id_factura = id_factura;
        this.no_factura = no_factura;
        this.serie = serie;
        this.id_cliente = id_cliente;
        this.id_empleado = id_empleado;
        this.fecha_factura = fecha_factura;
    }

    public int getId_factura() {
        return id_factura;
    }

    public void setId_factura(int id_factura) {
        this.id_factura = id_factura;
    }

    public int getNo_factura() {
        return no_factura;
    }

    public void setNo_factura(int no_factura) {
        this.no_factura = no_factura;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getFecha_factura() {
        return fecha_factura;
    }

    public void setFecha_factura(String fecha_factura) {
        this.fecha_factura = fecha_factura;
    }
    
    
    
}
