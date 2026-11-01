/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author edson
 */

// nota, esta es una clase personalizada que se usa para reemplazar el contenido de un combobox, 
// reemplaza el item1, item2 y los demás, se referencia a sí misma con el contenido de una tabla de sql
// 

public class ItemCombo {
    private final int id;
    private final String texto;
    public ItemCombo(int id, String texto) { this.id = id; this.texto = texto; }
    public int getId() { return id; }
    @Override public String toString() { return texto; }
    
    public static void seleccionar(javax.swing.JComboBox<ItemCombo> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) { combo.setSelectedIndex(i); return; }
        }
        combo.setSelectedIndex(-1);
    }
}
