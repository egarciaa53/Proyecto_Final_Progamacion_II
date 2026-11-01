/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.ProveedorDao;
import model.proveedor;
import java.util.List;

/**
 *
 * @author edson
 */
public class ControllerProveedor {
    private final ProveedorDao dao = new ProveedorDao();

    public List<proveedor> obtenerProveedores() {
        return dao.obtenerTodos();
    }

    public boolean guardarProveedor(proveedor prov) {
        return dao.guardar(prov);
    }

    public boolean actualizarProveedor(proveedor prov) {
        return dao.actualizar(prov);
    }

    public boolean eliminarProveedor(int idProveedor) {
        return dao.eliminar(idProveedor);
    }
}
