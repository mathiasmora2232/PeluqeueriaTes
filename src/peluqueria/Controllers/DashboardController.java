package peluqueria.Controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    @FXML private Label lblBienvenida;
    @FXML private Label lblFecha;
    @FXML private Label lblCitasHoy;
    @FXML private Label lblClientes;
    @FXML private Label lblServicios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mostrar fecha actual
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "EEEE dd 'de' MMMM, yyyy", new Locale("es", "ES")
        );
        String fecha = hoy.format(formatter);
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        lblFecha.setText(fecha);

        // Valores iniciales
        lblCitasHoy.setText("0");
        lblClientes.setText("0");
        lblServicios.setText("0");
    }

    // NAVEGACION
    @FXML private void irDashboard() { }

    @FXML
    private void irCitas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/peluqueria/Vistas/AgendarCita.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema Peluqueria - Agendar Cita");
            stage.setMaximized(true);
            stage.show();

            Stage actual = (Stage) lblBienvenida.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irClientes() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/peluqueria/Vistas/Clientes.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema Peluqueria - Clientes");
            stage.setMaximized(true);
            stage.show();

            Stage actual = (Stage) lblBienvenida.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void cargarVista(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.setMaximized(true);
            stage.show();
            Stage actual = (Stage) lblBienvenida.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CERRAR SESION
    @FXML
    private void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/peluqueria/Vistas/Login.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema Peluqueria - Login");
            stage.setMaximized(true);
            stage.show();

            Stage actual = (Stage) lblBienvenida.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
