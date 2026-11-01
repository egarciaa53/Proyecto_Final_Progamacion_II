/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.factura_detalle;
import dao.Factura_detalleDao;
import java.util.List;

/**
 *
 * @author edson
 */
public class ControllerFactura_detalle {
    private final Factura_detalleDao dao = new Factura_detalleDao();

    public List<factura_detalle> obtenerDetalles(int idFactura) {
        return dao.obtenerPorFactura(idFactura);
    }
}
