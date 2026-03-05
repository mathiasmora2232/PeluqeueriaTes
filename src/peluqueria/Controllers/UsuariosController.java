package peluqueria.Controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import peluqueria.Database.UsuarioDAO;
import peluqueria.Models.Usuario;

public class UsuariosController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private ComboBox<String> cmbRol;
    @FXML private TextField txtBuscar;
    @FXML private Label lblMensaje;

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colPassword;
    @FXML private TableColumn<Usuario, String> colRol;

    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private FilteredList<Usuario> listaFiltrada;
    private Usuario usuarioSeleccionado = null;
    private boolean editandoContrasena = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        // Mostrar *** en vez de la contrasena real
        colPassword.setCellFactory(column -> new javafx.scene.control.TableCell<Usuario, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "***");
            }
        });
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        cmbRol.setItems(FXCollections.observableArrayList("admin", "barbero", "recepcion"));

        // Password desbloqueado por defecto (modo creacion)
        // Se bloquea solo al seleccionar un usuario de la tabla

        listaFiltrada = new FilteredList<>(listaUsuarios, p -> true);
        tablaUsuarios.setItems(listaFiltrada);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            listaFiltrada.setPredicate(u -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return u.getUsername().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        // Al seleccionar un usuario: bloquear password y mostrar ***
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                usuarioSeleccionado = newVal;
                txtUsername.setText(newVal.getUsername());
                txtPassword.setText("***");
                txtPassword.setEditable(false);
                txtPassword.setStyle("-fx-opacity: 0.6;");
                editandoContrasena = false;
                cmbRol.setValue(newVal.getRol());
            }
        });

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        listaUsuarios.clear();
        List<Usuario> usuarios = UsuarioDAO.obtenerTodos();
        listaUsuarios.addAll(usuarios);
    }

    @FXML
    private void guardarUsuario() {
        // Guardar nuevo solo funciona sin seleccion
        if (usuarioSeleccionado != null) {
            mostrarMensaje("Limpie el formulario primero para crear un nuevo usuario", true);
            return;
        }

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty()) {
            mostrarMensaje("Ingrese el username", true);
            return;
        }
        if (username.contains(" ")) {
            mostrarMensaje("El username no puede contener espacios", true);
            return;
        }
        if (username.length() < 3) {
            mostrarMensaje("El username debe tener al menos 3 caracteres", true);
            return;
        }
        if (password.isEmpty()) {
            mostrarMensaje("Ingrese el password", true);
            return;
        }
        if (password.length() < 4) {
            mostrarMensaje("El password debe tener al menos 4 caracteres", true);
            return;
        }
        if (cmbRol.getValue() == null) {
            mostrarMensaje("Seleccione un rol", true);
            return;
        }

        Usuario nuevo = new Usuario(username, password, cmbRol.getValue());

        if (UsuarioDAO.crearUsuario(nuevo)) {
            mostrarMensaje("Usuario creado exitosamente!", false);
            limpiarFormulario();
            cargarUsuarios();
        } else {
            mostrarMensaje("Error al crear usuario", true);
        }
    }

    @FXML
    private void habilitarEdicionPassword() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje("Seleccione un usuario primero", true);
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar edicion");
        alerta.setHeaderText("Cambiar contrasena");
        alerta.setContentText("Estas seguro de que quieres cambiar la contrasena de " + usuarioSeleccionado.getUsername() + "?");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            editandoContrasena = true;
            txtPassword.setEditable(true);
            txtPassword.setStyle("");
            txtPassword.setText("");
            txtPassword.setPromptText("Ingrese nueva contrasena");
            txtPassword.requestFocus();
            mostrarMensaje("Ingrese la nueva contrasena", false);
        }
    }

    @FXML
    private void editarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje("Seleccione un usuario de la tabla", true);
            return;
        }
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            mostrarMensaje("Complete el username", true);
            return;
        }
        if (username.contains(" ")) {
            mostrarMensaje("El username no puede contener espacios", true);
            return;
        }
        if (username.length() < 3) {
            mostrarMensaje("El username debe tener al menos 3 caracteres", true);
            return;
        }
        if (cmbRol.getValue() == null) {
            mostrarMensaje("Seleccione un rol", true);
            return;
        }

        usuarioSeleccionado.setUsername(username);
        usuarioSeleccionado.setRol(cmbRol.getValue());

        // Solo actualizar password si se habilito la edicion
        if (editandoContrasena) {
            String password = txtPassword.getText().trim();
            if (password.isEmpty()) {
                mostrarMensaje("Ingrese la nueva contrasena", true);
                return;
            }
            if (password.length() < 4) {
                mostrarMensaje("El password debe tener al menos 4 caracteres", true);
                return;
            }
            usuarioSeleccionado.setPassword(password);
        }

        if (UsuarioDAO.actualizar(usuarioSeleccionado)) {
            mostrarMensaje("Usuario actualizado!", false);
            limpiarFormulario();
            cargarUsuarios();
        } else {
            mostrarMensaje("Error al actualizar usuario", true);
        }
    }

    @FXML
    private void eliminarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje("Seleccione un usuario de la tabla", true);
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar desactivacion");
        alerta.setHeaderText("Desactivar usuario");
        alerta.setContentText("Desactivar a " + usuarioSeleccionado.getUsername() + "? No podra iniciar sesion.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (UsuarioDAO.eliminar(usuarioSeleccionado.getId())) {
                mostrarMensaje("Usuario desactivado!", false);
                limpiarFormulario();
                cargarUsuarios();
            } else {
                mostrarMensaje("Error al desactivar usuario", true);
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtUsername.clear();
        txtPassword.clear();
        txtPassword.setPromptText("Ingrese password");
        // Desbloquear password (modo creacion)
        txtPassword.setEditable(true);
        txtPassword.setStyle("");
        editandoContrasena = false;
        cmbRol.setValue(null);
        lblMensaje.setText("");
        usuarioSeleccionado = null;
        tablaUsuarios.getSelectionModel().clearSelection();
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
    @FXML private void irUsuarios() { }
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
            Stage actual = (Stage) txtUsername.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
