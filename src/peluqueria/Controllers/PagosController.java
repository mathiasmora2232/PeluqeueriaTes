package peluqueria.Controllers;

import java.math.BigDecimal;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import peluqueria.Database.FacturaDAO;
import peluqueria.Database.FacturaDetalleDAO;
import peluqueria.Database.PagoDAO;
import peluqueria.Models.Factura;
import peluqueria.Models.FacturaDetalle;

public class PagosController implements Initializable {

    // Tabla facturas pendientes
    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, Integer> colId;
    @FXML private TableColumn<Factura, String> colCliente;
    @FXML private TableColumn<Factura, String> colFecha;
    @FXML private TableColumn<Factura, BigDecimal> colTotal;
    @FXML private TableColumn<Factura, String> colEstado;

    // Detalle de factura seleccionada
    @FXML private TableView<FacturaDetalle> tablaDetalle;
    @FXML private TableColumn<FacturaDetalle, String> colServicio;
    @FXML private TableColumn<FacturaDetalle, String> colEstilista;
    @FXML private TableColumn<FacturaDetalle, BigDecimal> colPrecio;
    @FXML private TableColumn<FacturaDetalle, BigDecimal> colSubtotal;

    // Pago
    @FXML private Label lblFacturaInfo;
    @FXML private Label lblTotalPagar;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private Label lblMensaje;

    private ObservableList<Factura> listaFacturas = FXCollections.observableArrayList();
    private ObservableList<FacturaDetalle> listaDetalle = FXCollections.observableArrayList();
    private Factura facturaSeleccionada = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Columnas facturas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFormateada"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaFacturas.setItems(listaFacturas);

        // Columnas detalle
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicioNombre"));
        colEstilista.setCellValueFactory(new PropertyValueFactory<>("estilistaNombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tablaDetalle.setItems(listaDetalle);

        // Metodos de pago
        cmbMetodoPago.setItems(FXCollections.observableArrayList(
            "EFECTIVO", "TARJETA", "TRANSFERENCIA", "PAYPHONE"
        ));

        // Al seleccionar factura, mostrar detalle
        tablaFacturas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                facturaSeleccionada = newVal;
                lblFacturaInfo.setText("Factura #" + newVal.getId() + " - " + newVal.getClienteNombre());
                lblTotalPagar.setText("$" + newVal.getTotal().toString());
                // Cargar detalle
                listaDetalle.clear();
                List<FacturaDetalle> detalles = FacturaDetalleDAO.obtenerPorFactura(newVal.getId());
                listaDetalle.addAll(detalles);
            }
        });

        cargarFacturasPendientes();
    }

    private void cargarFacturasPendientes() {
        listaFacturas.clear();
        listaDetalle.clear();
        facturaSeleccionada = null;
        lblFacturaInfo.setText("Seleccione una factura");
        lblTotalPagar.setText("$0.00");

        List<Factura> pendientes = FacturaDAO.obtenerPendientes();
        listaFacturas.addAll(pendientes);
    }

    @FXML
    private void confirmarPago() {
        if (facturaSeleccionada == null) {
            mostrarMensaje("Seleccione una factura pendiente", true);
            return;
        }
        if (cmbMetodoPago.getValue() == null) {
            mostrarMensaje("Seleccione un metodo de pago", true);
            return;
        }

        // Registrar pago
        boolean pagoOk = PagoDAO.crear(
            facturaSeleccionada.getId(),
            cmbMetodoPago.getValue(),
            facturaSeleccionada.getTotal()
        );

        if (!pagoOk) {
            mostrarMensaje("Error al registrar pago", true);
            return;
        }

        // Marcar factura como pagada
        boolean facturaOk = FacturaDAO.marcarPagada(facturaSeleccionada.getId());

        if (facturaOk) {
            mostrarMensaje("Pago registrado! Factura #" + facturaSeleccionada.getId() + " PAGADA", false);
            cmbMetodoPago.setValue(null);
            cargarFacturasPendientes();
        } else {
            mostrarMensaje("Error al actualizar factura", true);
        }
    }

    @FXML
    private void refrescar() {
        cargarFacturasPendientes();
        mostrarMensaje("Datos actualizados", false);
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
    @FXML private void irCaja() {
        cargarVista("/peluqueria/Vistas/Caja.fxml", "Sistema Peluqueria - Caja");
    }
    @FXML private void irPagos() { }
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
            Stage actual = (Stage) tablaFacturas.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
