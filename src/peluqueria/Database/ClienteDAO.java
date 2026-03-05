package peluqueria.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public static List<String[]> obtenerTodos() {
        List<String[]> clientes = new ArrayList<>();
        String query = "SELECT id, nombre, telefono, COALESCE(email, '') AS email, " +
                       "COALESCE(direccion, '') AS direccion, COALESCE(notas, '') AS notas " +
                       "FROM clientes ORDER BY nombre";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[6];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("telefono");
                fila[3] = rs.getString("email");
                fila[4] = rs.getString("direccion");
                fila[5] = rs.getString("notas");
                clientes.add(fila);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return clientes;
    }

    // Retorna: "ok", "email_duplicado", o "error"
    public static String crear(String nombre, String telefono, String email, String direccion, String notas) {
        String query = "INSERT INTO clientes (nombre, telefono, email, direccion, notas) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, email.isEmpty() ? null : email);
            ps.setString(4, direccion);
            ps.setString(5, notas);
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
    public static String actualizar(int id, String nombre, String telefono, String email, String direccion, String notas) {
        String query = "UPDATE clientes SET nombre = ?, telefono = ?, email = ?, direccion = ?, notas = ? WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, email.isEmpty() ? null : email);
            ps.setString(4, direccion);
            ps.setString(5, notas);
            ps.setInt(6, id);
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

    public static boolean eliminar(int id) {
        String query = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}
