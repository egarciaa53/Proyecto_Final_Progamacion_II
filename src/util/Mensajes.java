/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import java.awt.Component;
import javax.swing.JOptionPane;
/**
 *
 * @author edson
 */

// lanza mensajes en dado caso el usuario ingresó algo o hizo algo que no debe

public class Mensajes {
    private Mensajes() { }
    public static void info(Component p, String m)  { JOptionPane.showMessageDialog(p, m, "Información", JOptionPane.INFORMATION_MESSAGE); }
    public static void aviso(Component p, String m) { JOptionPane.showMessageDialog(p, m, "Atención", JOptionPane.WARNING_MESSAGE); }
    public static void error(Component p, String m) { JOptionPane.showMessageDialog(p, m, "Error", JOptionPane.ERROR_MESSAGE); }
    public static boolean confirmar(Component p, String m) {
        return JOptionPane.showConfirmDialog(p, m, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
