/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import controller.ControllerCliente;
import controller.ControllerEmpleado;
import controller.ControllerFactura;
import controller.ControllerProducto;
import java.awt.Graphics;
import java.awt.Image;
import java.time.format.DateTimeParseException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.cliente;
import model.empleado;
import model.factura;
import model.factura_detalle;
import model.producto;
import util.ItemCombo;
import util.Mensajes;

/**
 *
 * @author edson
 */
public class vistaFacturas extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(vistaFacturas.class.getName());
    private static final String SERIE = "A";

    private final ControllerFactura controlador = new ControllerFactura();
    private final ControllerCliente controladorCliente = new ControllerCliente();
    private final ControllerEmpleado controladorEmpleado = new ControllerEmpleado();
    private final ControllerProducto controladorProducto = new ControllerProducto();

    private List<cliente> listaClientes = new ArrayList<>();
    private List<producto> listaProductos = new ArrayList<>();
    private final List<factura_detalle> lineas = new ArrayList<>(); 
    FondoPanel fondo = new FondoPanel();    
    private static final int PORCENTAJE_IVA = 12;
    private static final BigDecimal TASA_IVA = BigDecimal.valueOf(PORCENTAJE_IVA).divide(BigDecimal.valueOf(100));


    /**
     * Creates new form vistaFacturas
     */
   public vistaFacturas() {
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        txtNit.setEditable(false);
        txtNoFact.setEditable(false);
        txtSerie.setEditable(false);
        txtFechaFac.setEditable(false);

        tblDetalle.setDefaultEditor(Object.class, null); 
        tblDetalle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nuevaFactura();
    }
    private void cargarCombos() {
        listaClientes = controladorCliente.obtenerClientes();
        List<empleado> empleados = controladorEmpleado.obtenerEmpleados();
        listaProductos = controladorProducto.obtenerProductos();

        cmbCliente.removeAllItems();
        for (cliente c : listaClientes) {
            if (c.isEstatus()) {
                String nombre = Objects.toString(c.getNombre(), "") + " " + Objects.toString(c.getApellido(), "");
                cmbCliente.addItem(new ItemCombo(c.getId_cliente(), nombre.trim()));
            }
        }

        cmbEmpleado.removeAllItems();
        for (empleado e : empleados) {
            if (e.isEstatus()) {
                String nombre = Objects.toString(e.getPrimer_nombre(), "") + " " + Objects.toString(e.getPrimer_apellido(), "");
                cmbEmpleado.addItem(new ItemCombo(e.getId_empleado(), nombre.trim()));
            }
        }

        cmbProducto.removeAllItems();
        for (producto p : listaProductos) {
            cmbProducto.addItem(new ItemCombo(p.getId_producto(),
                    Objects.toString(p.getDescripcion(), "") + " (stock: " + p.getStock() + ")"));
        }
    }
    private cliente buscarCliente(int id) {
        for (cliente c : listaClientes) {
            if (c.getId_cliente() == id) return c;
        }
        return null;
    }

    private producto buscarProducto(int id) {
        for (producto p : listaProductos) {
            if (p.getId_producto() == id) return p;
        }
        return null;
    }

    private factura_detalle buscarLinea(int idProducto) {
        for (factura_detalle d : lineas) {
            if (d.getId_producto() == idProducto) return d;
        }
        return null;
    }

    private int cantidadEnLineas(int idProducto) {
        factura_detalle d = buscarLinea(idProducto);
        return d == null ? 0 : d.getCantidad();
    }
    
    private String formatearMonto(BigDecimal monto) {
        return monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private void refrescarDetalle() {
    DefaultTableModel modelo = (DefaultTableModel) tblDetalle.getModel();
    modelo.setRowCount(0);
    BigDecimal subtotalGeneral = BigDecimal.ZERO;
    for (factura_detalle d : lineas) {
        producto p = buscarProducto(d.getId_producto());
        BigDecimal precio = BigDecimal.valueOf(p.getPrecio_unitario());
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(d.getCantidad()));
        subtotalGeneral = subtotalGeneral.add(subtotal);
        modelo.addRow(new Object[]{
            String.valueOf(d.getId_linea()),
            String.valueOf(p.getId_producto()),
            Objects.toString(p.getDescripcion(), ""),
            String.valueOf(d.getCantidad()),
            formatearMonto(precio),
            formatearMonto(subtotal)
        });
    }
    BigDecimal iva = calcularIva(subtotalGeneral);
    lblSubtotalValor.setText(formatearMonto(subtotalGeneral.subtract(iva)));
    lblIvaValor.setText(formatearMonto(iva));
    lblTotalValor.setText(formatearMonto(subtotalGeneral));
}

    private void nuevaFactura() {
        lineas.clear();
        cargarCombos();
        txtNoFact.setText(String.valueOf(controlador.obtenerSiguienteId()));
        txtSerie.setText(SERIE);
        txtFechaFac.setText(LocalDate.now().toString());  
        spnCantidad.setValue(1);
        refrescarDetalle();
    }
    
    private void agregarLinea() {
        ItemCombo itemProducto = (ItemCombo) cmbProducto.getSelectedItem();
        if (itemProducto == null) {
            Mensajes.aviso(this, "Seleccione un producto. Si la lista está vacía, registre primero productos.");
            return;
        }
        producto prod = buscarProducto(itemProducto.getId());
        if (prod == null) {
            Mensajes.error(this, "El producto seleccionado ya no existe.");
            return;
        }

        int cantidad = ((Number) spnCantidad.getValue()).intValue();
        int yaAgregada = cantidadEnLineas(prod.getId_producto());
        if (yaAgregada + cantidad > prod.getStock()) {
            Mensajes.aviso(this, "Stock insuficiente de \"" + prod.getDescripcion() + "\".\n"
                    + "Disponible: " + prod.getStock() + ". Ya agregado en esta factura: " + yaAgregada + ".");
            return;
        }

        factura_detalle existente = buscarLinea(prod.getId_producto());
        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + cantidad);
        } else {
            lineas.add(new factura_detalle(0, lineas.size() + 1, prod.getId_producto(), cantidad));
        }
        refrescarDetalle();
        spnCantidad.setValue(1);
    }
    
    private void quitarLinea() {
        int fila = tblDetalle.getSelectedRow();
        if (fila < 0) {
            Mensajes.aviso(this, "Seleccione una línea del detalle para quitarla.");
            return;
        }
        lineas.remove(tblDetalle.convertRowIndexToModel(fila));
        for (int i = 0; i < lineas.size(); i++) {
            lineas.get(i).setId_linea(i + 1);
        }
        refrescarDetalle();
    }
    
    private void guardarFactura() {
        ItemCombo itemCliente = (ItemCombo) cmbCliente.getSelectedItem();
        ItemCombo itemEmpleado = (ItemCombo) cmbEmpleado.getSelectedItem();
        if (itemCliente == null) {
            Mensajes.aviso(this, "Seleccione un cliente. Si la lista está vacía, registre un cliente activo.");
            return;
        }
        if (itemEmpleado == null) {
            Mensajes.aviso(this, "Seleccione un empleado. Si la lista está vacía, registre un empleado activo.");
            return;
        }
        if (lineas.isEmpty()) {
            Mensajes.aviso(this, "Agregue al menos un producto a la factura.");
            return;
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFechaFac.getText().trim());
        } catch (DateTimeParseException ex) {
            Mensajes.aviso(this, "La fecha de la factura no es válida. Use el formato yyyy-MM-dd.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Desea guardar la factura por un total de " + lblTotalValor.getText() + "?")) {
            return;
        }

        int idFactura = controlador.obtenerSiguienteId();
        if (idFactura <= 0) {
            Mensajes.error(this, "No se pudo obtener el número de factura. Verifique la conexión a la base de datos.");
            return;
        }

        factura fac = new factura(idFactura, idFactura, SERIE,
                itemCliente.getId(), itemEmpleado.getId(), fecha.toString());
        List<factura_detalle> detalles = new ArrayList<>();
        for (factura_detalle d : lineas) {
            d.setId_factura(idFactura);
            detalles.add(d);
        }

        if (controlador.guardarFactura(fac, detalles)) {
            Mensajes.info(this, "Factura No. " + idFactura + " guardada correctamente.");
            if (Mensajes.confirmar(this, "¿Desea imprimir la factura?")) {
                imprimirFactura(fac, itemCliente.toString(), txtNit.getText(), itemEmpleado.toString());
            }
            nuevaFactura();
        } else {
        }
    }

    private String esc(String texto) {
        return Objects.toString(texto, "")
                .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    
    //  para poder imprimir la factura se instancia un html que extrae la informacion de la factura, posteriomente
    // ejecuta el comando para que sea impreso
    
    private String construirHtmlFactura(factura fac, String nombreCliente, String nit, String nombreEmpleado) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body bgcolor='#FFFFFF' text='#000000' style='font-family: sans-serif; font-size: 12pt'>");
        sb.append("<h2 align='center'>FACTURA</h2>");
        sb.append("<p><b>No.:</b> ").append(fac.getNo_factura())
          .append(" &nbsp;&nbsp; <b>Serie:</b> ").append(esc(fac.getSerie()))
          .append(" &nbsp;&nbsp; <b>Fecha:</b> ").append(fac.getFecha_factura()).append("<br>")
          .append("<b>Cliente:</b> ").append(esc(nombreCliente)).append("<br>")
          .append("<b>NIT:</b> ").append(esc(nit)).append("<br>")
          .append("<b>Atendido por:</b> ").append(esc(nombreEmpleado)).append("</p>");

        sb.append("<table border='1' cellpadding='4' cellspacing='0' width='100%'>");
        sb.append("<tr><th>Cant.</th><th>Descripción</th><th>Precio</th><th>Subtotal</th></tr>");
        BigDecimal subtotalGeneral = BigDecimal.ZERO;
        for (factura_detalle d : lineas) {
            producto p = buscarProducto(d.getId_producto());
            BigDecimal precio = BigDecimal.valueOf(p.getPrecio_unitario());
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(d.getCantidad()));
            subtotalGeneral = subtotalGeneral.add(subtotal);
            sb.append("<tr><td align='center'>").append(d.getCantidad()).append("</td>")
              .append("<td>").append(esc(p.getDescripcion())).append("</td>")
              .append("<td align='right'>").append(formatearMonto(precio)).append("</td>")
              .append("<td align='right'>").append(formatearMonto(subtotal)).append("</td></tr>");
        }
        sb.append("</table>");

        BigDecimal iva = calcularIva(subtotalGeneral);
        sb.append("<p align='right'>Subtotal: ").append(formatearMonto(subtotalGeneral.subtract(iva))).append("<br>")
          .append("IVA (").append(PORCENTAJE_IVA).append("%): ").append(formatearMonto(iva)).append("</p>");
        sb.append("<h3 align='right'>TOTAL: ").append(formatearMonto(subtotalGeneral)).append("</h3>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private void imprimirFactura(factura fac, String nombreCliente, String nit, String nombreEmpleado) {
        javax.swing.JEditorPane documento = new javax.swing.JEditorPane("text/html",
                construirHtmlFactura(fac, nombreCliente, nit, nombreEmpleado));
        documento.setEditable(false);
        try {
            documento.print();
        } catch (java.awt.print.PrinterException ex) {
            Mensajes.error(this, "No se pudo imprimir la factura.");
        }
    }
    private BigDecimal calcularIva(BigDecimal totalConIva) {
        return totalConIva.subtract(totalConIva.divide(BigDecimal.ONE.add(TASA_IVA), 2, RoundingMode.HALF_UP));
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
        pnlEncabezado = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtNoFact = new javax.swing.JTextField();
        txtSerie = new javax.swing.JTextField();
        txtFechaFac = new javax.swing.JTextField();
        txtNit = new javax.swing.JTextField();
        cmbCliente = new javax.swing.JComboBox<>();
        cmbEmpleado = new javax.swing.JComboBox<>();
        pnlTotal = new javax.swing.JPanel();
        btnNuevaFactura = new javax.swing.JButton();
        btnGuardarFactura = new javax.swing.JButton();
        pnlResultados = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblIvaValor = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblSubtotalValor = new javax.swing.JLabel();
        lblTotal1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblTotalValor = new javax.swing.JLabel();
        pnlLinea = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        cmbProducto = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        spnCantidad = new javax.swing.JSpinner();
        btnAgregar = new javax.swing.JButton();
        btnQuitar = new javax.swing.JButton();
        scpDetalle = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        jPanel1 = new FondoPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Facturas");

        pnlGeneral.setBackground(new java.awt.Color(255, 255, 255));

        pnlEncabezado.setBackground(new java.awt.Color(247, 247, 247));
        pnlEncabezado.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos de la Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel1.setText("No de Factura:");

        jLabel2.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel2.setText("Serie:");

        jLabel3.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel3.setText("Fecha:");

        jLabel4.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel4.setText("Cliente:");

        jLabel5.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel5.setText("Empleado:");

        jLabel6.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel6.setText("NIT:");

        txtNoFact.setEditable(false);
        txtNoFact.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N

        txtSerie.setEditable(false);
        txtSerie.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N

        txtFechaFac.setEditable(false);
        txtFechaFac.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N

        txtNit.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        txtNit.addActionListener(this::txtNitActionPerformed);

        cmbCliente.setBackground(new java.awt.Color(247, 247, 247));
        cmbCliente.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        cmbCliente.addActionListener(this::cmbClienteActionPerformed);

        cmbEmpleado.setBackground(new java.awt.Color(247, 247, 247));
        cmbEmpleado.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        cmbEmpleado.addActionListener(this::cmbEmpleadoActionPerformed);

        javax.swing.GroupLayout pnlEncabezadoLayout = new javax.swing.GroupLayout(pnlEncabezado);
        pnlEncabezado.setLayout(pnlEncabezadoLayout);
        pnlEncabezadoLayout.setHorizontalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNoFact, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFechaFac, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNit, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbEmpleado, 0, 168, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlEncabezadoLayout.setVerticalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNoFact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtFechaFac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtNit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cmbEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTotal.setBackground(new java.awt.Color(247, 247, 247));
        pnlTotal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total de Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N
        pnlTotal.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N

        btnNuevaFactura.setBackground(new java.awt.Color(247, 247, 247));
        btnNuevaFactura.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        btnNuevaFactura.setText("NUEVA FACTURA");
        btnNuevaFactura.addActionListener(this::btnNuevaFacturaActionPerformed);

        btnGuardarFactura.setBackground(new java.awt.Color(247, 247, 247));
        btnGuardarFactura.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        btnGuardarFactura.setText("GUARDAR FACTURA");
        btnGuardarFactura.addActionListener(this::btnGuardarFacturaActionPerformed);

        pnlResultados.setBackground(new java.awt.Color(247, 247, 247));

        jLabel11.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel11.setText("IVA:");

        jLabel10.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        jLabel10.setText("Q");

        lblIvaValor.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        lblIvaValor.setText("0.00");

        jLabel12.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel12.setText("Subtotal:");

        jLabel13.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        jLabel13.setText("Q");

        lblSubtotalValor.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        lblSubtotalValor.setText("0.00");

        lblTotal1.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        lblTotal1.setText("TOTAL:");

        jLabel9.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        jLabel9.setText("Q");

        lblTotalValor.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        lblTotalValor.setText("0.00");

        javax.swing.GroupLayout pnlResultadosLayout = new javax.swing.GroupLayout(pnlResultados);
        pnlResultados.setLayout(pnlResultadosLayout);
        pnlResultadosLayout.setHorizontalGroup(
            pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResultadosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(lblIvaValor, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSubtotalValor, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTotal1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(lblTotalValor, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlResultadosLayout.setVerticalGroup(
            pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(lblIvaValor)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(lblSubtotalValor)
                    .addComponent(lblTotal1)
                    .addComponent(jLabel9)
                    .addComponent(lblTotalValor))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlTotalLayout = new javax.swing.GroupLayout(pnlTotal);
        pnlTotal.setLayout(pnlTotalLayout);
        pnlTotalLayout.setHorizontalGroup(
            pnlTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(btnNuevaFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGuardarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlTotalLayout.setVerticalGroup(
            pnlTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalLayout.createSequentialGroup()
                .addGroup(pnlTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnGuardarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNuevaFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlLinea.setBackground(new java.awt.Color(247, 247, 247));
        pnlLinea.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Agregar Producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Narrow", 0, 14))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel7.setText("Producto:");

        cmbProducto.setBackground(new java.awt.Color(247, 247, 247));
        cmbProducto.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel8.setText("Cantidad:");

        spnCantidad.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        spnCantidad.setModel(new javax.swing.SpinnerNumberModel(1, 1, 9999, 1));

        btnAgregar.setBackground(new java.awt.Color(247, 247, 247));
        btnAgregar.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(this::btnAgregarActionPerformed);

        btnQuitar.setBackground(new java.awt.Color(247, 247, 247));
        btnQuitar.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        btnQuitar.setText("Quitar");
        btnQuitar.addActionListener(this::btnQuitarActionPerformed);

        javax.swing.GroupLayout pnlLineaLayout = new javax.swing.GroupLayout(pnlLinea);
        pnlLinea.setLayout(pnlLineaLayout);
        pnlLineaLayout.setHorizontalGroup(
            pnlLineaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLineaLayout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQuitar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlLineaLayout.setVerticalGroup(
            pnlLineaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLineaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnAgregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnQuitar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel8)
                .addComponent(cmbProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel7))
        );

        tblDetalle.setBackground(new java.awt.Color(247, 247, 247));
        tblDetalle.setFont(new java.awt.Font("Arial Narrow", 1, 14)); // NOI18N
        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Línea", "ID Producto", "Descripción", "Cantidad", "Precio Unitario", "Sub Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblDetalle.setGridColor(new java.awt.Color(247, 247, 247));
        tblDetalle.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scpDetalle.setViewportView(tblDetalle);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 95, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 71, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scpDetalle, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlLinea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGeneralLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLinea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scpDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void txtNitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNitActionPerformed

    private void cmbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClienteActionPerformed
        // TODO add your handling code here:
        ItemCombo item = (ItemCombo) cmbCliente.getSelectedItem();
        if (item == null) {
            txtNit.setText("");
            return;
        }
        cliente c = buscarCliente(item.getId());
        txtNit.setText(c == null ? "" : Objects.toString(c.getNit(), ""));
    }//GEN-LAST:event_cmbClienteActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // TODO add your handling code here:
        agregarLinea();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnQuitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarActionPerformed
        // TODO add your handling code here:
        quitarLinea();
    }//GEN-LAST:event_btnQuitarActionPerformed

    private void btnNuevaFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaFacturaActionPerformed
        // TODO add your handling code here:
        nuevaFactura();
    }//GEN-LAST:event_btnNuevaFacturaActionPerformed

    private void btnGuardarFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarFacturaActionPerformed
        // TODO add your handling code here:
        guardarFactura();
    }//GEN-LAST:event_btnGuardarFacturaActionPerformed

    private void cmbEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbEmpleadoActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new vistaFacturas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnGuardarFactura;
    private javax.swing.JButton btnNuevaFactura;
    private javax.swing.JButton btnQuitar;
    private javax.swing.JComboBox<util.ItemCombo> cmbCliente;
    private javax.swing.JComboBox<util.ItemCombo> cmbEmpleado;
    private javax.swing.JComboBox<util.ItemCombo> cmbProducto;
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
    private javax.swing.JLabel lblIvaValor;
    private javax.swing.JLabel lblSubtotalValor;
    private javax.swing.JLabel lblTotal1;
    private javax.swing.JLabel lblTotalValor;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlLinea;
    private javax.swing.JPanel pnlResultados;
    private javax.swing.JPanel pnlTotal;
    private javax.swing.JScrollPane scpDetalle;
    private javax.swing.JSpinner spnCantidad;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTextField txtFechaFac;
    private javax.swing.JTextField txtNit;
    private javax.swing.JTextField txtNoFact;
    private javax.swing.JTextField txtSerie;
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
