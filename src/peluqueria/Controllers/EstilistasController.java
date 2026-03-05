package peluqueria.Controllers;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import peluqueria.Database.EstilistaDAO;
import peluqueria.Database.UsuarioDAO;
import peluqueria.Models.Estilista;
import peluqueria.Models.Usuario;

public class EstilistasController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEspecialidad;
    @FXML private TextField txtExperiencia;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<Usuario> cmbUsuario;
    @FXML private TextField txtBuscar;
    @FXML private Label lblMensaje;

    @FXML private TableView<Estilista> tablaEstilistas;
    @FXML private TableColumn<Estilista, String> colNombre;
    @FXML private TableColumn<Estilista, String> colTelefono;
    @FXML private TableColumn<Estilista, String> colEspecialidad;
    @FXML private TableColumn<Estilista, Integer> colExperiencia;
    @FXML private TableColumn<Estilista, String> colEstado;

    private ObservableList<Estilista> listaEstilistas = FXCollections.observableArrayList();
    private FilteredList<Estilista> listaFiltrada;
    private Estilista estilistaSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colExperiencia.setCellValueFactory(new PropertyValueFactory<>("experienciaAnios"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));

        // Cargar usuarios disponibles para asociar
        List<Usuario> usuarios = UsuarioDAO.obtenerTodos();
        cmbUsuario.setItems(FXCollections.observableArrayList(usuarios));

        listaFiltrada = new FilteredList<>(listaEstilistas, p -> true);
        tablaEstilistas.setItems(listaFiltrada);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            listaFiltrada.setPredicate(e -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return e.getNombre().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        tablaEstilistas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                estilistaSeleccionado = newVal;
                txtNombre.setText(newVal.getNombre());
                txtTelefono.setText(newVal.getTelefono() != null ? newVal.getTelefono() : "");
                txtEspecialidad.setText(newVal.getEspecialidad() != null ? newVal.getEspecialidad() : "");
                txtExperiencia.setText(String.valueOf(newVal.getExperienciaAnios()));
                cmbEstado.setValue(newVal.getEstado());
                // Buscar el usuario asociado en el ComboBox
                for (Usuario u : cmbUsuario.getItems()) {
                    if (u.getId() == newVal.getIdUsuario()) {
                        cmbUsuario.setValue(u);
                        break;
                    }
                }
            }
        });

        cargarEstilistas();
    }

    private void cargarEstilistas() {
        listaEstilistas.clear();
        // Obtener todos los estilistas (activos e inactivos) para la gestion
        List<Estilista> estilistas = obtenerTodosEstilistas();
        listaEstilistas.addAll(estilistas);
    }

    // Obtener todos incluyendo inactivos
    private List<Estilista> obtenerTodosEstilistas() {
        return EstilistaDAO.obtenerTodos();
    }

    @FXML
    private void guardarEstilista() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty()) {
            mostrarMensaje("Ingrese el nombre", true);
            return;
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            mostrarMensaje("El nombre solo debe contener letras", true);
            return;
        }
        if (!telefono.isEmpty() && !telefono.matches("[0-9\\-\\+]+")) {
            mostrarMensaje("El telefono solo debe contener numeros", true);
            return;
        }
        if (!telefono.isEmpty() && telefono.replaceAll("[^0-9]", "").length() < 7) {
            mostrarMensaje("El telefono debe tener al menos 7 digitos", true);
            return;
        }
        if (cmbUsuario.getValue() == null) {
            mostrarMensaje("Seleccione un usuario asociado", true);
            return;
        }
        if (cmbEstado.getValue() == null) {
            mostrarMensaje("Seleccione el estado", true);
            return;
        }

        int experiencia = 0;
        try {
            if (!txtExperiencia.getText().trim().isEmpty()) {
                experiencia = Integer.parseInt(txtExperiencia.getText().trim());
                if (experiencia < 0) {
                    mostrarMensaje("La experiencia no puede ser negativa", true);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Experiencia debe ser un numero entero", true);
            return;
        }

        Estilista nuevo = new Estilista(
            cmbUsuario.getValue().getId(),
            txtNombre.getText().trim(),
            txtTelefono.getText().trim(),
            txtEspecialidad.getText().trim(),
            experiencia,
            cmbEstado.getValue()
        );

        if (EstilistaDAO.crear(nuevo)) {
            mostrarMensaje("Estilista creado exitosamente!", false);
            limpiarFormulario();
            cargarEstilistas();
        } else {
            mostrarMensaje("Error al crear estilista", true);
        }
    }

    @FXML
    private void editarEstilista() {
        if (estilistaSeleccionado == null) {
            mostrarMensaje("Seleccione un estilista de la tabla", true);
            return;
        }
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty()) {
            mostrarMensaje("Ingrese el nombre", true);
            return;
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            mostrarMensaje("El nombre solo debe contener letras", true);
            return;
        }
        if (!telefono.isEmpty() && !telefono.matches("[0-9\\-\\+]+")) {
            mostrarMensaje("El telefono solo debe contener numeros", true);
            return;
        }
        if (!telefono.isEmpty() && telefono.replaceAll("[^0-9]", "").length() < 7) {
            mostrarMensaje("El telefono debe tener al menos 7 digitos", true);
            return;
        }

        int experiencia = 0;
        try {
            if (!txtExperiencia.getText().trim().isEmpty()) {
                experiencia = Integer.parseInt(txtExperiencia.getText().trim());
                if (experiencia < 0) {
                    mostrarMensaje("La experiencia no puede ser negativa", true);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Experiencia debe ser un numero entero", true);
            return;
        }

        estilistaSeleccionado.setNombre(txtNombre.getText().trim());
        estilistaSeleccionado.setTelefono(txtTelefono.getText().trim());
        estilistaSeleccionado.setEspecialidad(txtEspecialidad.getText().trim());
        estilistaSeleccionado.setExperienciaAnios(experiencia);
        estilistaSeleccionado.setEstado(cmbEstado.getValue());

        if (EstilistaDAO.actualizar(estilistaSeleccionado)) {
            mostrarMensaje("Estilista actualizado!", false);
            limpiarFormulario();
            cargarEstilistas();
        } else {
            mostrarMensaje("Error al actualizar estilista", true);
        }
    }

    @FXML
    private void eliminarEstilista() {
        if (estilistaSeleccionado == null) {
            mostrarMensaje("Seleccione un estilista de la tabla", true);
            return;
        }

        if (EstilistaDAO.eliminar(estilistaSeleccionado.getIdUsuario())) {
            mostrarMensaje("Estilista desactivado!", false);
            limpiarFormulario();
            cargarEstilistas();
        } else {
            mostrarMensaje("Error al eliminar estilista", true);
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        txtTelefono.clear();
        txtEspecialidad.clear();
        txtExperiencia.clear();
        cmbEstado.setValue(null);
        cmbUsuario.setValue(null);
        lblMensaje.setText("");
        estilistaSeleccionado = null;
        tablaEstilistas.getSelectionModel().clearSelection();
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
    @FXML private void irEstilistas() { }
    @FXML private void irUsuarios() {
        cargarVista("/peluqueria/Vistas/Usuarios.fxml", "Sistema Peluqueria - Usuarios");
    }
    @FXML private void irCaja() {
        cargarVista("/peluqueria/Vistas/Caja.fxml", "Sistema Peluqueria - Caja");
    }
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
            Stage actual = (Stage) txtNombre.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
