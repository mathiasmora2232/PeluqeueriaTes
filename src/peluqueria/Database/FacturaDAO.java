package peluqueria.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import peluqueria.Models.Factura;

public class FacturaDAO {

    // Crear factura y retornar su id (sin cita asociada)
    public static int crear(int clienteId, BigDecimal subtotal, BigDecimal impuesto, BigDecimal total) {
        return crear(clienteId, subtotal, impuesto, total, -1);
    }

    // Crear factura con cita asociada y retornar su id
    public static int crear(int clienteId, BigDecimal subtotal, BigDecimal impuesto, BigDecimal total, int citaId) {
        String query = citaId > 0
            ? "INSERT INTO facturas (cliente_id, subtotal, impuesto, total, estado, cita_id) VALUES (?, ?, ?, ?, 'PENDIENTE', ?) RETURNING id"
            : "INSERT INTO facturas (cliente_id, subtotal, impuesto, total, estado) VALUES (?, ?, ?, ?, 'PENDIENTE') RETURNING id";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, clienteId);
            ps.setBigDecimal(2, subtotal);
            ps.setBigDecimal(3, impuesto);
            ps.setBigDecimal(4, total);
            if (citaId > 0) {
                ps.setInt(5, citaId);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("Factura creada con id: " + id);
                return id;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al crear factura: " + e.getMessage());
        }
        return -1;
    }

    // Obtener todas las facturas con nombre de cliente
    public static List<Factura> obtenerTodas() {
        List<Factura> facturas = new ArrayList<>();
        String query = "SELECT f.id, f.cliente_id, cl.nombre AS cliente, f.fecha, " +
                       "f.subtotal, f.impuesto, f.total, f.estado " +
                       "FROM facturas f JOIN clientes cl ON f.cliente_id = cl.id " +
                       "ORDER BY f.fecha DESC";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                facturas.add(new Factura(
                    rs.getInt("id"),
                    rs.getInt("cliente_id"),
                    rs.getString("cliente"),
                    rs.getTimestamp("fecha"),
                    rs.getBigDecimal("subtotal"),
                    rs.getBigDecimal("impuesto"),
                    rs.getBigDecimal("total"),
                    rs.getString("estado")
                ));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener facturas: " + e.getMessage());
        }
        return facturas;
    }

    // Obtener facturas pendientes
    public static List<Factura> obtenerPendientes() {
        List<Factura> facturas = new ArrayList<>();
        String query = "SELECT f.id, f.cliente_id, cl.nombre AS cliente, f.fecha, " +
                       "f.subtotal, f.impuesto, f.total, f.estado " +
                       "FROM facturas f JOIN clientes cl ON f.cliente_id = cl.id " +
                       "WHERE f.estado = 'PENDIENTE' ORDER BY f.fecha DESC";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                facturas.add(new Factura(
                    rs.getInt("id"),
                    rs.getInt("cliente_id"),
                    rs.getString("cliente"),
                    rs.getTimestamp("fecha"),
                    rs.getBigDecimal("subtotal"),
                    rs.getBigDecimal("impuesto"),
                    rs.getBigDecimal("total"),
                    rs.getString("estado")
                ));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener facturas pendientes: " + e.getMessage());
        }
        return facturas;
    }

    // Marcar factura como PAGADA
    public static boolean marcarPagada(int facturaId) {
        String query = "UPDATE facturas SET estado = 'PAGADA' WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, facturaId);
            ps.executeUpdate();
            System.out.println("Factura #" + facturaId + " marcada como PAGADA");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al marcar factura como pagada: " + e.getMessage());
            return false;
        }
    }
}
