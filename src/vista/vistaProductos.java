/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;
import controller.ControllerProducto;
import controller.ControllerProveedor;
import java.awt.Graphics;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.producto;
import model.proveedor;
import util.ItemCombo;
import util.Mensajes;

/**
 *
 * @author edson
 */
public class vistaProductos extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(vistaProductos.class.getName());
    
    private static final int MAX_DESCRIPCION = 100;

    private final ControllerProducto controlador = new ControllerProducto();
    private final ControllerProveedor controladorProveedor = new ControllerProveedor();
    private List<producto> listaProductos = new ArrayList<>();
    private List<proveedor> listaProveedores = new ArrayList<>();
    
    FondoPanel fondo = new FondoPanel();    
    /**
     * Creates new form vistaProductos
     */
    public vistaProductos() {
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        txtIdProducto.setEditable(false);
        tblProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarEnFormulario();
        });
        cargarCombos();
        cargarTabla("");
        limpiarFormulario();
    }
    private void cargarCombos() {
        listaProveedores = controladorProveedor.obtenerProveedores();
        cmbIdProveedor.removeAllItems();
        for (proveedor p : listaProveedores) {
            String texto = Objects.toString(p.getEmpresa(), "") + (p.isEstatus() ? "" : " (Inactivo)");
            cmbIdProveedor.addItem(new ItemCombo(p.getId_proveedor(), texto));
        }
    }
    private String formatearPrecio(double precio) {
        return BigDecimal.valueOf(precio).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private void cargarTabla(String filtro) {
        listaProductos = controlador.obtenerProductos();
        DefaultTableModel modelo = (DefaultTableModel) tblProductos.getModel();
        modelo.setRowCount(0);
        String f = filtro.trim().toLowerCase();
        for (producto p : listaProductos) {
            String texto = (p.getId_producto() + " "
                    + Objects.toString(p.getDescripcion(), "")).toLowerCase();
            if (f.isEmpty() || texto.contains(f)) {
                modelo.addRow(new Object[]{
                    String.valueOf(p.getId_producto()),
                    Objects.toString(p.getDescripcion(), ""),
                    String.valueOf(p.getStock()),
                    String.valueOf(p.getStock_minimo()),
                    formatearPrecio(p.getPrecio_unitario()),
                    String.valueOf(p.getId_proveedor())
                });
            }
        }
    }
    private int siguienteId() {
        int max = 0;
        for (producto p : listaProductos) max = Math.max(max, p.getId_producto());
        return max + 1;
    }

    private void limpiarFormulario() {
        tblProductos.clearSelection();
        txtIdProducto.setText(String.valueOf(siguienteId()));
        txtDescripcion.setText("");
        txtStock.setText("");
        txtStockMinimo.setText("");
        txtPrecioUnitario.setText("");
        if (cmbIdProveedor.getItemCount() > 0) cmbIdProveedor.setSelectedIndex(0);
        txtDescripcion.requestFocus();
    }
    private boolean esEnteroNoNegativo(javax.swing.JTextField campo, String nombreCampo) {
    try {
        if (Integer.parseInt(campo.getText().trim()) >= 0) return true;
    } catch (NumberFormatException ex) {
    }
    Mensajes.aviso(this, nombreCampo + " debe ser un número entero mayor o igual a 0.");
    campo.requestFocus();
    return false;
    }

    private boolean validarCampos() {
        String descripcion = txtDescripcion.getText().trim();
        if (descripcion.isEmpty()) {
            Mensajes.aviso(this, "La descripción es obligatoria.");
            txtDescripcion.requestFocus(); return false;
        }
        if (descripcion.length() > MAX_DESCRIPCION) {
            Mensajes.aviso(this, "La descripción admite máximo " + MAX_DESCRIPCION + " caracteres.");
            txtDescripcion.requestFocus(); return false;
        }

        if (!esEnteroNoNegativo(txtStock, "El stock")) return false;
        if (!esEnteroNoNegativo(txtStockMinimo, "El stock mínimo")) return false;

        try {
            BigDecimal precio = new BigDecimal(txtPrecioUnitario.getText().trim());
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                Mensajes.aviso(this, "El precio debe ser mayor que cero.");
                txtPrecioUnitario.requestFocus(); return false;
            }
            if (precio.stripTrailingZeros().scale() > 2) {
                Mensajes.aviso(this, "El precio admite máximo 2 decimales.");
                txtPrecioUnitario.requestFocus(); return false;
            }
        } catch (NumberFormatException ex) {
            Mensajes.aviso(this, "El precio debe ser un número válido (ejemplo: 25.50).");
            txtPrecioUnitario.requestFocus(); return false;
        }

        if (cmbIdProveedor.getSelectedItem() == null) {
            Mensajes.aviso(this, "Seleccione un proveedor. Si la lista está vacía, registre primero un proveedor.");
            return false;
        }
        return true;
    }
    private producto leerFormulario() {
        ItemCombo proveedorSeleccionado = (ItemCombo) cmbIdProveedor.getSelectedItem();
        return new producto(
            Integer.parseInt(txtIdProducto.getText()),
            txtDescripcion.getText().trim(),
            Integer.parseInt(txtStock.getText().trim()),
            Integer.parseInt(txtStockMinimo.getText().trim()),
            new BigDecimal(txtPrecioUnitario.getText().trim()).doubleValue(),
            proveedorSeleccionado.getId());
    }
    private void mostrarEnFormulario() {
        int fila = tblProductos.getSelectedRow();
        if (fila < 0) return;
        int id = Integer.parseInt(tblProductos.getValueAt(fila, 0).toString());
        for (producto p : listaProductos) {
            if (p.getId_producto() == id) {
                txtIdProducto.setText(String.valueOf(p.getId_producto()));
                txtDescripcion.setText(Objects.toString(p.getDescripcion(), ""));
                txtStock.setText(String.valueOf(p.getStock()));
                txtStockMinimo.setText(String.valueOf(p.getStock_minimo()));
                txtPrecioUnitario.setText(formatearPrecio(p.getPrecio_unitario()));
                ItemCombo.seleccionar(cmbIdProveedor, p.getId_proveedor());
                return;
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlGeneral = new javax.swing.JPanel();
        pnlFormulario = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtIdProducto = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        txtStock = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtStockMinimo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPrecioUnitario = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbIdProveedor = new javax.swing.JComboBox<>();
        pnlBotones = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        pnlBusqueda = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        scpTabla = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jPanel1 = new FondoPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Productos");

        pnlGeneral.setBackground(new java.awt.Color(255, 255, 255));

        pnlFormulario.setBackground(new java.awt.Color(247, 247, 247));
        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial Narrow", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(34, 19, 83));
        jLabel1.setText("Información del Producto");

        txtIdProducto.setEditable(false);
        txtIdProducto.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel2.setText("ID Producto:");

        jLabel3.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel3.setText("Descipción:");

        txtDescripcion.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        txtDescripcion.addActionListener(this::txtDescripcionActionPerformed);

        txtStock.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        txtStock.addActionListener(this::txtStockActionPerformed);

        jLabel4.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel4.setText("Stock:");

        txtStockMinimo.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        jLabel5.setText("Stock Mínimo:");

        txtPrecioUnitario.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        jLabel6.setText("Precio Unitario:");

        jLabel7.setText("ID Proveedor:");

        cmbIdProveedor.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel1)
                .addContainerGap(31, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFormularioLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5)
                        .addComponent(jLabel3)
                        .addComponent(jLabel2))
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(12, 12, 12)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtStock, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtStockMinimo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtPrecioUnitario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmbIdProveedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtIdProducto))
                .addContainerGap())
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(15, 15, 15)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtPrecioUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cmbIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pnlBotones.setBackground(new java.awt.Color(247, 247, 247));
        pnlBotones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Opciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        btnNuevo.setBackground(new java.awt.Color(247, 247, 247));
        btnNuevo.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnNuevo.setText("NUEVO");
        btnNuevo.addActionListener(this::btnNuevoActionPerformed);

        btnGuardar.setBackground(new java.awt.Color(247, 247, 247));
        btnGuardar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnGuardar.setText("GUARDAR");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnModificar.setBackground(new java.awt.Color(247, 247, 247));
        btnModificar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnModificar.setText("MODIFICAR");
        btnModificar.addActionListener(this::btnModificarActionPerformed);

        btnEliminar.setBackground(new java.awt.Color(247, 247, 247));
        btnEliminar.setText("ELIMINAR");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        javax.swing.GroupLayout pnlBotonesLayout = new javax.swing.GroupLayout(pnlBotones);
        pnlBotones.setLayout(pnlBotonesLayout);
        pnlBotonesLayout.setHorizontalGroup(
            pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlBotonesLayout.setVerticalGroup(
            pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotonesLayout.createSequentialGroup()
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevo)
                    .addComponent(btnGuardar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModificar)
                    .addComponent(btnEliminar))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pnlBusqueda.setBackground(new java.awt.Color(247, 247, 247));
        pnlBusqueda.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de los Productos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(34, 19, 83));
        jLabel9.setText("PRODUCTOS REGISTRADOS");

        tblProductos.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Producto", "Descripción", "Stock", "Stock Minimo", "Precio Unitario", "ID Proveedor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductos.setGridColor(new java.awt.Color(247, 247, 247));
        tblProductos.setSelectionBackground(new java.awt.Color(247, 247, 247));
        tblProductos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scpTabla.setViewportView(tblProductos);

        jLabel8.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel8.setText("Buscar.");

        txtBuscar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        txtBuscar.addActionListener(this::txtBuscarActionPerformed);

        btnBuscar.setBackground(new java.awt.Color(247, 247, 247));
        btnBuscar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);

        javax.swing.GroupLayout pnlBusquedaLayout = new javax.swing.GroupLayout(pnlBusqueda);
        pnlBusqueda.setLayout(pnlBusquedaLayout);
        pnlBusquedaLayout.setHorizontalGroup(
            pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBusquedaLayout.createSequentialGroup()
                .addGroup(pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBusquedaLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlBusquedaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar))
                    .addComponent(scpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlBusquedaLayout.setVerticalGroup(
            pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBusquedaLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scpTabla)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 146, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtDescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescripcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescripcionActionPerformed

    private void txtStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
        cargarTabla(txtBuscar.getText());
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        if (!validarCampos()) return;
        if (controlador.guardarProducto(leerFormulario())) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Producto guardado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo guardar el producto.");
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        if (tblProductos.getSelectedRow() < 0) {
            Mensajes.aviso(this, "Seleccione un producto de la tabla."); return;
        }
        if (!validarCampos()) return;
        if (controlador.actualizarProducto(leerFormulario())) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Producto actualizado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo actualizar el producto.");
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if (tblProductos.getSelectedRow() < 0) {
            Mensajes.aviso(this, "Seleccione un producto de la tabla."); return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar el producto seleccionado?")) return;
        if (controlador.eliminarProducto(Integer.parseInt(txtIdProducto.getText()))) {
            cargarTabla(""); limpiarFormulario();
        } else {
            Mensajes.error(this, "No se pudo eliminar. Es posible que el producto esté incluido en facturas.");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        cargarTabla(txtBuscar.getText());
    }//GEN-LAST:event_btnBuscarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new vistaProductos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<util.ItemCombo> cmbIdProveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlBusqueda;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JScrollPane scpTabla;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtIdProducto;
    private javax.swing.JTextField txtPrecioUnitario;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtStockMinimo;
    // End of variables declaration//GEN-END:variables
    class FondoPanel extends JPanel{
            private Image imagen;

            @Override
            public void paint(Graphics g){
                imagen = new ImageIcon(getClass().getResource("/images/AUTOPARTES.png")).getImage();
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);

                setOpaque(false);

                super.paint(g);
            }
        }
}
