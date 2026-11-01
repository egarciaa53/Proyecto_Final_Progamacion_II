/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author edson
 */
public class producto {
    private int id_producto;
    private String descripcion;
    private int stock;
    private int stock_minimo;
    private double precio_unitario;
    private int id_proveedor;

    public producto() {
    }

    public producto(int id_producto, String descripcion, int stock, int stock_minimo, double precio_unitario, int id_proveedor) {
        this.id_producto = id_producto;
        this.descripcion = descripcion;
        this.stock = stock;
        this.stock_minimo = stock_minimo;
        this.precio_unitario = precio_unitario;
        this.id_proveedor = id_proveedor;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStock_minimo() {
        return stock_minimo;
    }

    public void setStock_minimo(int stock_minimo) {
        this.stock_minimo = stock_minimo;
    }

    public double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }
    
    
    
    
    
    
}
