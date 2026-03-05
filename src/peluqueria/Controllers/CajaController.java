package peluqueria.Controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import peluqueria.Database.CitaDAO;
import peluqueria.Database.EstilistaDAO;
import peluqueria.Database.FacturaDAO;
import peluqueria.Database.FacturaDetalleDAO;
import peluqueria.Database.ServicioDAO;
import peluqueria.Models.Estilista;
import peluqueria.Models.FacturaDetalle;
import peluqueria.Models.Servicio;

public class CajaController implements Initializable {

    // Cliente
    @FXML private TextField txtClienteNombre;
    @FXML private TextField txtClienteTelefono;

    // Agregar servicio
    @FXML private ComboBox<Servicio> cmbServicio;
    @FXML private ComboBox<Estilista> cmbEstilista;
    @FXML private Label lblPrecioServicio;

    // Tabla detalle
    @FXML private TableView<FacturaDetalle> tablaDetalle;
    @FXML private TableColumn<FacturaDetalle, String> colServicio;
    @FXML private TableColumn<FacturaDetalle, String> colEstilista;
    @FXML private TableColumn<FacturaDetalle, BigDecimal> colPrecio;
    @FXML private TableColumn<FacturaDetalle, Integer> colCantidad;
    @FXML private TableColumn<FacturaDetalle, BigDecimal> colSubtotal;

    // Totales
    @FXML private Label lblSubtotal;
    @FXML private Label lblIVA;
    @FXML private Label lblTotal;
    @FXML private Label lblMensaje;

    private ObservableList<FacturaDetalle> listaDetalle = FXCollections.observableArrayList();
    private static final BigDecimal TASA_IVA = new BigDecimal("0.15");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar tabla
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicioNombre"));
        colEstilista.setCellValueFactory(new PropertyValueFactory<>("estilistaNombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tablaDetalle.setItems(listaDetalle);

        // Cargar servicios y estilistas
        List<Servicio> servicios = ServicioDAO.obtenerTodos();
        cmbServicio.setItems(FXCollections.observableArrayList(servicios));

        List<Estilista> estilistas = EstilistaDAO.obtenerTodos();
        cmbEstilista.setItems(FXCollections.observableArrayList(estilistas));

        // Mostrar precio al seleccionar servicio
        cmbServicio.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getPrecio() != null) {
                lblPrecioServicio.setText("$" + newVal.getPrecio().toString());
            } else {
                lblPrecioServicio.setText("$0.00");
            }
        });

        actualizarTotales();
    }

    @FXML
    private void agregarServicio() {
        if (cmbServicio.getValue() == null) {
            mostrarMensaje("Seleccione un servicio", true);
            return;
        }
        if (cmbEstilista.getValue() == null) {
            mostrarMensaje("Seleccione un estilista", true);
            return;
        }

        Servicio servicio = cmbServicio.getValue();
        Estilista estilista = cmbEstilista.getValue();

        FacturaDetalle detalle = new FacturaDetalle(
            servicio.getId(),
            servicio.getNombre(),
            estilista.getIdUsuario(),
            estilista.getNombre(),
            servicio.getPrecio(),
            1
        );

        listaDetalle.add(detalle);
        actualizarTotales();
        cmbServicio.setValue(null);
        cmbEstilista.setValue(null);
        lblPrecioServicio.setText("$0.00");
        mostrarMensaje("Servicio agregado", false);
    }

    @FXML
    private void quitarServicio() {
        FacturaDetalle seleccionado = tablaDetalle.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("Seleccione un servicio de la tabla para quitar", true);
            return;
        }
        listaDetalle.remove(seleccionado);
        actualizarTotales();
    }

    private void actualizarTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (FacturaDetalle d : listaDetalle) {
            subtotal = subtotal.add(d.getSubtotal());
        }
        BigDecimal iva = subtotal.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(iva);

        lblSubtotal.setText("$" + subtotal.setScale(2, RoundingMode.HALF_UP));
        lblIVA.setText("$" + iva);
        lblTotal.setText("$" + total.setScale(2, RoundingMode.HALF_UP));
    }

    @FXML
    private void generarFactura() {
        String nombre = txtClienteNombre.getText().trim();
        String telefono = txtClienteTelefono.getText().trim();

        if (nombre.isEmpty()) {
            mostrarMensaje("Ingrese el nombre del cliente", true);
            return;
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            mostrarMensaje("El nombre solo debe contener letras", true);
            return;
        }
        if (telefono.isEmpty()) {
            mostrarMensaje("Ingrese el telefono del cliente", true);
            return;
        }
        if (!telefono.matches("[0-9\\-\\+]+")) {
            mostrarMensaje("El telefono solo debe contener numeros", true);
            return;
        }
        if (telefono.replaceAll("[^0-9]", "").length() < 7) {
            mostrarMensaje("El telefono debe tener al menos 7 digitos", true);
            return;
        }
        if (listaDetalle.isEmpty()) {
            mostrarMensaje("Agregue al menos un servicio", true);
            return;
        }

        // Obtener o crear cliente
        int clienteId = CitaDAO.obtenerOCrearCliente(
            txtClienteNombre.getText().trim(),
            txtClienteTelefono.getText().trim(),
            ""
        );

        if (clienteId == -1) {
            mostrarMensaje("Error al registrar cliente", true);
            return;
        }

        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        for (FacturaDetalle d : listaDetalle) {
            subtotal = subtotal.add(d.getSubtotal());
        }
        BigDecimal iva = subtotal.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(iva);

        // Crear factura
        int facturaId = FacturaDAO.crear(clienteId, subtotal, iva, total);
        if (facturaId == -1) {
            mostrarMensaje("Error al crear factura", true);
            return;
        }

        // Crear detalles
        for (FacturaDetalle d : listaDetalle) {
            FacturaDetalleDAO.crear(
                facturaId,
                d.getServicioId(),
                d.getEstilistaId(),
                d.getPrecio(),
                d.getCantidad(),
                d.getSubtotal()
            );
        }

        mostrarMensaje("Factura #" + facturaId + " generada exitosamente! Total: $" + total, false);
        limpiarTodo();
    }

    @FXML
    private void limpiarTodo() {
        txtClienteNombre.clear();
        txtClienteTelefono.clear();
        cmbServicio.setValue(null);
        cmbEstilista.setValue(null);
        lblPrecioServicio.setText("$0.00");
        listaDetalle.clear();
        actualizarTotales();
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #EF4444;" : "-fx-text-fill: #10B981;");
    }

    // NAVEGACION
    @FXML private void irDashboard() {
        cargarVista("/peluqueria/Vistas/Dashboard.fxml", "Sistema Peluqueria - Dashboard");
    }
    @FXML private void irCitas() {
        cargarVista("/peluqueria/Vistas/AgendarCita.fxml", "Sistema Peluqueria - Citas");
    }
    @FXML private void irClientes() {
        cargarVista("/peluqueria/Vistas/Clientes.fxml", "Sistema Peluqueria - Clientes");
    }
    @FXML private void irServicios() {
        cargarVista("/peluqueria/Vistas/Servicios.fxml", "Sistema Peluqueria - Servicios");
    }
    @FXML private void irEstilistas() {
        cargarVista("/peluqueria/Vistas/Estilistas.fxml", "Sistema Peluqueria - Estilistas");
    }
    @FXML private void irUsuarios() {
        cargarVista("/peluqueria/Vistas/Usuarios.fxml", "Sistema Peluqueria - Usuarios");
    }
    @FXML private void irCaja() { }
    @FXML private void irPagos() {
        cargarVista("/peluqueria/Vistas/PagosFactura.fxml", "Sistema Peluqueria - Pagos");
    }
    @FXML private void irFacturacion() {
        cargarVista("/peluqueria/Vistas/Facturacion.fxml", "Sistema Peluqueria - Facturacion");
    }
    @FXML private void cerrarSesion() {
        cargarVista("/peluqueria/Vistas/Login.fxml", "Sistema Peluqueria - Login");
    }

    private void cargarVista(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.setMaximized(true);
            stage.show();
            Stage actual = (Stage) txtClienteNombre.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
