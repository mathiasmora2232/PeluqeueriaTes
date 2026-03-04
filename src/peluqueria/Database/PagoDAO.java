package peluqueria.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import peluqueria.Models.Pago;

public class PagoDAO {

    // Crear pago
    public static boolean crear(int facturaId, String metodoPago, BigDecimal monto) {
        String query = "INSERT INTO pagos (factura_id, metodo_pago, monto) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, facturaId);
            ps.setString(2, metodoPago);
            ps.setBigDecimal(3, monto);
            ps.executeUpdate();
            System.out.println("Pago registrado para factura #" + facturaId);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear pago: " + e.getMessage());
            return false;
        }
    }

    // Obtener pagos de una factura
    public static List<Pago> obtenerPorFactura(int facturaId) {
        List<Pago> pagos = new ArrayList<>();
        String query = "SELECT id, factura_id, metodo_pago, monto, fecha FROM pagos WHERE factura_id = ? ORDER BY fecha";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, facturaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pagos.add(new Pago(
                    rs.getInt("id"),
                    rs.getInt("factura_id"),
                    rs.getString("metodo_pago"),
                    rs.getBigDecimal("monto"),
                    rs.getTimestamp("fecha")
                ));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener pagos: " + e.getMessage());
        }
        return pagos;
    }
}
