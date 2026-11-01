/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author edson
 */
public class proveedor {
    private int id_proveedor;
    private String empresa;
    private String direccion;
    private int telefono;
    private String nit;
    private String ciudad;
    private String pais;
    private String contacto;
    private String email;
    private int telefono_contacto;
    private boolean estatus;
    
    public proveedor() {
    }

    public proveedor(int id_proveedor, String empresa, String direccion, int telefono, String nit, String ciudad, String pais, String contacto, String email, int telefono_contacto, boolean estatus) {
        this.id_proveedor = id_proveedor;
        this.empresa = empresa;
        this.direccion = direccion;
        this.telefono = telefono;
        this.nit = nit;
        this.ciudad = ciudad;
        this.pais = pais;
        this.contacto = contacto;
        this.email = email;
        this.telefono_contacto = telefono_contacto;
        this.estatus = estatus;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelefono_contacto() {
        return telefono_contacto;
    }

    public void setTelefono_contacto(int telefono_contacto) {
        this.telefono_contacto = telefono_contacto;
    }

    public boolean isEstatus() {
        return estatus;
    }

    public void setEstatus(boolean estatus) {
        this.estatus = estatus;
    }
    
    
}
