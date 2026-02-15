package peluqueria.Controllers;

import java.net.URL;
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

public class ClientesController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtNotas;
    @FXML private TextField txtBuscar;
    @FXML private Label lblMensaje;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, String> colNotas;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private FilteredList<Cliente> listaFiltrada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar columnas de la tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colNotas.setCellValueFactory(new PropertyValueFactory<>("notas"));

        // Configurar lista filtrada para busqueda
        listaFiltrada = new FilteredList<>(listaClientes, p -> true);
        tablaClientes.setItems(listaFiltrada);

        // Filtrar al escribir en el campo de busqueda
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(cliente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filtro = newValue.toLowerCase();
                return cliente.getNombre().toLowerCase().contains(filtro)
                    || cliente.getTelefono().toLowerCase().contains(filtro)
                    || cliente.getEmail().toLowerCase().contains(filtro);
            });
        });
    }

    @FXML
    private void guardarCliente() {
        if (txtNombre.getText().isEmpty()) {
            mostrarMensaje("Ingrese el nombre del cliente", true);
            return;
        }
        if (txtTelefono.getText().isEmpty()) {
            mostrarMensaje("Ingrese el telefono", true);
            return;
        }

        Cliente nuevo = new Cliente(
            txtNombre.getText(),
            txtTelefono.getText(),
            txtEmail.getText(),
            txtDireccion.getText(),
            txtNotas.getText()
        );

        listaClientes.add(nuevo);
        mostrarMensaje("Cliente guardado exitosamente!", false);
        limpiarFormulario();
    }

    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtNotas.clear();
        lblMensaje.setText("");
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        if (esError) {
            lblMensaje.setStyle("-fx-text-fill: #EF4444;");
        } else {
            lblMensaje.setStyle("-fx-text-fill: #10B981;");
        }
    }

    // NAVEGACION
    @FXML
    private void irDashboard() {
        cargarVista("/peluqueria/Vistas/Dashboard.fxml", "Sistema Peluqueria - Dashboard");
    }

    @FXML
    private void irCitas() {
        cargarVista("/peluqueria/Vistas/AgendarCita.fxml", "Sistema Peluqueria - Agendar Cita");
    }

    @FXML private void irClientes() { }
    @FXML private void irServicios() { }
    @FXML private void irPagos() { }

    @FXML
    private void cerrarSesion() {
        cargarVista("/peluqueria/Vistas/Login.fxml", "Sistema Peluqueria - Login");
    }

    private void cargarVista(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

            Stage actual = (Stage) txtNombre.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clase interna para representar un cliente en la tabla
    public static class Cliente {
        private String nombre;
        private String telefono;
        private String email;
        private String direccion;
        private String notas;

        public Cliente(String nombre, String telefono, String email, String direccion, String notas) {
            this.nombre = nombre;
            this.telefono = telefono;
            this.email = email;
            this.direccion = direccion;
            this.notas = notas;
        }

        public String getNombre() { return nombre; }
        public String getTelefono() { return telefono; }
        public String getEmail() { return email; }
        public String getDireccion() { return direccion; }
        public String getNotas() { return notas; }
    }
}
