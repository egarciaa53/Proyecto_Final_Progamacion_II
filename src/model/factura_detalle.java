/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author edson
 */
public class factura_detalle {
    private int id_factura;
    private int id_linea;
    private int id_producto;
    private int cantidad;

    public factura_detalle() {
    }

    public factura_detalle(int id_factura, int id_linea, int id_producto, int cantidad) {
        this.id_factura = id_factura;
        this.id_linea = id_linea;
        this.id_producto = id_producto;
        this.cantidad = cantidad;
    }

    public int getId_factura() {
        return id_factura;
    }

    public void setId_factura(int id_factura) {
        this.id_factura = id_factura;
    }

    public int getId_linea() {
        return id_linea;
    }

    public void setId_linea(int id_linea) {
        this.id_linea = id_linea;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    
    
}
