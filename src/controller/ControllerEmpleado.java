/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.empleado;
import dao.EmpleadoDao;
import java.util.List;

/**
 *
 * @author edson
 */
public class ControllerEmpleado {
    private final EmpleadoDao dao = new EmpleadoDao();

    public List<empleado> obtenerEmpleados() {
        return dao.obtenerTodos();
    }

    public boolean guardarEmpleado(empleado emp) {
        return dao.guardar(emp);
    }

    public boolean actualizarEmpleado(empleado emp) {
        return dao.actualizar(emp);
    }

    public boolean eliminarEmpleado(int idEmpleado) {
        return dao.eliminar(idEmpleado);
    }
}
