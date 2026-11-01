/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.UsuarioDao;
import model.usuario;
import java.util.List;


/**
 *
 * @author edson
 */
public class ControllerUsuario {
    private final UsuarioDao dao = new UsuarioDao();

    public List<usuario> obtenerUsuarios() {
        return dao.obtenerTodos();
    }

    public boolean guardarUsuario(usuario user) {
        return dao.guardar(user);
    }

    public boolean actualizarUsuario(usuario user, String usuarioAnterior, String contrasenaAnterior) {
        return dao.actualizar(user, usuarioAnterior, contrasenaAnterior);
    }

    public boolean eliminarUsuario(String nombreUsuario, String contrasena) {
        return dao.eliminar(nombreUsuario, contrasena);
    }

    public usuario validarLogin(String nombreUsuario, String contrasena) {
        return dao.validarLogin(nombreUsuario, contrasena);
    }
}
