/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;
import controller.ControllerCliente;
import java.awt.Graphics;
import java.awt.Image;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.cliente;
import util.Mensajes;

/**
 *
 * @author edson
 */
public class vistaClientes extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(vistaClientes.class.getName());
    private static final int MAX_TEXTO = 100;
    private static final int MAX_NIT = 10;
    private static final int MAX_EMAIL = 50;

    private final ControllerCliente controlador = new ControllerCliente();
    private List<cliente> listaClientes = new ArrayList<>();
    FondoPanel fondo = new FondoPanel();    

    
    /**
     * Creates new form vistaClientes
     */
    public vistaClientes() {
        initComponents();
        
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        txtIdCliente.setEditable(false);
        txtFechaIngreso.setEditable(false);
        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarEnFormulario();
        });
        cargarTabla("");
        limpiarFormulario();
    }
    
    private void cargarTabla(String filtro) {
        listaClientes = controlador.obtenerClientes();
        DefaultTableModel modelo = (DefaultTableModel) tblClientes.getModel();
        modelo.setRowCount(0);
        String f = filtro.trim().toLowerCase();
        for (cliente c : listaClientes) {
            String texto = (Objects.toString(c.getNombre(), "") + " "
                    + Objects.toString(c.getApellido(), "") + " "
                    + Objects.toString(c.getRazon_social(), "") + " "
                    + Objects.toString(c.getNit(), "") + " "
                    + Objects.toString(c.getEmail(), "")).toLowerCase();
            if (f.isEmpty() || texto.contains(f)) {
                modelo.addRow(new Object[]{
                    String.valueOf(c.getId_cliente()),
                    Objects.toString(c.getNombre(), ""),
                    Objects.toString(c.getApellido(), ""),
                    Objects.toString(c.getRazon_social(), ""),
                    Objects.toString(c.getNit(), ""),
                    Objects.toString(c.getDireccion(), ""),
                    Objects.toString(c.getTelefono(), ""),
                    Objects.toString(c.getEmail(), ""),
                    Objects.toString(c.getFecha_ingreso(), ""),
                    c.isEstatus() ? "Activo" : "Inactivo"
                });
            }
        }
    }
    private int siguienteId() {
        int max = 0;
        for (cliente c : listaClientes) max = Math.max(max, c.getId_cliente());
        return max + 1;
    }
    private void limpiarFormulario() {
        tblClientes.clearSelection();
        txtIdCliente.setText(String.valueOf(siguienteId()));
        txtNombre.setText("");
        txtApellido.setText("");
        txtRazonSocial.setText("");
        txtNit.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtFechaIngreso.setText(LocalDate.now().toString());
        chkEstatus.setSelected(true);
        txtNombre.requestFocus();
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
        if (txtNombre.getText().trim().isEmpty()) {
            Mensajes.aviso(this, "El nombre es obligatorio.");
            txtNombre.requestFocus(); return false;
        }
        if (txtApellido.getText().trim().isEmpty()) {
            Mensajes.aviso(this, "El apellido es obligatorio.");
            txtApellido.requestFocus(); return false;
        }
        if (txtNit.getText().trim().isEmpty()) {
            Mensajes.aviso(this, "El NIT es obligatorio.");
            txtNit.requestFocus(); return false;
        }
        if (txtTelefono.getText().trim().isEmpty()) {
            Mensajes.aviso(this, "El teléfono es obligatorio.");
            txtTelefono.requestFocus(); return false;
        }

        if (excede(txtNombre, MAX_TEXTO, "El nombre")
                || excede(txtApellido, MAX_TEXTO, "El apellido")
                || excede(txtRazonSocial, MAX_TEXTO, "La razón social")
                || excede(txtNit, MAX_NIT, "El NIT")
                || excede(txtDireccion, MAX_TEXTO, "La dirección")
                || excede(txtTelefono, MAX_TEXTO, "El teléfono")
                || excede(txtEmail, MAX_EMAIL, "El email")) {
            return false;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            Mensajes.aviso(this, "El email no tiene un formato válido (ejemplo: nombre@correo.com).");
            txtEmail.requestFocus(); return false;
        }

        try {
            LocalDate.parse(txtFechaIngreso.getText().trim());
        } catch (DateTimeParseException ex) {
            Mensajes.aviso(this, "La fecha de ingreso debe tener el formato yyyy-MM-dd.");
            return false;
        }
        return true;
    }
    private cliente leerFormulario() {
        return new cliente(
            Integer.parseInt(txtIdCliente.getText()),
            txtNombre.getText().trim(),
            txtApellido.getText().trim(),
            txtRazonSocial.getText().trim(),
            txtNit.getText().trim(),
            txtDireccion.getText().trim(),
            txtTelefono.getText().trim(),
            txtEmail.getText().trim(),
            txtFechaIngreso.getText().trim(),
            chkEstatus.isSelected());
    }
    private void mostrarEnFormulario() {
        int fila = tblClientes.getSelectedRow();
        if (fila < 0) return;
        int id = Integer.parseInt(tblClientes.getValueAt(fila, 0).toString());
        for (cliente c : listaClientes) {
            if (c.getId_cliente() == id) {
                txtIdCliente.setText(String.valueOf(c.getId_cliente()));
                txtNombre.setText(Objects.toString(c.getNombre(), ""));
                txtApellido.setText(Objects.toString(c.getApellido(), ""));
                txtRazonSocial.setText(Objects.toString(c.getRazon_social(), ""));
                txtNit.setText(Objects.toString(c.getNit(), ""));
                txtDireccion.setText(Objects.toString(c.getDireccion(), ""));
                txtTelefono.setText(Objects.toString(c.getTelefono(), ""));
                txtEmail.setText(Objects.toString(c.getEmail(), ""));
                txtFechaIngreso.setText(Objects.toString(c.getFecha_ingreso(), LocalDate.now().toString()));
                chkEstatus.setSelected(c.isEstatus());
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
        txtIdCliente = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtRazonSocial = new javax.swing.JTextField();
        txtNit = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtFechaIngreso = new javax.swing.JTextField();
        chkEstatus = new javax.swing.JCheckBox();
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
        pnlBotones = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        pnlBusqueda = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        scpTabla = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jPanel1 = new FondoPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clientes");

        pnlGeneral.setBackground(new java.awt.Color(255, 255, 255));

        pnlFormulario.setBackground(new java.awt.Color(247, 247, 247));
        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial Narrow", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(34, 19, 83));
        jLabel1.setText("Datos del Cliente");

        txtIdCliente.setEditable(false);
        txtIdCliente.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtNombre.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N
        txtNombre.addActionListener(this::txtNombreActionPerformed);

        txtApellido.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtRazonSocial.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtNit.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtDireccion.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N
        txtDireccion.addActionListener(this::txtDireccionActionPerformed);

        txtTelefono.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtEmail.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtFechaIngreso.setEditable(false);
        txtFechaIngreso.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        chkEstatus.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N
        chkEstatus.setText("Activo");

        jLabel2.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel2.setText("ID Cliente:");

        jLabel3.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel3.setText("Nombres:");

        jLabel4.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel4.setText("Apellidos:");

        jLabel5.setText("Razón Social:");

        jLabel6.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel6.setText("NIT:");

        jLabel7.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel7.setText("Dirección:");

        jLabel8.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel8.setText("Teléfono:");

        jLabel9.setText("Email:");

        jLabel10.setText("Fecha Ingreso:");

        jLabel11.setText("Estatus:");

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(jLabel1)
                        .addGap(0, 43, Short.MAX_VALUE))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtNombre)
                            .addComponent(txtIdCliente, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRazonSocial)
                            .addComponent(txtApellido, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDireccion)
                            .addComponent(txtNit, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFechaIngreso)
                            .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlFormularioLayout.createSequentialGroup()
                                .addComponent(chkEstatus)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(txtNit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(txtFechaIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
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
                .addGap(0, 24, Short.MAX_VALUE))
        );

        pnlBusqueda.setBackground(new java.awt.Color(247, 247, 247));
        pnlBusqueda.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de los Clientes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel12.setBackground(new java.awt.Color(34, 19, 83));
        jLabel12.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(34, 19, 83));
        jLabel12.setText("REGISTRO DE CLIENTES");

        tblClientes.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Cliente", "Nombre", "Apellido", "Razón Social", "NIT", "Dirección", "Teléfono", "Email", "Fecha Ingreso", "Estatus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblClientes.setGridColor(new java.awt.Color(247, 247, 247));
        tblClientes.setSelectionBackground(new java.awt.Color(247, 247, 247));
        tblClientes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scpTabla.setViewportView(tblClientes);

        jLabel13.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel13.setText("Buscar:");

        txtBuscar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        btnBuscar.setBackground(new java.awt.Color(247, 247, 247));
        btnBuscar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);

        javax.swing.GroupLayout pnlBusquedaLayout = new javax.swing.GroupLayout(pnlBusqueda);
        pnlBusqueda.setLayout(pnlBusquedaLayout);
        pnlBusquedaLayout.setHorizontalGroup(
            pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scpTabla, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 910, Short.MAX_VALUE)
            .addGroup(pnlBusquedaLayout.createSequentialGroup()
                .addGroup(pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBusquedaLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel12)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlBusquedaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar)))
                .addContainerGap())
        );
        pnlBusquedaLayout.setVerticalGroup(
            pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
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
            .addGap(0, 83, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 83, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(pnlBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
            .addComponent(pnlGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        if (!validarCampos()) return;
        if (controlador.guardarCliente(leerFormulario())) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Cliente guardado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo guardar el cliente.");
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDireccionActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        cargarTabla(txtBuscar.getText());
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        if (tblClientes.getSelectedRow() < 0) {
            Mensajes.aviso(this, "Seleccione un cliente de la tabla."); return;
        }
        if (!validarCampos()) return;
        if (controlador.actualizarCliente(leerFormulario())) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Cliente actualizado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo actualizar el cliente.");
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if (tblClientes.getSelectedRow() < 0) {
            Mensajes.aviso(this, "Seleccione un cliente de la tabla."); return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar el cliente seleccionado?")) return;
        if (controlador.eliminarCliente(Integer.parseInt(txtIdCliente.getText()))) {
            cargarTabla(""); limpiarFormulario();
        } else {
            Mensajes.error(this, "No se pudo eliminar. Es posible que el cliente tenga facturas asociadas.");
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
        java.awt.EventQueue.invokeLater(() -> new vistaClientes().setVisible(true));
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
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFechaIngreso;
    private javax.swing.JTextField txtIdCliente;
    private javax.swing.JTextField txtNit;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtRazonSocial;
    private javax.swing.JTextField txtTelefono;
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
