package peluqueria.Controllers;

import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import peluqueria.Database.CitaDAO;
import peluqueria.Database.EstilistaDAO;
import peluqueria.Database.ServicioDAO;
import peluqueria.Models.Estilista;
import peluqueria.Models.Servicio;

public class AgendarCitaController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<Servicio> cmbServicio;
    @FXML private ComboBox<Estilista> cmbEstilista;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbHora;
    @FXML private Label lblMensaje;

    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, String> colCliente;
    @FXML private TableColumn<Cita, String> colServicio;
    @FXML private TableColumn<Cita, String> colEstilista;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colEstado;

    private ObservableList<Cita> listaCitas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();

        // Fecha minima: hoy
        dpFecha.setValue(LocalDate.now());

        // Configurar columnas de la tabla
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        colEstilista.setCellValueFactory(new PropertyValueFactory<>("estilista"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaCitas.setItems(listaCitas);

        // Cargar citas existentes desde la base de datos
        cargarCitasDesdeDB();
    }

    // Método para cargar servicios y estilistas
    private void cargarDatos() {
        cargarServicios();
        cargarEstilistas();
        cargarHorarios();
    }

    // Cargar servicios desde la base de datos
    private void cargarServicios() {
        List<Servicio> servicios = ServicioDAO.obtenerTodos();
        cmbServicio.setItems(FXCollections.observableArrayList(servicios));
    }

    // Cargar estilistas desde la base de datos
    private void cargarEstilistas() {
        List<Estilista> estilistas = EstilistaDAO.obtenerTodos();
        cmbEstilista.setItems(FXCollections.observableArrayList(estilistas));
        System.out.println("Estilistas cargados: " + estilistas.size());
    }

    // Cargar horarios disponibles
    private void cargarHorarios() {
        ObservableList<String> horarios = FXCollections.observableArrayList();
        for (int h = 9; h <= 18; h++) {
            horarios.add(String.format("%02d:00", h));
            if (h < 18) {
                horarios.add(String.format("%02d:30", h));
            }
        }
        cmbHora.setItems(horarios);
    }

    // Método público para refrescar estilistas y servicios
    @FXML
    public void refrescarDatos() {
        cargarDatos();
        mostrarMensaje("Datos recargados exitosamente", false);
    }

    @FXML
    private void agendarCita() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty()) {
            mostrarMensaje("Ingrese el nombre del cliente", true);
            return;
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            mostrarMensaje("El nombre solo debe contener letras", true);
            return;
        }
        if (telefono.isEmpty()) {
            mostrarMensaje("Ingrese el telefono", true);
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
        if (cmbServicio.getValue() == null) {
            mostrarMensaje("Seleccione un servicio", true);
            return;
        }
        if (cmbEstilista.getValue() == null) {
            mostrarMensaje("Seleccione un estilista", true);
            return;
        }
        if (dpFecha.getValue() == null) {
            mostrarMensaje("Seleccione una fecha", true);
            return;
        }
        if (cmbHora.getValue() == null) {
            mostrarMensaje("Seleccione una hora", true);
            return;
        }
        if (dpFecha.getValue().isBefore(LocalDate.now())) {
            mostrarMensaje("La fecha no puede ser anterior a hoy", true);
            return;
        }

        // Obtener o crear cliente en la BD
        int clienteId = CitaDAO.obtenerOCrearCliente(
            txtNombre.getText().trim(),
            txtTelefono.getText().trim(),
            ""
        );

        if (clienteId == -1) {
            mostrarMensaje("Error al registrar el cliente", true);
            return;
        }

        // Guardar cita en la base de datos
        boolean guardado = CitaDAO.crear(
            clienteId,
            cmbServicio.getValue().getId(),
            dpFecha.getValue(),
            cmbHora.getValue(),
            "Estilista: " + cmbEstilista.getValue().getNombre()
        );

        if (guardado) {
            mostrarMensaje("Cita agendada exitosamente!", false);
            limpiarFormulario();
            cargarCitasDesdeDB();
        } else {
            mostrarMensaje("Error al guardar la cita", true);
        }
    }

    // Cargar citas existentes desde la base de datos
    private void cargarCitasDesdeDB() {
        listaCitas.clear();
        List<String[]> citasDB = CitaDAO.obtenerTodas();
        for (String[] fila : citasDB) {
            listaCitas.add(new Cita(
                fila[1], // cliente
                fila[2], // servicio
                "",      // estilista (guardado en observaciones)
                fila[3], // fecha
                fila[4], // hora
                fila[5]  // estado
            ));
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        txtTelefono.clear();
        cmbServicio.setValue(null);
        cmbEstilista.setValue(null);
        dpFecha.setValue(LocalDate.now());
        cmbHora.setValue(null);
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

    @FXML private void irCitas() { }

    @FXML
    private void irClientes() {
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
            stage.setMaximized(true);
            stage.show();

            Stage actual = (Stage) txtNombre.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clase interna para representar una cita en la tabla
    public static class Cita {
        private String cliente;
        private String servicio;
        private String estilista;
        private String fecha;
        private String hora;
        private String estado;

        public Cita(String cliente, String servicio, String estilista, String fecha, String hora, String estado) {
            this.cliente = cliente;
            this.servicio = servicio;
            this.estilista = estilista;
            this.fecha = fecha;
            this.hora = hora;
            this.estado = estado;
        }

        public String getCliente() { return cliente; }
        public String getServicio() { return servicio; }
        public String getEstilista() { return estilista; }
        public String getFecha() { return fecha; }
        public String getHora() { return hora; }
        public String getEstado() { return estado; }
    }
}
