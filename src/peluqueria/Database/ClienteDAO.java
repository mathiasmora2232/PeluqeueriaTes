package peluqueria.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Obtener todos los clientes (para gestion - incluye inactivos)
    public static List<String[]> obtenerTodos() {
        List<String[]> clientes = new ArrayList<>();
        String query = "SELECT id, nombre, telefono, COALESCE(email, '') AS email, " +
                       "COALESCE(genero, '') AS genero, " +
                       "COALESCE(TO_CHAR(fecha_registro, 'DD/MM/YYYY'), '') AS fecha_registro, " +
                       "COALESCE(estado, 'Activo') AS estado " +
                       "FROM clientes ORDER BY nombre";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("telefono");
                fila[3] = rs.getString("email");
                fila[4] = rs.getString("genero");
                fila[5] = rs.getString("fecha_registro");
                fila[6] = rs.getString("estado");
                clientes.add(fila);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return clientes;
    }

    // Obtener solo clientes activos (para combos de citas, facturas, etc.)
    public static List<String[]> obtenerActivos() {
        List<String[]> clientes = new ArrayList<>();
        String query = "SELECT id, nombre, telefono, COALESCE(email, '') AS email " +
                       "FROM clientes WHERE estado = 'Activo' ORDER BY nombre";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("telefono");
                fila[3] = rs.getString("email");
                clientes.add(fila);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes activos: " + e.getMessage());
        }
        return clientes;
    }

    // Retorna: "ok", "email_duplicado", o "error"
    public static String crear(String nombre, String telefono, String email, String genero) {
        String query = "INSERT INTO clientes (nombre, telefono, email, genero) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, email.isEmpty() ? null : email);
            ps.setString(4, genero.isEmpty() ? null : genero);
            ps.executeUpdate();
            return "ok";
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) {
                return "email_duplicado";
            }
            System.err.println("Error al crear cliente: " + e.getMessage());
            return "error";
        }
    }

    // Retorna: "ok", "email_duplicado", o "error"
    public static String actualizar(int id, String nombre, String telefono, String email, String genero) {
        String query = "UPDATE clientes SET nombre = ?, telefono = ?, email = ?, genero = ? WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, email.isEmpty() ? null : email);
            ps.setString(4, genero.isEmpty() ? null : genero);
            ps.setInt(5, id);
            ps.executeUpdate();
            return "ok";
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) {
                return "email_duplicado";
            }
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return "error";
        }
    }

    // Soft delete - cambiar estado a Inactivo
    public static boolean eliminar(int id) {
        String query = "UPDATE clientes SET estado = 'Inactivo' WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al desactivar cliente: " + e.getMessage());
            return false;
        }
    }
}
