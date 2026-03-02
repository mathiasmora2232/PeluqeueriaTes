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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import peluqueria.Database.CitaDAO;
import peluqueria.Database.EstilistaDAO;
import peluqueria.Database.ServicioDAO;
import peluqueria.Models.Estilista;
import peluqueria.Models.Servicio;

public class Agendamiendo implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<Servicio> cmbServicio;
    @FXML private ComboBox<Estilista> cmbEstilista;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbHora;
    @FXML private Label lblMensaje;
    @FXML private Button btnAdmin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cargar servicios desde la base de datos
        List<Servicio> servicios = ServicioDAO.obtenerTodos();
        cmbServicio.setItems(FXCollections.observableArrayList(servicios));

        // Cargar estilistas desde la base de datos
        List<Estilista> estilistas = EstilistaDAO.obtenerTodos();
        cmbEstilista.setItems(FXCollections.observableArrayList(estilistas));

        // Cargar horas disponibles
        ObservableList<String> horarios = FXCollections.observableArrayList();
        for (int h = 9; h <= 18; h++) {
            horarios.add(String.format("%02d:00", h));
            if (h < 18) {
                horarios.add(String.format("%02d:30", h));
            }
        }
        cmbHora.setItems(horarios);

        // Configurar fecha mínima
        dpFecha.setValue(LocalDate.now());
        dpFecha.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    @FXML
    private void agendarCita() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        // Validaciones
        if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            mostrarMensaje("Por favor complete todos los datos personales", "error");
            return;
        }

        if (cmbServicio.getValue() == null || cmbEstilista.getValue() == null
                || dpFecha.getValue() == null || cmbHora.getValue() == null) {
            mostrarMensaje("Por favor seleccione todos los detalles de la cita", "error");
            return;
        }

        // Validar email básico
        if (!email.contains("@")) {
            mostrarMensaje("Ingrese un email válido", "error");
            return;
        }

        // Obtener o crear cliente en la BD
        int clienteId = CitaDAO.obtenerOCrearCliente(nombre, telefono, email);

        if (clienteId == -1) {
            mostrarMensaje("Error al registrar los datos del cliente", "error");
            return;
        }

        // Guardar cita en la base de datos
        Servicio servicioSeleccionado = cmbServicio.getValue();
        Estilista estilistaSeleccionado = cmbEstilista.getValue();

        boolean guardado = CitaDAO.crear(
            clienteId,
            servicioSeleccionado.getId(),
            dpFecha.getValue(),
            cmbHora.getValue(),
            "Estilista: " + estilistaSeleccionado.getNombre()
        );

        if (guardado) {
            mostrarMensaje("¡Cita agendada exitosamente! Nos contactaremos pronto para confirmar.", "success");
            limpiar();
        } else {
            mostrarMensaje("Error al agendar la cita. Intente nuevamente.", "error");
        }
    }

    @FXML
    private void limpiar() {
        txtNombre.clear();
        txtTelefono.clear();
        txtEmail.clear();
        cmbServicio.setValue(null);
        cmbEstilista.setValue(null);
        dpFecha.setValue(LocalDate.now());
        cmbHora.setValue(null);
        lblMensaje.setText("");
    }

    @FXML
    private void abrirLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/peluqueria/Vistas/Login.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema Peluquería - Admin Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al abrir login", "error");
        }
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().remove("lbl-error");
        lblMensaje.getStyleClass().remove("lbl-success");

        if (tipo.equals("success")) {
            lblMensaje.getStyleClass().add("lbl-success");
        } else if (tipo.equals("error")) {
            lblMensaje.getStyleClass().add("lbl-error");
        }
    }
}
