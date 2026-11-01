/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.ClienteDao;
import model.cliente;
import java.util.List;


/**
 *
 * @author edson
 */
public class ControllerCliente {
    private final ClienteDao dao = new ClienteDao();

    public List<cliente> obtenerClientes() {
        return dao.obtenerTodos();
    }

    public boolean guardarCliente(cliente cl) {
        return dao.guardar(cl);
    }

    public boolean actualizarCliente(cliente cl) {
        return dao.actualizar(cl);
    }

    public boolean eliminarCliente(int idCliente) {
        return dao.eliminar(idCliente);
    }
}
