package peluqueria.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    // Buscar cliente por nombre y telefono, retorna su id o -1
    public static int buscarClienteId(String nombre, String telefono) {
        String query = "SELECT id FROM clientes WHERE nombre = ? AND telefono = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return -1;
    }

    // Crear cliente y retornar su id
    public static int crearCliente(String nombre, String telefono, String email) {
        String query = "INSERT INTO clientes (nombre, telefono, email) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, (email != null && !email.isEmpty()) ? email : null);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
        }
        return -1;
    }

    // Obtener o crear cliente, retorna id
    public static int obtenerOCrearCliente(String nombre, String telefono, String email) {
        int id = buscarClienteId(nombre, telefono);
        if (id == -1) {
            id = crearCliente(nombre, telefono, email != null ? email : "");
        }
        return id;
    }

    // Crear una nueva cita
    public static boolean crear(int clienteId, int servicioId, int estilistaId, LocalDate fecha, String hora, String observaciones) {
        String query = "INSERT INTO citas (cliente_id, servicio_id, estilista_id, fecha, hora, estado, observaciones) VALUES (?, ?, ?, ?, ?, 'Agendada', ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, clienteId);
            ps.setInt(2, servicioId);
            ps.setInt(3, estilistaId);
            ps.setDate(4, Date.valueOf(fecha));
            ps.setTime(5, Time.valueOf(LocalTime.parse(hora)));
            ps.setString(6, observaciones);
            ps.executeUpdate();
            System.out.println("Cita creada exitosamente");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear cita: " + e.getMessage());
            return false;
        }
    }

    // Obtener todas las citas con datos de cliente, servicio y estilista
    public static List<String[]> obtenerTodas() {
        List<String[]> citas = new ArrayList<>();
        String query = "SELECT c.id, cl.nombre AS cliente, s.nombre AS servicio, " +
                       "COALESCE(e.nombre, '') AS estilista, " +
                       "c.fecha, c.hora, c.estado " +
                       "FROM citas c " +
                       "JOIN clientes cl ON c.cliente_id = cl.id " +
                       "JOIN servicios s ON c.servicio_id = s.id " +
                       "LEFT JOIN estilistas e ON c.estilista_id = e.id_usuario " +
                       "ORDER BY c.fecha DESC, c.hora DESC";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("cliente");
                fila[2] = rs.getString("servicio");
                fila[3] = rs.getDate("fecha").toLocalDate().toString();
                fila[4] = rs.getTime("hora").toLocalTime().toString();
                fila[5] = rs.getString("estado");
                fila[6] = rs.getString("estilista");
                citas.add(fila);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener citas: " + e.getMessage());
        }
        return citas;
    }

    // Obtener citas completadas que aun no tienen factura asociada
    public static List<String[]> obtenerCompletadasSinFactura() {
        List<String[]> citas = new ArrayList<>();
        String query = "SELECT c.id, cl.nombre AS cliente, cl.telefono, s.nombre AS servicio, " +
                       "s.id AS servicio_id, s.precio, " +
                       "COALESCE(e.nombre, '') AS estilista, c.estilista_id, " +
                       "c.fecha, c.hora " +
                       "FROM citas c " +
                       "JOIN clientes cl ON c.cliente_id = cl.id " +
                       "JOIN servicios s ON c.servicio_id = s.id " +
                       "LEFT JOIN estilistas e ON c.estilista_id = e.id_usuario " +
                       "WHERE c.estado = 'Completada' " +
                       "AND c.id NOT IN (SELECT cita_id FROM facturas WHERE cita_id IS NOT NULL) " +
                       "ORDER BY c.fecha DESC, c.hora DESC";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[10];
                fila[0] = String.valueOf(rs.getInt("id"));           // cita_id
                fila[1] = rs.getString("cliente");                    // nombre cliente
                fila[2] = rs.getString("telefono");                   // telefono cliente
                fila[3] = rs.getString("servicio");                   // nombre servicio
                fila[4] = String.valueOf(rs.getInt("servicio_id"));   // servicio_id
                fila[5] = rs.getBigDecimal("precio").toString();      // precio servicio
                fila[6] = rs.getString("estilista");                  // nombre estilista
                fila[7] = String.valueOf(rs.getInt("estilista_id"));  // estilista_id
                fila[8] = rs.getDate("fecha").toLocalDate().toString();
                fila[9] = rs.getTime("hora").toLocalTime().toString();
                citas.add(fila);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener citas completadas: " + e.getMessage());
        }
        return citas;
    }

    // Actualizar estado de una cita
    public static boolean actualizarEstado(int citaId, String nuevoEstado) {
        String query = "UPDATE citas SET estado = ? WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, citaId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado cita: " + e.getMessage());
            return false;
        }
    }
}
