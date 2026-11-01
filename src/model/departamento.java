/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author edson
 */
public class departamento {
    private int id_departamento;
    private String descripcion;

    public departamento() {
    }

    public departamento(int id_departamento, String descripcion) {
        this.id_departamento = id_departamento;
        this.descripcion = descripcion;
    }

    public int getId_departamento() {
        return id_departamento;
    }

    public void setId_departamento(int id_departamento) {
        this.id_departamento = id_departamento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void saludo(){
        System.out.println("hola");
    }
    
    
}
