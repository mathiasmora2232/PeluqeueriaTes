package peluqueria.Controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

public class AgendarCitaController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cmbServicio;
    @FXML private ComboBox<String> cmbEstilista;
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
        // Cargar servicios
        cmbServicio.setItems(FXCollections.observableArrayList(
            "Corte de Cabello",
            "Tinte",
            "Peinado",
            "Alisado",
            "Manicure",
            "Pedicure",
            "Tratamiento Capilar",
            "Barba"
        ));

        // Cargar estilistas
        cmbEstilista.setItems(FXCollections.observableArrayList(
            "Ana Garcia",
            "Carlos Mendez",
            "Laura Torres",
            "Pedro Ruiz"
        ));

        // Cargar horarios disponibles (9:00 AM - 6:00 PM)
        ObservableList<String> horarios = FXCollections.observableArrayList();
        for (int h = 9; h <= 18; h++) {
            horarios.add(String.format("%02d:00", h));
            if (h < 18) {
                horarios.add(String.format("%02d:30", h));
            }
        }
        cmbHora.setItems(horarios);

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
    }

    @FXML
    private void agendarCita() {
        // Validar campos
        if (txtNombre.getText().isEmpty()) {
            mostrarMensaje("Ingrese el nombre del cliente", true);
            return;
        }
        if (txtTelefono.getText().isEmpty()) {
            mostrarMensaje("Ingrese el telefono", true);
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

        // Crear cita y agregar a la tabla
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Cita nuevaCita = new Cita(
            txtNombre.getText(),
            cmbServicio.getValue(),
            cmbEstilista.getValue(),
            dpFecha.getValue().format(fmt),
            cmbHora.getValue(),
            "Pendiente"
        );

        listaCitas.add(nuevaCita);
        mostrarMensaje("Cita agendada exitosamente!", false);
        limpiarFormulario();
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
