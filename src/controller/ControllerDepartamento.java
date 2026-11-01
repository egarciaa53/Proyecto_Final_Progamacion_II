/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.List;

import model.departamento;
import dao.DepartamentoDao;


/**
 *
 * @author edson
 */
public class ControllerDepartamento {
    private final DepartamentoDao dao = new DepartamentoDao();
    
    public List<departamento> obtenerDepartamentos(){
        return dao.obtenerTodos();
    }
    public boolean guardarDepartamento(departamento depto){
        return dao.guardar(depto);
    }
    public boolean actualizarDepartamento(departamento depto){
        return dao.actualizar(depto);        
    }
    public boolean eliminarDepartamento(departamento depto){
        return dao.eliminar(depto.getId_departamento());
    }
    public boolean eliminarDepartamento(int idDepartamento){
        return dao.eliminar(idDepartamento);
    }
}


