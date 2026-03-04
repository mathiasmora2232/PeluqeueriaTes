package peluqueria.Controllers;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import peluqueria.Database.ServicioDAO;
import peluqueria.Models.Servicio;

public class ServiciosController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtBuscar;
    @FXML private Label lblMensaje;

    @FXML private TableView<Servicio> tablaServicios;
    @FXML private TableColumn<Servicio, String> colNombre;
    @FXML private TableColumn<Servicio, BigDecimal> colPrecio;
    @FXML private TableColumn<Servicio, Integer> colDuracion;
    @FXML private TableColumn<Servicio, String> colDescripcion;

    private ObservableList<Servicio> listaServicios = FXCollections.observableArrayList();
    private FilteredList<Servicio> listaFiltrada;
    private Servicio servicioSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionMin"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        listaFiltrada = new FilteredList<>(listaServicios, p -> true);
        tablaServicios.setItems(listaFiltrada);

        // Filtro de busqueda
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            listaFiltrada.setPredicate(s -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return s.getNombre().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        // Al seleccionar fila, cargar datos en formulario
        tablaServicios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                servicioSeleccionado = newVal;
                txtNombre.setText(newVal.getNombre());
                txtPrecio.setText(newVal.getPrecio() != null ? newVal.getPrecio().toString() : "");
                txtDuracion.setText(newVal.getDuracionMin() != null ? String.valueOf(newVal.getDuracionMin()) : "");
                txtDescripcion.setText(newVal.getDescripcion() != null ? newVal.getDescripcion() : "");
            }
        });

        cargarServicios();
    }

    private void cargarServicios() {
        listaServicios.clear();
        List<Servicio> servicios = ServicioDAO.obtenerTodos();
        listaServicios.addAll(servicios);
    }

    @FXML
    private void guardarServicio() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje("Ingrese el nombre del servicio", true);
            return;
        }
        if (txtPrecio.getText().trim().isEmpty()) {
            mostrarMensaje("Ingrese el precio", true);
            return;
        }
        if (!txtPrecio.getText().trim().matches("[0-9]+(\\.[0-9]{1,2})?")) {
            mostrarMensaje("Precio invalido. Use formato: 15.00", true);
            return;
        }
        if (!txtDuracion.getText().trim().isEmpty() && !txtDuracion.getText().trim().matches("[0-9]+")) {
            mostrarMensaje("La duracion debe ser un numero entero en minutos", true);
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje("El precio debe ser mayor a 0", true);
                return;
            }
            int duracion = txtDuracion.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtDuracion.getText().trim());

            Servicio nuevo = new Servicio(
                txtNombre.getText().trim(),
                precio,
                duracion,
                txtDescripcion.getText().trim()
            );

            if (ServicioDAO.crear(nuevo)) {
                mostrarMensaje("Servicio creado exitosamente!", false);
                limpiarFormulario();
                cargarServicios();
            } else {
                mostrarMensaje("Error al crear servicio", true);
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Precio o duracion invalidos", true);
        }
    }

    @FXML
    private void editarServicio() {
        if (servicioSeleccionado == null) {
            mostrarMensaje("Seleccione un servicio de la tabla", true);
            return;
        }
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje("Ingrese el nombre del servicio", true);
            return;
        }
        if (txtPrecio.getText().trim().isEmpty()) {
            mostrarMensaje("Ingrese el precio", true);
            return;
        }
        if (!txtPrecio.getText().trim().matches("[0-9]+(\\.[0-9]{1,2})?")) {
            mostrarMensaje("Precio invalido. Use formato: 15.00", true);
            return;
        }
        if (!txtDuracion.getText().trim().isEmpty() && !txtDuracion.getText().trim().matches("[0-9]+")) {
            mostrarMensaje("La duracion debe ser un numero entero en minutos", true);
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje("El precio debe ser mayor a 0", true);
                return;
            }
            int duracion = txtDuracion.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtDuracion.getText().trim());

            servicioSeleccionado.setNombre(txtNombre.getText().trim());
            servicioSeleccionado.setPrecio(precio);
            servicioSeleccionado.setDuracionMin(duracion);
            servicioSeleccionado.setDescripcion(txtDescripcion.getText().trim());

            if (ServicioDAO.actualizar(servicioSeleccionado)) {
                mostrarMensaje("Servicio actualizado!", false);
                limpiarFormulario();
                cargarServicios();
            } else {
                mostrarMensaje("Error al actualizar servicio", true);
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Precio o duracion invalidos", true);
        }
    }

    @FXML
    private void eliminarServicio() {
        if (servicioSeleccionado == null) {
            mostrarMensaje("Seleccione un servicio de la tabla", true);
            return;
        }

        if (ServicioDAO.eliminar(servicioSeleccionado.getId())) {
            mostrarMensaje("Servicio eliminado!", false);
            limpiarFormulario();
            cargarServicios();
        } else {
            mostrarMensaje("Error al eliminar servicio", true);
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtDuracion.clear();
        txtDescripcion.clear();
        lblMensaje.setText("");
        servicioSeleccionado = null;
        tablaServicios.getSelectionModel().clearSelection();
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
    @FXML private void irServicios() { }
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
            Stage actual = (Stage) txtNombre.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
