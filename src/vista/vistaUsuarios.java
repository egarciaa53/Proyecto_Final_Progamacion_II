/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controller.ControllerEmpleado;
import controller.ControllerUsuario;
import java.awt.Graphics;
import java.awt.Image;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.empleado;
import model.usuario;
import util.ItemCombo;
import util.Mensajes;
import util.Seguridad;
/**
 *
 * @author edson
 */
public class vistaUsuarios extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(vistaUsuarios.class.getName());
    private static final int MAX_USUARIO = 100;
    private static final int MIN_CONTRASENA = 4;
    private static final int MAX_CONTRASENA = 50;

    private final ControllerUsuario controlador = new ControllerUsuario();
    private final ControllerEmpleado controladorEmpleado = new ControllerEmpleado();
    private List<usuario> listaUsuarios = new ArrayList<>();
    private usuario usuarioSeleccionado;
    private char echoPorDefecto;
    FondoPanel fondo = new FondoPanel();    

    /**
     * Creates new form vistaUsuarios
     */
    public vistaUsuarios() {
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        txtIdUsuario.setEditable(false);
        txtFechaIngreso.setEditable(false);
        txtFechaUltimaModificacion.setEditable(false);
        echoPorDefecto = pwdContrasena.getEchoChar();
        pwdContrasena.setToolTipText("Al modificar, déjelo vacío para conservar la contraseña actual.");

        tblUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarEnFormulario();
        });
        cargarCombos();
        cargarTabla("");
        limpiarFormulario();
    }
    private void cargarCombos() {
        List<empleado> empleados = controladorEmpleado.obtenerEmpleados();
        cmbIdEmpleado.removeAllItems();
        for (empleado e : empleados) {
            String texto = Objects.toString(e.getPrimer_nombre(), "") + " "
                    + Objects.toString(e.getPrimer_apellido(), "")
                    + (e.isEstatus() ? "" : " (Inactivo)");
            cmbIdEmpleado.addItem(new ItemCombo(e.getId_empleado(), texto.trim()));
        }
    }
    
    private void cargarTabla(String filtro) {
        listaUsuarios = controlador.obtenerUsuarios();
        DefaultTableModel modelo = (DefaultTableModel) tblUsuarios.getModel();
        modelo.setRowCount(0);
        String f = filtro.trim().toLowerCase();
        for (usuario u : listaUsuarios) {
            String texto = (u.getId_usuario() + " " + Objects.toString(u.getUsuario(), "")).toLowerCase();
            if (f.isEmpty() || texto.contains(f)) {
                modelo.addRow(new Object[]{
                    String.valueOf(u.getId_usuario()),
                    Objects.toString(u.getUsuario(), ""),
                    Objects.toString(u.getFecha_ingreso(), ""),
                    Objects.toString(u.getFecha_ultima_modificacion(), ""),
                    String.valueOf(u.getId_empleado()),
                    u.isEstatus() ? "Activo" : "Inactivo"
                });
            }
        }
    }

    private int siguienteId() {
        int max = 0;
        for (usuario u : listaUsuarios) max = Math.max(max, u.getId_usuario());
        return max + 1;
    }

    private void limpiarFormulario() {
        usuarioSeleccionado = null;
        tblUsuarios.clearSelection();
        String hoy = LocalDate.now().toString();
        txtIdUsuario.setText(String.valueOf(siguienteId()));
        txtUsuario.setText("");
        pwdContrasena.setText("");
        txtFechaIngreso.setText(hoy);
        txtFechaUltimaModificacion.setText(hoy);
        if (cmbIdEmpleado.getItemCount() > 0) cmbIdEmpleado.setSelectedIndex(0);
        chkEstatus.setSelected(true);
        txtUsuario.requestFocus();
    }
    
    private boolean usuarioDuplicado(String nombre) {
        for (usuario u : listaUsuarios) {
            boolean esElMismo = usuarioSeleccionado != null
                    && u.getId_usuario() == usuarioSeleccionado.getId_usuario()
                    && Objects.equals(u.getUsuario(), usuarioSeleccionado.getUsuario());
            if (!esElMismo && nombre.equalsIgnoreCase(Objects.toString(u.getUsuario(), ""))) {
                return true;
            }
        }
        return false;
        }

    private boolean validarCampos(boolean contrasenaObligatoria) {
        String nombre = txtUsuario.getText().trim();
        if (nombre.isEmpty()) {
            Mensajes.aviso(this, "El nombre de usuario es obligatorio.");
            txtUsuario.requestFocus(); return false;
        }
        if (nombre.length() > MAX_USUARIO) {
            Mensajes.aviso(this, "El usuario admite máximo " + MAX_USUARIO + " caracteres.");
            txtUsuario.requestFocus(); return false;
        }
        if (!nombre.matches("\\S+")) {
            Mensajes.aviso(this, "El usuario no puede contener espacios.");
            txtUsuario.requestFocus(); return false;
        }
        if (usuarioDuplicado(nombre)) {
            Mensajes.aviso(this, "Ya existe un usuario con ese nombre.");
            txtUsuario.requestFocus(); return false;
        }

        String clave = new String(pwdContrasena.getPassword());
        if (clave.isEmpty() && contrasenaObligatoria) {
            Mensajes.aviso(this, "La contraseña es obligatoria.");
            pwdContrasena.requestFocus(); return false;
        }
        if (!clave.isEmpty() && (clave.length() < MIN_CONTRASENA || clave.length() > MAX_CONTRASENA)) {
            Mensajes.aviso(this, "La contraseña debe tener entre " + MIN_CONTRASENA
                    + " y " + MAX_CONTRASENA + " caracteres.");
            pwdContrasena.requestFocus(); return false;
        }

        if (cmbIdEmpleado.getSelectedItem() == null) {
            Mensajes.aviso(this, "Seleccione un empleado. Si la lista está vacía, registre primero un empleado.");
            return false;
        }
        return true;
    }
    private usuario leerFormulario() {
        ItemCombo empleadoSeleccionado = (ItemCombo) cmbIdEmpleado.getSelectedItem();
        String hoy = LocalDate.now().toString();
        return new usuario(
            Integer.parseInt(txtIdUsuario.getText()),
            txtUsuario.getText().trim(),
            "",
            hoy,
            hoy,
            empleadoSeleccionado.getId(),
            chkEstatus.isSelected());
    }
    private void mostrarEnFormulario() {
        int fila = tblUsuarios.getSelectedRow();
        if (fila < 0) {
            usuarioSeleccionado = null;
            return;
        }
        int id = Integer.parseInt(tblUsuarios.getValueAt(fila, 0).toString());
        String nombre = tblUsuarios.getValueAt(fila, 1).toString();
        for (usuario u : listaUsuarios) {
            if (u.getId_usuario() == id && nombre.equals(u.getUsuario())) {
                usuarioSeleccionado = u;
                txtIdUsuario.setText(String.valueOf(u.getId_usuario()));
                txtUsuario.setText(Objects.toString(u.getUsuario(), ""));
                pwdContrasena.setText("");
                txtFechaIngreso.setText(Objects.toString(u.getFecha_ingreso(), ""));
                txtFechaUltimaModificacion.setText(Objects.toString(u.getFecha_ultima_modificacion(), ""));
                ItemCombo.seleccionar(cmbIdEmpleado, u.getId_empleado());
                chkEstatus.setSelected(u.isEstatus());
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

        jPanel1 = new javax.swing.JPanel();
        pnlFormulario = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtIdUsuario = new javax.swing.JTextField();
        txtUsuario = new javax.swing.JTextField();
        txtFechaIngreso = new javax.swing.JTextField();
        txtFechaUltimaModificacion = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        chkEstatus = new javax.swing.JCheckBox();
        pwdContrasena = new javax.swing.JPasswordField();
        chkMostrar = new javax.swing.JCheckBox();
        cmbIdEmpleado = new javax.swing.JComboBox<>();
        pnlBotones = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        pnlBusqueda = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        scpTabla = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jPanel2 = new FondoPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Usuarios");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        pnlFormulario.setBackground(new java.awt.Color(247, 247, 247));
        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial Narrow", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(34, 19, 83));
        jLabel1.setText("Información del Usuario");

        jLabel2.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel2.setText("ID Usuario:");

        txtIdUsuario.setEditable(false);
        txtIdUsuario.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        txtUsuario.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        txtFechaIngreso.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        txtFechaUltimaModificacion.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel3.setText("Usuario:");

        jLabel4.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel4.setText("Contraseña:");

        jLabel5.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel5.setText("Fecha ingreso:");

        jLabel6.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel6.setText("Última modificacion:");

        jLabel7.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel7.setText("ID Empleado:");

        jLabel8.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel8.setText("Estatus:");

        chkEstatus.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        chkEstatus.setText("Activo");

        chkMostrar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        chkMostrar.setText("Mostrar");
        chkMostrar.addActionListener(this::chkMostrarActionPerformed);

        cmbIdEmpleado.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel1)
                        .addGap(0, 66, Short.MAX_VALUE))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlFormularioLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFormularioLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFechaIngreso)
                            .addComponent(txtIdUsuario)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtFechaUltimaModificacion)
                            .addComponent(pwdContrasena)
                            .addComponent(cmbIdEmpleado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnlFormularioLayout.createSequentialGroup()
                                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkMostrar)
                                    .addComponent(chkEstatus))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIdUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(pwdContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMostrar)
                .addGap(5, 5, 5)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFechaIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txtFechaUltimaModificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cmbIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(chkEstatus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(23, 23, 23)
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        pnlBusqueda.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de los Usuarios", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N
        pnlBusqueda.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(34, 19, 83));
        jLabel10.setText("REGISTRO DE USUARIOS");

        tblUsuarios.setBackground(new java.awt.Color(247, 247, 247));
        tblUsuarios.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Usuario", "Usuario", "Fecha Ingreso", "Fecha Última Modificación", "ID Empleado", "Estatus"
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
        tblUsuarios.setGridColor(new java.awt.Color(247, 247, 247));
        tblUsuarios.setSelectionBackground(new java.awt.Color(247, 247, 247));
        tblUsuarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scpTabla.setViewportView(tblUsuarios);

        jLabel9.setText("Buscar:");

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
                    .addComponent(scpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
                    .addGroup(pnlBusquedaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar)))
                .addContainerGap())
            .addGroup(pnlBusquedaLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlBusquedaLayout.setVerticalGroup(
            pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 157, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 131, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void chkMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMostrarActionPerformed
        // TODO add your handling code here:
        if (chkMostrar.isSelected()) {
            pwdContrasena.setEchoChar((char) 0);
        } else {
            pwdContrasena.setEchoChar('•');
        }
    }//GEN-LAST:event_chkMostrarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        if (usuarioSeleccionado != null) {
            Mensajes.aviso(this, "Hay un usuario seleccionado. Presione NUEVO para registrar otro, "
                    + "o MODIFICAR para actualizar el seleccionado.");
            return;
        }
        if (!validarCampos(true)) return;
        usuario u = leerFormulario();
        u.setId_usuario(siguienteId());
        u.setContraseña(Seguridad.sha256(new String(pwdContrasena.getPassword())));
        if (controlador.guardarUsuario(u)) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Usuario guardado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo guardar el usuario.");
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        if (usuarioSeleccionado == null) {
            Mensajes.aviso(this, "Seleccione un usuario de la tabla."); return;
        }
        if (!validarCampos(false)) return;

        usuario u = leerFormulario();
        String clave = new String(pwdContrasena.getPassword());
        u.setContraseña(clave.isEmpty() ? usuarioSeleccionado.getContraseña() : Seguridad.sha256(clave));
        String fechaIngresoOriginal = usuarioSeleccionado.getFecha_ingreso();
        u.setFecha_ingreso(fechaIngresoOriginal != null ? fechaIngresoOriginal : LocalDate.now().toString());

        if (controlador.actualizarUsuario(u, usuarioSeleccionado.getUsuario(), usuarioSeleccionado.getContraseña())) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Usuario actualizado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo actualizar el usuario.");
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if (usuarioSeleccionado == null) {
            Mensajes.aviso(this, "Seleccione un usuario de la tabla."); return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar el usuario \"" + usuarioSeleccionado.getUsuario() + "\"?")) return;
        if (controlador.eliminarUsuario(usuarioSeleccionado.getUsuario(), usuarioSeleccionado.getContraseña())) {
            cargarTabla(""); limpiarFormulario();
        } else {
            Mensajes.error(this, "No se pudo eliminar el usuario.");
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
        java.awt.EventQueue.invokeLater(() -> new vistaUsuarios().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JCheckBox chkEstatus;
    private javax.swing.JCheckBox chkMostrar;
    private javax.swing.JComboBox<util.ItemCombo> cmbIdEmpleado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlBusqueda;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPasswordField pwdContrasena;
    private javax.swing.JScrollPane scpTabla;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtFechaIngreso;
    private javax.swing.JTextField txtFechaUltimaModificacion;
    private javax.swing.JTextField txtIdUsuario;
    private javax.swing.JTextField txtUsuario;
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
