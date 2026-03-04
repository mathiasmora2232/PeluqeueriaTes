package peluqueria.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import peluqueria.Models.FacturaDetalle;

public class FacturaDetalleDAO {

    // Crear detalle de factura
    public static boolean crear(int facturaId, int servicioId, int estilistaId,
                                java.math.BigDecimal precio, int cantidad, java.math.BigDecimal subtotal) {
        String query = "INSERT INTO factura_detalle (factura_id, servicio_id, estilista_id, precio, cantidad, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, facturaId);
            ps.setInt(2, servicioId);
            ps.setInt(3, estilistaId);
            ps.setBigDecimal(4, precio);
            ps.setInt(5, cantidad);
            ps.setBigDecimal(6, subtotal);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear detalle factura: " + e.getMessage());
            return false;
        }
    }

    // Obtener detalles de una factura con nombres de servicio y estilista
    public static List<FacturaDetalle> obtenerPorFactura(int facturaId) {
        List<FacturaDetalle> detalles = new ArrayList<>();
        String query = "SELECT fd.id, fd.factura_id, fd.servicio_id, s.nombre AS servicio, " +
                       "fd.estilista_id, COALESCE(e.nombre, '-') AS estilista, " +
                       "fd.precio, fd.cantidad, fd.subtotal " +
                       "FROM factura_detalle fd " +
                       "JOIN servicios s ON fd.servicio_id = s.id " +
                       "LEFT JOIN estilistas e ON fd.estilista_id = e.id_usuario " +
                       "WHERE fd.factura_id = ? ORDER BY fd.id";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, facturaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                detalles.add(new FacturaDetalle(
                    rs.getInt("id"),
                    rs.getInt("factura_id"),
                    rs.getInt("servicio_id"),
                    rs.getString("servicio"),
                    rs.getInt("estilista_id"),
                    rs.getString("estilista"),
                    rs.getBigDecimal("precio"),
                    rs.getInt("cantidad"),
                    rs.getBigDecimal("subtotal")
                ));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles factura: " + e.getMessage());
        }
        return detalles;
    }
}
