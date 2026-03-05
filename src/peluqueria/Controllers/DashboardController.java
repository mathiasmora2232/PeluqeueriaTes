package peluqueria.Controllers;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import peluqueria.Database.Conexion;

public class DashboardController implements Initializable {

    @FXML private Label lblBienvenida;
    @FXML private Label lblFecha;
    @FXML private Label lblCitasHoy;
    @FXML private Label lblClientes;
    @FXML private Label lblServicios;
    @FXML private Label lblEstilistas;
    @FXML private Label lblFacturasPendientes;
    @FXML private Label lblIngresosMes;

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

        // Cargar conteos desde la base de datos
        lblCitasHoy.setText(String.valueOf(contarCitasHoy()));
        lblClientes.setText(String.valueOf(contarRegistros("clientes")));
        lblServicios.setText(String.valueOf(contarRegistros("servicios")));
        lblEstilistas.setText(String.valueOf(contarRegistros("estilistas")));
        lblFacturasPendientes.setText(String.valueOf(contarFacturasPendientes()));
        lblIngresosMes.setText("$" + obtenerIngresosMes().toString());
    }

    private int contarCitasHoy() {
        String query = "SELECT COUNT(*) FROM citas WHERE fecha = CURRENT_DATE";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
        } catch (Exception e) {
            System.err.println("Error al contar citas de hoy: " + e.getMessage());
        }
        return 0;
    }

    private int contarRegistros(String tabla) {
        String query = "SELECT COUNT(*) FROM " + tabla;
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
        } catch (Exception e) {
            System.err.println("Error al contar " + tabla + ": " + e.getMessage());
        }
        return 0;
    }

    private int contarFacturasPendientes() {
        String query = "SELECT COUNT(*) FROM facturas WHERE estado = 'PENDIENTE'";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
        } catch (Exception e) {
            System.err.println("Error al contar facturas pendientes: " + e.getMessage());
        }
        return 0;
    }

    private BigDecimal obtenerIngresosMes() {
        String query = "SELECT COALESCE(SUM(total), 0) FROM facturas WHERE estado = 'PAGADA' AND EXTRACT(MONTH FROM fecha) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM fecha) = EXTRACT(YEAR FROM CURRENT_DATE)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            rs.close();
        } catch (Exception e) {
            System.err.println("Error al obtener ingresos del mes: " + e.getMessage());
        }
        return BigDecimal.ZERO;
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
    @FXML private void irFacturacion() {
        cargarVista("/peluqueria/Vistas/Facturacion.fxml", "Sistema Peluqueria - Facturacion");
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
