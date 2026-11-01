/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controller.ControllerProveedor;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.proveedor;
import util.Mensajes;
/**
 *
 * @author edson
 */
public class vistaProveedores extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(vistaProveedores.class.getName());
    private static final int MAX_TEXTO = 100;
    private static final int MAX_NIT = 30;
    private static final String PATRON_TELEFONO = "\\d{1,9}";
    FondoPanel fondo = new FondoPanel();  

    private final ControllerProveedor controlador = new ControllerProveedor();
    private List<proveedor> listaProveedores = new ArrayList<>();
    /**
     * Creates new form vistaProveedores
     */
    public vistaProveedores() {
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        txtIdProveedor.setEditable(false);
        tblProveedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarEnFormulario();
        });
        cargarTabla("");
        limpiarFormulario();
    }
    private void cargarTabla(String filtro) {
        listaProveedores = controlador.obtenerProveedores();
        DefaultTableModel modelo = (DefaultTableModel) tblProveedores.getModel();
        modelo.setRowCount(0);
        String f = filtro.trim().toLowerCase();
        for (proveedor p : listaProveedores) {
            String texto = (Objects.toString(p.getEmpresa(), "") + " "
                    + Objects.toString(p.getNit(), "") + " "
                    + Objects.toString(p.getCiudad(), "") + " "
                    + Objects.toString(p.getPais(), "") + " "
                    + Objects.toString(p.getContacto(), "") + " "
                    + Objects.toString(p.getEmail(), "")).toLowerCase();
            if (f.isEmpty() || texto.contains(f)) {
                modelo.addRow(new Object[]{
                    String.valueOf(p.getId_proveedor()),
                    Objects.toString(p.getEmpresa(), ""),
                    Objects.toString(p.getDireccion(), ""),
                    String.valueOf(p.getTelefono()),
                    Objects.toString(p.getNit(), ""),
                    Objects.toString(p.getCiudad(), ""),
                    Objects.toString(p.getPais(), ""),
                    Objects.toString(p.getContacto(), ""),
                    Objects.toString(p.getEmail(), ""),
                    String.valueOf(p.getTelefono_contacto()),
                    p.isEstatus() ? "Activo" : "Inactivo"
                });
            }
        }
    }
    private int siguienteId() {
        int max = 0;
        for (proveedor p : listaProveedores) max = Math.max(max, p.getId_proveedor());
        return max + 1;
    }

    private void limpiarFormulario() {
        tblProveedores.clearSelection();
        txtIdProveedor.setText(String.valueOf(siguienteId()));
        txtEmpresa.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtNit.setText("");
        txtCiudad.setText("");
        txtPais.setText("");
        txtContacto.setText("");
        txtEmail.setText("");
        txtTelefonoContacto.setText("");
        chkEstatus.setSelected(true);
        txtEmpresa.requestFocus();
    }
    private boolean excede(javax.swing.JTextField campo, int max, String nombreCampo) {
        if (campo.getText().trim().length() > max) {
            Mensajes.aviso(this, nombreCampo + " admite máximo " + max + " caracteres.");
            campo.requestFocus();
            return true;
        }
        return false;
    }

    private boolean validarCampos() {
        if (txtEmpresa.getText().trim().isEmpty()) {
            Mensajes.aviso(this, "El nombre de la empresa es obligatorio.");
            txtEmpresa.requestFocus(); return false;
        }
        if (txtNit.getText().trim().isEmpty()) {
            Mensajes.aviso(this, "El NIT es obligatorio.");
            txtNit.requestFocus(); return false;
        }

        if (excede(txtEmpresa, MAX_TEXTO, "La empresa")
                || excede(txtDireccion, MAX_TEXTO, "La dirección")
                || excede(txtNit, MAX_NIT, "El NIT")
                || excede(txtCiudad, MAX_TEXTO, "La ciudad")
                || excede(txtPais, MAX_TEXTO, "El país")
                || excede(txtContacto, MAX_TEXTO, "El contacto")
                || excede(txtEmail, MAX_TEXTO, "El email")) {
            return false;
        }

        if (!txtTelefono.getText().trim().matches(PATRON_TELEFONO)) {
            Mensajes.aviso(this, "El teléfono es obligatorio y debe tener solo dígitos (máximo 9).");
            txtTelefono.requestFocus(); return false;
        }
        if (!txtTelefonoContacto.getText().trim().matches(PATRON_TELEFONO)) {
            Mensajes.aviso(this, "El teléfono de contacto es obligatorio y debe tener solo dígitos (máximo 9).");
            txtTelefonoContacto.requestFocus(); return false;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            Mensajes.aviso(this, "El email no tiene un formato válido (ejemplo: ventas@empresa.com).");
            txtEmail.requestFocus(); return false;
        }
        return true;
    }  
    
    private proveedor leerFormulario() {
        return new proveedor(
            Integer.parseInt(txtIdProveedor.getText()),
            txtEmpresa.getText().trim(),
            txtDireccion.getText().trim(),
            Integer.parseInt(txtTelefono.getText().trim()),
            txtNit.getText().trim(),
            txtCiudad.getText().trim(),
            txtPais.getText().trim(),
            txtContacto.getText().trim(),
            txtEmail.getText().trim(),
            Integer.parseInt(txtTelefonoContacto.getText().trim()),
            chkEstatus.isSelected());
    }
    private void mostrarEnFormulario() {
        int fila = tblProveedores.getSelectedRow();
        if (fila < 0) return;
        int id = Integer.parseInt(tblProveedores.getValueAt(fila, 0).toString());
        for (proveedor p : listaProveedores) {
            if (p.getId_proveedor() == id) {
                txtIdProveedor.setText(String.valueOf(p.getId_proveedor()));
                txtEmpresa.setText(Objects.toString(p.getEmpresa(), ""));
                txtDireccion.setText(Objects.toString(p.getDireccion(), ""));
                txtTelefono.setText(String.valueOf(p.getTelefono()));
                txtNit.setText(Objects.toString(p.getNit(), ""));
                txtCiudad.setText(Objects.toString(p.getCiudad(), ""));
                txtPais.setText(Objects.toString(p.getPais(), ""));
                txtContacto.setText(Objects.toString(p.getContacto(), ""));
                txtEmail.setText(Objects.toString(p.getEmail(), ""));
                txtTelefonoContacto.setText(String.valueOf(p.getTelefono_contacto()));
                chkEstatus.setSelected(p.isEstatus());
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtIdProveedor = new javax.swing.JTextField();
        txtEmpresa = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtNit = new javax.swing.JTextField();
        txtCiudad = new javax.swing.JTextField();
        txtPais = new javax.swing.JTextField();
        txtContacto = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtTelefonoContacto = new javax.swing.JTextField();
        chkEstatus = new javax.swing.JCheckBox();
        pnlBotones = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        pnlBusqueda = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        scpTabla = new javax.swing.JScrollPane();
        tblProveedores = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jPanel1 = new FondoPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Proveedores");

        pnlGeneral.setBackground(new java.awt.Color(255, 255, 255));

        pnlFormulario.setBackground(new java.awt.Color(247, 247, 247));
        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial Narrow", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(34, 19, 83));
        jLabel2.setText("Datos del Proveedor");

        jLabel3.setText("ID Proveedor:");

        jLabel4.setText("Empresa:");

        jLabel5.setText("Dirección:");

        jLabel6.setText("Teléfono:");

        jLabel7.setText("NIT:");

        jLabel8.setText("Ciudad:");

        jLabel9.setText("País:");

        jLabel10.setText("Contacto:");

        jLabel11.setText("Email:");

        jLabel12.setText("Teléfono Contacto:");

        jLabel13.setText("Estatus:");

        txtIdProveedor.setEditable(false);
        txtIdProveedor.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        txtIdProveedor.addActionListener(this::txtIdProveedorActionPerformed);

        txtPais.addActionListener(this::txtPaisActionPerformed);

        chkEstatus.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        chkEstatus.setText("Activo");

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(chkEstatus)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlFormularioLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTelefonoContacto, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                            .addGroup(pnlFormularioLayout.createSequentialGroup()
                                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEmpresa)
                                    .addComponent(txtIdProveedor)
                                    .addComponent(txtDireccion)
                                    .addComponent(txtTelefono)
                                    .addComponent(txtNit)
                                    .addComponent(txtCiudad)
                                    .addComponent(txtPais)
                                    .addComponent(txtContacto)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFormularioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(44, 44, 44))
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(txtCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(txtContacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTelefonoContacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEstatus)
                    .addComponent(jLabel13)))
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
        btnEliminar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnEliminar.setText("ELIMINAR");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        javax.swing.GroupLayout pnlBotonesLayout = new javax.swing.GroupLayout(pnlBotones);
        pnlBotones.setLayout(pnlBotonesLayout);
        pnlBotonesLayout.setHorizontalGroup(
            pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotonesLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBusqueda.setBackground(new java.awt.Color(247, 247, 247));
        pnlBusqueda.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de los Proveedores", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(34, 19, 83));
        jLabel1.setText("REGISTRO DE PROVEEDORES");

        tblProveedores.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        tblProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Proveedor", "Empresa", "Dirección", "Teléfono", "NIT", "Ciudad", "País", "Contacto", "Email", "Tel-Contacto", "Estatus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProveedores.setGridColor(new java.awt.Color(247, 247, 247));
        tblProveedores.setSelectionBackground(new java.awt.Color(247, 247, 247));
        tblProveedores.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scpTabla.setViewportView(tblProveedores);

        jLabel14.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel14.setText("Buscar:");

        btnBuscar.setBackground(new java.awt.Color(247, 247, 247));
        btnBuscar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);

        javax.swing.GroupLayout pnlBusquedaLayout = new javax.swing.GroupLayout(pnlBusqueda);
        pnlBusqueda.setLayout(pnlBusquedaLayout);
        pnlBusquedaLayout.setHorizontalGroup(
            pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBusquedaLayout.createSequentialGroup()
                .addGroup(pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlBusquedaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scpTabla))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlBusquedaLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1)
                        .addGap(0, 592, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlBusquedaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar)))
                .addContainerGap())
        );
        pnlBusquedaLayout.setVerticalGroup(
            pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBusquedaLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scpTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 57, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlFormulario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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

    private void txtIdProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdProveedorActionPerformed

    private void txtPaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPaisActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();

    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        cargarTabla(txtBuscar.getText());
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        if (!validarCampos()) return;
        if (controlador.guardarProveedor(leerFormulario())) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Proveedor guardado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo guardar el proveedor.");
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        if (tblProveedores.getSelectedRow() < 0) {
            Mensajes.aviso(this, "Seleccione un proveedor de la tabla."); return;
        }
        if (!validarCampos()) return;
        if (controlador.actualizarProveedor(leerFormulario())) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Proveedor actualizado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo actualizar el proveedor.");
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if (tblProveedores.getSelectedRow() < 0) {
            Mensajes.aviso(this, "Seleccione un proveedor de la tabla."); return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar el proveedor seleccionado?")) return;
        if (controlador.eliminarProveedor(Integer.parseInt(txtIdProveedor.getText()))) {
            cargarTabla(""); limpiarFormulario();
        } else {
            Mensajes.error(this, "No se pudo eliminar. Es posible que el proveedor tenga productos asociados.");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new vistaProveedores().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JCheckBox chkEstatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JTable tblProveedores;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCiudad;
    private javax.swing.JTextField txtContacto;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmpresa;
    private javax.swing.JTextField txtIdProveedor;
    private javax.swing.JTextField txtNit;
    private javax.swing.JTextField txtPais;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JTextField txtTelefonoContacto;
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
