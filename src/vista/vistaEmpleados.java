/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;
import controller.ControllerEmpleado;
import controller.ControllerDepartamento;
import java.awt.Graphics;
import java.awt.Image;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.departamento;
import model.empleado;
import util.ItemCombo;
import util.Mensajes;
/**
 *
 * @author edson
 */
public class vistaEmpleados extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(vistaEmpleados.class.getName());
    private final ControllerEmpleado controlador = new ControllerEmpleado();
    private final ControllerDepartamento controladorDepto = new ControllerDepartamento();
    private List<empleado> listaEmpleados = new ArrayList<>();
    private List<departamento> listaDepartamentos = new ArrayList<>();
    FondoPanel fondo = new FondoPanel(); 

    /**
     * Creates new form vistaEmpleados
     */
    public vistaEmpleados() {
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLocationRelativeTo(null);
        txtIdEmpleado.setEditable(false);
        tblEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarEnFormulario();
        });
        cargarCombos();
        cargarTabla("");
        limpiarFormulario();
    }
    private void cargarCombos() {
        listaDepartamentos = controladorDepto.obtenerDepartamentos();
        cmbIdDepartamento.removeAllItems();
        for (departamento d : listaDepartamentos) {
            cmbIdDepartamento.addItem(new ItemCombo(d.getId_departamento(), d.getDescripcion()));
        }
    }
    private String nombreDepartamento(int id) {
    for (departamento d : listaDepartamentos) {
        if (d.getId_departamento() == id) return d.getDescripcion();
    }
    return "";
    }
    private void cargarTabla(String filtro) {
        listaEmpleados = controlador.obtenerEmpleados();
        DefaultTableModel modelo = (DefaultTableModel) tblEmpleados.getModel();
        modelo.setRowCount(0);
        String f = filtro.trim().toLowerCase();
        for (empleado e : listaEmpleados) {
            String texto = (Objects.toString(e.getPrimer_nombre(), "") + " "
                    + Objects.toString(e.getSegundo_nombre(), "") + " "
                    + Objects.toString(e.getPrimer_apellido(), "") + " "
                    + Objects.toString(e.getSegundo_apellido(), "") + " "
                    + Objects.toString(e.getNit(), "")).toLowerCase();
            if (f.isEmpty() || texto.contains(f)) {
                modelo.addRow(new Object[]{
                    String.valueOf(e.getId_empleado()),
                    Objects.toString(e.getPrimer_nombre(), ""),
                    Objects.toString(e.getSegundo_nombre(), ""),
                    Objects.toString(e.getPrimer_apellido(), ""),
                    Objects.toString(e.getSegundo_apellido(), ""),
                    Objects.toString(e.getNit(), ""),
                    String.format("%.2f", e.getSalario()),
                    nombreDepartamento(e.getId_departamento()),
                    e.isEstatus() ? "Activo" : "Inactivo"
                });
            }
        }
    }
    private int siguienteId() {
    int max = 0;
    for (empleado e : listaEmpleados) max = Math.max(max, e.getId_empleado());
    return max + 1;
    }
    private void limpiarFormulario() {
        tblEmpleados.clearSelection();
        txtIdEmpleado.setText(String.valueOf(siguienteId()));
        txtPrimerNombre.setText("");
        txtSegundoNombre.setText("");
        txtPrimerApellido.setText("");
        txtSegundoApellido.setText("");
        txtNit.setText("");
        txtSalario.setText("");
        if (cmbIdDepartamento.getItemCount() > 0) cmbIdDepartamento.setSelectedIndex(0);
        chkEstatus.setSelected(true);
        txtPrimerNombre.requestFocus();
    }
    private boolean validarCampos() {
        if (txtPrimerNombre.getText().trim().isEmpty()) {
            Mensajes.aviso(this, "El primer nombre es obligatorio.");
            txtPrimerNombre.requestFocus(); return false;
        }
        if (txtPrimerApellido.getText().trim().isEmpty()) {
            Mensajes.aviso(this, "El primer apellido es obligatorio.");
            txtPrimerApellido.requestFocus(); return false;
        }
        if (txtPrimerNombre.getText().trim().length() > 100 || txtSegundoNombre.getText().trim().length() > 100
                || txtPrimerApellido.getText().trim().length() > 100 || txtSegundoApellido.getText().trim().length() > 100) {
            Mensajes.aviso(this, "Los nombres y apellidos admiten máximo 100 caracteres."); return false;
        }
        String nit = txtNit.getText().trim();
        if (nit.isEmpty() || nit.length() > 10) {
            Mensajes.aviso(this, "El NIT es obligatorio y admite máximo 10 caracteres.");
            txtNit.requestFocus(); return false;
        }
        try {
            BigDecimal salario = new BigDecimal(txtSalario.getText().trim());
            if (salario.compareTo(BigDecimal.ZERO) < 0) {
                Mensajes.aviso(this, "El salario no puede ser negativo."); return false;
            }
        } catch (NumberFormatException ex) {
            Mensajes.aviso(this, "El salario debe ser un número válido (ejemplo: 5000.50).");
            txtSalario.requestFocus(); return false;
        }
        if (cmbIdDepartamento.getSelectedItem() == null) {
            Mensajes.aviso(this, "Seleccione un departamento."); return false;
        }
        return true;
    }
    private empleado leerFormulario() {
        ItemCombo depto = (ItemCombo) cmbIdDepartamento.getSelectedItem();
        return new empleado(
            Integer.parseInt(txtIdEmpleado.getText()),
            txtPrimerNombre.getText().trim(),
            txtSegundoNombre.getText().trim(),
            txtPrimerApellido.getText().trim(),
            txtSegundoApellido.getText().trim(),
            txtNit.getText().trim(),
            new BigDecimal(txtSalario.getText().trim()).doubleValue(),
            chkEstatus.isSelected(),
            depto.getId());
    }
    private void mostrarEnFormulario() {
        int fila = tblEmpleados.getSelectedRow();
        if (fila < 0) return;
        int id = Integer.parseInt(tblEmpleados.getValueAt(fila, 0).toString());
        for (empleado e : listaEmpleados) {
            if (e.getId_empleado() == id) {
                txtIdEmpleado.setText(String.valueOf(e.getId_empleado()));
                txtPrimerNombre.setText(Objects.toString(e.getPrimer_nombre(), ""));
                txtSegundoNombre.setText(Objects.toString(e.getSegundo_nombre(), ""));
                txtPrimerApellido.setText(Objects.toString(e.getPrimer_apellido(), ""));
                txtSegundoApellido.setText(Objects.toString(e.getSegundo_apellido(), ""));
                txtNit.setText(Objects.toString(e.getNit(), ""));
                txtSalario.setText(String.valueOf(e.getSalario()));
                ItemCombo.seleccionar(cmbIdDepartamento, e.getId_departamento());
                chkEstatus.setSelected(e.isEstatus());
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cmbIdDepartamento = new javax.swing.JComboBox<>();
        txtIdEmpleado = new javax.swing.JTextField();
        txtPrimerNombre = new javax.swing.JTextField();
        txtSegundoNombre = new javax.swing.JTextField();
        txtPrimerApellido = new javax.swing.JTextField();
        txtSegundoApellido = new javax.swing.JTextField();
        txtNit = new javax.swing.JTextField();
        txtSalario = new javax.swing.JTextField();
        chkEstatus = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        pnlBotones = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        pnlBusqueda = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        scpTabla = new javax.swing.JScrollPane();
        tblEmpleados = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jPanel1 = new FondoPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Empleados");

        pnlGeneral.setBackground(new java.awt.Color(255, 255, 255));

        pnlFormulario.setBackground(new java.awt.Color(247, 247, 247));
        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial Narrow", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(34, 19, 83));
        jLabel1.setText("Información del Empleado");

        jLabel2.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel2.setText("ID Empleado:");

        jLabel3.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel3.setText("Primer Nombre:");

        jLabel4.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel4.setText("Segundo Nombre");

        jLabel6.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel6.setText("Primer Apellido:");

        jLabel7.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel7.setText("Segundo Apellido:");

        jLabel8.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel8.setText("NIT:");

        jLabel9.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel9.setText("Salario:");

        jLabel11.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel11.setText("ID Departamento");

        cmbIdDepartamento.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        cmbIdDepartamento.setToolTipText("");
        cmbIdDepartamento.addActionListener(this::cmbIdDepartamentoActionPerformed);

        txtIdEmpleado.setEditable(false);
        txtIdEmpleado.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N
        txtIdEmpleado.addActionListener(this::txtIdEmpleadoActionPerformed);

        txtPrimerNombre.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtSegundoNombre.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtPrimerApellido.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtSegundoApellido.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtNit.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        txtSalario.setFont(new java.awt.Font("Arial Narrow", 0, 16)); // NOI18N

        chkEstatus.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        chkEstatus.setText("Activo");

        jLabel10.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel10.setText("Estatus:");

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFormularioLayout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(18, 18, 18)
                            .addComponent(txtPrimerNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pnlFormularioLayout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(jLabel2)
                            .addGap(18, 18, 18)
                            .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSegundoNombre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                            .addComponent(txtPrimerApellido, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel7)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(pnlFormularioLayout.createSequentialGroup()
                                    .addComponent(txtSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 2, Short.MAX_VALUE))
                                .addComponent(txtSegundoApellido, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtNit, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(cmbIdDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkEstatus))))
                .addContainerGap(27, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFormularioLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(55, 55, 55))
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtPrimerNombre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtSegundoNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtPrimerApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(txtSegundoApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(txtNit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(txtSalario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cmbIdDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkEstatus)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBotones.setBackground(new java.awt.Color(247, 247, 247));
        pnlBotones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Opciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        btnNuevo.setBackground(new java.awt.Color(247, 247, 247));
        btnNuevo.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(this::btnNuevoActionPerformed);

        btnGuardar.setBackground(new java.awt.Color(247, 247, 247));
        btnGuardar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnModificar.setBackground(new java.awt.Color(247, 247, 247));
        btnModificar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.addActionListener(this::btnModificarActionPerformed);

        btnEliminar.setBackground(new java.awt.Color(247, 247, 247));
        btnEliminar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        javax.swing.GroupLayout pnlBotonesLayout = new javax.swing.GroupLayout(pnlBotones);
        pnlBotones.setLayout(pnlBotonesLayout);
        pnlBotonesLayout.setHorizontalGroup(
            pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlBotonesLayout.setVerticalGroup(
            pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotonesLayout.createSequentialGroup()
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModificar)
                    .addComponent(btnEliminar)))
        );

        pnlBusqueda.setBackground(new java.awt.Color(247, 247, 247));
        pnlBusqueda.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de los Empleados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel12.setBackground(new java.awt.Color(34, 19, 83));
        jLabel12.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        jLabel12.setText("REGISTRO DE EMPLEADOS");

        tblEmpleados.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        tblEmpleados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Empleado", "Primer Nombre", "Segundo Nombre", "Primer Apellido", "Segundo Apellido", "NIT", "Salario", "Departamento", "Estatus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblEmpleados.setGridColor(new java.awt.Color(247, 247, 247));
        tblEmpleados.setSelectionBackground(new java.awt.Color(247, 247, 247));
        tblEmpleados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scpTabla.setViewportView(tblEmpleados);

        jLabel5.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        jLabel5.setText("Buscar:");

        txtBuscar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N

        btnBuscar.setBackground(new java.awt.Color(247, 247, 247));
        btnBuscar.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);

        javax.swing.GroupLayout pnlBusquedaLayout = new javax.swing.GroupLayout(pnlBusqueda);
        pnlBusqueda.setLayout(pnlBusquedaLayout);
        pnlBusquedaLayout.setHorizontalGroup(
            pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scpTabla, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
            .addGroup(pnlBusquedaLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBusquedaLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar))
                    .addComponent(jLabel12))
                .addContainerGap())
        );
        pnlBusquedaLayout.setVerticalGroup(
            pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
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
            .addGap(0, 132, Short.MAX_VALUE)
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
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addComponent(pnlBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 6, Short.MAX_VALUE))
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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbIdDepartamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIdDepartamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbIdDepartamentoActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void txtIdEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdEmpleadoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        if (!validarCampos()) return;
        if (controlador.guardarEmpleado(leerFormulario())) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Empleado guardado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo guardar el empleado.");
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        if (tblEmpleados.getSelectedRow() < 0) {
            Mensajes.aviso(this, "Seleccione un empleado de la tabla."); return;
        }
        if (!validarCampos()) return;
        if (controlador.actualizarEmpleado(leerFormulario())) {
            cargarTabla(""); limpiarFormulario();
            Mensajes.info(this, "Empleado actualizado correctamente.");
        } else {
            Mensajes.error(this, "No se pudo actualizar el empleado.");
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if (tblEmpleados.getSelectedRow() < 0) {
            Mensajes.aviso(this, "Seleccione un empleado de la tabla."); return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar el empleado seleccionado?")) return;
        if (controlador.eliminarEmpleado(Integer.parseInt(txtIdEmpleado.getText()))) {
            cargarTabla(""); limpiarFormulario();
        } else {
            Mensajes.error(this, "No se pudo eliminar. Es posible que tenga usuarios o facturas asociados.");
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
        java.awt.EventQueue.invokeLater(() -> new vistaEmpleados().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JCheckBox chkEstatus;
    private javax.swing.JComboBox<util.ItemCombo> cmbIdDepartamento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JTable tblEmpleados;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtIdEmpleado;
    private javax.swing.JTextField txtNit;
    private javax.swing.JTextField txtPrimerApellido;
    private javax.swing.JTextField txtPrimerNombre;
    private javax.swing.JTextField txtSalario;
    private javax.swing.JTextField txtSegundoApellido;
    private javax.swing.JTextField txtSegundoNombre;
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
