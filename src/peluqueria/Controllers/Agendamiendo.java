package peluqueria.Controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
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

public class PublicSchedulingController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cmbServicio;
    @FXML private ComboBox<String> cmbEstilista;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbHora;
    @FXML private Label lblMensaje;
    @FXML private Button btnAdmin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cargar servicios
        cmbServicio.setItems(FXCollections.observableArrayList(
            "Corte de Cabello",
            "Tinte",
            "Peinado",
            "Alisado",
            "Peinado con Ondas",
            "Tratamiento Capilar"
        ));

        // Cargar estilistas
        cmbEstilista.setItems(FXCollections.observableArrayList(
            "María",
            "Ana",
            "Laura",
            "Patricia",
            "Carolina"
        ));

        // Cargar horas disponibles
        cmbHora.setItems(FXCollections.observableArrayList(
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"
        ));

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
        String servicio = cmbServicio.getValue();
        String estilista = cmbEstilista.getValue();
        LocalDate fecha = dpFecha.getValue();
        String hora = cmbHora.getValue();

        // Validaciones
        if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            mostrarMensaje("Por favor complete todos los datos personales", "error");
            return;
        }

        if (servicio == null || estilista == null || fecha == null || hora == null) {
            mostrarMensaje("Por favor seleccione todos los detalles de la cita", "error");
            return;
        }

        // Validar email básico
        if (!email.contains("@")) {
            mostrarMensaje("Ingrese un email válido", "error");
            return;
        }

        // TODO: Guardar en base de datos
        mostrarMensaje("¡Cita agendada exitosamente! Nos contactaremos pronto para confirmar.", "success");
        limpiar();
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
