/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.FacturaDao;
import model.factura;
import model.factura_detalle;
import java.util.List;


/**
 *
 * @author edson
 */
public class ControllerFactura {
    private final FacturaDao dao = new FacturaDao();

    public List<factura> obtenerFacturas() {
        return dao.obtenerTodos();
    }

    public int obtenerSiguienteId() {
        return dao.obtenerSiguienteId();
    }

    public boolean guardarFactura(factura fac, List<factura_detalle> detalles) {
        return dao.guardarFactura(fac, detalles);
    }
}
