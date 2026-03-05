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
import peluqueria.Models.Factura;
import peluqueria.Models.FacturaDetalle;

public class FacturacionController implements Initializable {

    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, Integer> colId;
    @FXML private TableColumn<Factura, String> colCliente;
    @FXML private TableColumn<Factura, String> colFecha;
    @FXML private TableColumn<Factura, BigDecimal> colSubtotal;
    @FXML private TableColumn<Factura, BigDecimal> colImpuesto;
    @FXML private TableColumn<Factura, BigDecimal> colTotal;
    @FXML private TableColumn<Factura, String> colEstado;

    @FXML private TableView<FacturaDetalle> tablaDetalle;
    @FXML private TableColumn<FacturaDetalle, String> colDetServicio;
    @FXML private TableColumn<FacturaDetalle, String> colDetEstilista;
    @FXML private TableColumn<FacturaDetalle, BigDecimal> colDetPrecio;
    @FXML private TableColumn<FacturaDetalle, BigDecimal> colDetSubtotal;

    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private Label lblMensaje;

    private ObservableList<Factura> listaFacturas = FXCollections.observableArrayList();
    private ObservableList<FacturaDetalle> listaDetalle = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Columnas facturas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFormateada"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colImpuesto.setCellValueFactory(new PropertyValueFactory<>("impuesto"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaFacturas.setItems(listaFacturas);

        // Columnas detalle
        colDetServicio.setCellValueFactory(new PropertyValueFactory<>("servicioNombre"));
        colDetEstilista.setCellValueFactory(new PropertyValueFactory<>("estilistaNombre"));
        colDetPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDetSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tablaDetalle.setItems(listaDetalle);

        // Filtro de estado
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "PENDIENTE", "PAGADA"));

        // Al seleccionar factura, mostrar detalle
        tablaFacturas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                listaDetalle.clear();
                List<FacturaDetalle> detalles = FacturaDetalleDAO.obtenerPorFactura(newVal.getId());
                listaDetalle.addAll(detalles);
            }
        });

        cargarFacturas();
    }

    private void cargarFacturas() {
        listaFacturas.clear();
        listaDetalle.clear();
        List<Factura> todas = FacturaDAO.obtenerTodas();
        listaFacturas.addAll(todas);
    }

    @FXML
    private void filtrar() {
        String filtro = cmbFiltroEstado.getValue();
        listaFacturas.clear();
        listaDetalle.clear();

        if (filtro == null || filtro.equals("Todos")) {
            listaFacturas.addAll(FacturaDAO.obtenerTodas());
        } else if (filtro.equals("PENDIENTE")) {
            listaFacturas.addAll(FacturaDAO.obtenerPendientes());
        } else {
            // PAGADA - filtrar de todas
            for (Factura f : FacturaDAO.obtenerTodas()) {
                if ("PAGADA".equals(f.getEstado())) {
                    listaFacturas.add(f);
                }
            }
        }
        mostrarMensaje("Mostrando: " + (filtro != null ? filtro : "Todos"), false);
    }

    @FXML
    private void refrescar() {
        cargarFacturas();
        cmbFiltroEstado.setValue(null);
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
    @FXML private void irPagos() {
        cargarVista("/peluqueria/Vistas/PagosFactura.fxml", "Sistema Peluqueria - Pagos");
    }
    @FXML private void irFacturacion() { }
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
