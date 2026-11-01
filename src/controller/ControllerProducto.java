/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.ProductoDao;
import model.producto;
import java.util.List;

/**
 *
 * @author edson
 */
public class ControllerProducto {
    private final ProductoDao dao = new ProductoDao();

    public List<producto> obtenerProductos() {
        return dao.obtenerTodos();
    }

    public boolean guardarProducto(producto prod) {
        return dao.guardar(prod);
    }

    public boolean actualizarProducto(producto prod) {
        return dao.actualizar(prod);
    }

    public boolean eliminarProducto(int idProducto) {
        return dao.eliminar(idProducto);
    }  
}
