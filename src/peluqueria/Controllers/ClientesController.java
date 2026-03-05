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
import peluqueria.Database.ClienteDAO;

public class ClientesController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cmbGenero;
    @FXML private TextField txtBuscar;
    @FXML private Label lblMensaje;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colGenero;
    @FXML private TableColumn<Cliente, String> colFechaRegistro;
    @FXML private TableColumn<Cliente, String> colEstado;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private FilteredList<Cliente> listaFiltrada;
    private Cliente clienteSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cmbGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "Otro"));

        listaFiltrada = new FilteredList<>(listaClientes, p -> true);
        tablaClientes.setItems(listaFiltrada);

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

        // Al seleccionar un cliente, cargar datos en el formulario
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                clienteSeleccionado = newVal;
                txtNombre.setText(newVal.getNombre());
                txtTelefono.setText(newVal.getTelefono());
                txtEmail.setText(newVal.getEmail());
                cmbGenero.setValue(newVal.getGenero().isEmpty() ? null : newVal.getGenero());
            }
        });

        cargarClientesDesdeDB();
    }

    private void cargarClientesDesdeDB() {
        listaClientes.clear();
        List<String[]> clientesDB = ClienteDAO.obtenerTodos();
        for (String[] fila : clientesDB) {
            listaClientes.add(new Cliente(
                Integer.parseInt(fila[0]),
                fila[1], fila[2], fila[3], fila[4], fila[5], fila[6]
            ));
        }
    }

    @FXML
    private void guardarCliente() {
        if (!validarFormulario()) return;

        String genero = cmbGenero.getValue() != null ? cmbGenero.getValue() : "";

        String resultado = ClienteDAO.crear(
            txtNombre.getText().trim(),
            txtTelefono.getText().trim(),
            txtEmail.getText().trim(),
            genero
        );
        if (resultado.equals("ok")) {
            mostrarMensaje("Cliente guardado exitosamente!", false);
        } else if (resultado.equals("email_duplicado")) {
            mostrarMensaje("Ya existe un cliente con ese email", true);
            return;
        } else {
            mostrarMensaje("Error al guardar cliente", true);
        }

        limpiarFormulario();
        cargarClientesDesdeDB();
    }

    @FXML
    private void editarCliente() {
        if (clienteSeleccionado == null) {
            mostrarMensaje("Seleccione un cliente de la tabla", true);
            return;
        }
        if (!validarFormulario()) return;

        String genero = cmbGenero.getValue() != null ? cmbGenero.getValue() : "";

        String resultado = ClienteDAO.actualizar(
            clienteSeleccionado.getId(),
            txtNombre.getText().trim(),
            txtTelefono.getText().trim(),
            txtEmail.getText().trim(),
            genero
        );
        if (resultado.equals("ok")) {
            mostrarMensaje("Cliente actualizado exitosamente!", false);
        } else if (resultado.equals("email_duplicado")) {
            mostrarMensaje("Ya existe un cliente con ese email", true);
            return;
        } else {
            mostrarMensaje("Error al actualizar cliente", true);
        }

        limpiarFormulario();
        cargarClientesDesdeDB();
    }

    @FXML
    private void eliminarCliente() {
        if (clienteSeleccionado == null) {
            mostrarMensaje("Seleccione un cliente de la tabla", true);
            return;
        }

        if (ClienteDAO.eliminar(clienteSeleccionado.getId())) {
            mostrarMensaje("Cliente desactivado!", false);
            limpiarFormulario();
            cargarClientesDesdeDB();
        } else {
            mostrarMensaje("Error al desactivar cliente", true);
        }
    }

    private boolean validarFormulario() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombre.isEmpty()) {
            mostrarMensaje("Ingrese el nombre del cliente", true);
            return false;
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            mostrarMensaje("El nombre solo debe contener letras", true);
            return false;
        }
        if (telefono.isEmpty()) {
            mostrarMensaje("Ingrese el telefono", true);
            return false;
        }
        if (!telefono.matches("[0-9\\-\\+]+")) {
            mostrarMensaje("El telefono solo debe contener numeros", true);
            return false;
        }
        if (telefono.replaceAll("[^0-9]", "").length() < 7) {
            mostrarMensaje("El telefono debe tener al menos 7 digitos", true);
            return false;
        }
        if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            mostrarMensaje("Ingrese un email valido (ej: correo@ejemplo.com)", true);
            return false;
        }
        return true;
    }

    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        txtTelefono.clear();
        txtEmail.clear();
        cmbGenero.setValue(null);
        lblMensaje.setText("");
        clienteSeleccionado = null;
        tablaClientes.getSelectionModel().clearSelection();
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
    @FXML private void irDashboard() {
        cargarVista("/peluqueria/Vistas/Dashboard.fxml", "Sistema Peluqueria - Dashboard");
    }
    @FXML private void irCitas() {
        cargarVista("/peluqueria/Vistas/AgendarCita.fxml", "Sistema Peluqueria - Agendar Cita");
    }
    @FXML private void irClientes() { }
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

    // Clase interna para representar un cliente en la tabla
    public static class Cliente {
        private int id;
        private String nombre;
        private String telefono;
        private String email;
        private String genero;
        private String fechaRegistro;
        private String estado;

        public Cliente(int id, String nombre, String telefono, String email, String genero, String fechaRegistro, String estado) {
            this.id = id;
            this.nombre = nombre;
            this.telefono = telefono;
            this.email = email;
            this.genero = genero;
            this.fechaRegistro = fechaRegistro;
            this.estado = estado;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getTelefono() { return telefono; }
        public String getEmail() { return email; }
        public String getGenero() { return genero; }
        public String getFechaRegistro() { return fechaRegistro; }
        public String getEstado() { return estado; }
    }
}
