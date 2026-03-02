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
            ps.setString(3, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                conn.commit();
                return id;
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
    public static boolean crear(int clienteId, int servicioId, LocalDate fecha, String hora, String observaciones) {
        String query = "INSERT INTO citas (cliente_id, servicio_id, fecha, hora, estado, observaciones) VALUES (?, ?, ?, ?, 'Agendada', ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, clienteId);
            ps.setInt(2, servicioId);
            ps.setDate(3, Date.valueOf(fecha));
            ps.setTime(4, Time.valueOf(LocalTime.parse(hora)));
            ps.setString(5, observaciones);
            ps.executeUpdate();
            conn.commit();
            System.out.println("Cita creada exitosamente");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear cita: " + e.getMessage());
            return false;
        }
    }

    // Obtener todas las citas con datos de cliente y servicio (para la tabla)
    public static List<String[]> obtenerTodas() {
        List<String[]> citas = new ArrayList<>();
        String query = "SELECT c.id, cl.nombre AS cliente, s.nombre AS servicio, " +
                       "c.fecha, c.hora, c.estado " +
                       "FROM citas c " +
                       "JOIN clientes cl ON c.cliente_id = cl.id " +
                       "JOIN servicios s ON c.servicio_id = s.id " +
                       "ORDER BY c.fecha DESC, c.hora DESC";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[6];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("cliente");
                fila[2] = rs.getString("servicio");
                fila[3] = rs.getDate("fecha").toLocalDate().toString();
                fila[4] = rs.getTime("hora").toLocalTime().toString();
                fila[5] = rs.getString("estado");
                citas.add(fila);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener citas: " + e.getMessage());
        }
        return citas;
    }
}
