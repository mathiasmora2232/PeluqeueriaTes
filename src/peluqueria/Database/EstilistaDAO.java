package peluqueria.Database;

import peluqueria.Models.Estilista;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstilistaDAO {

    // Obtener todos los estilistas activos
    public static List<Estilista> obtenerTodos() {
        List<Estilista> estilistas = new ArrayList<>();
        String query = "SELECT id_usuario, nombre, telefono, especialidad, experiencia_anios, estado FROM estilistas WHERE estado = 'Activo' ORDER BY nombre";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Estilista estilista = new Estilista(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("especialidad"),
                    rs.getInt("experiencia_anios"),
                    rs.getString("estado")
                );
                estilistas.add(estilista);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener estilistas: " + e.getMessage());
        }
        
        return estilistas;
    }

    // Obtener estilista por ID
    public static Estilista obtenerPorId(int idUsuario) {
        Estilista estilista = null;
        String query = "SELECT id_usuario, nombre, telefono, especialidad, experiencia_anios, estado FROM estilistas WHERE id_usuario = ?";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                estilista = new Estilista(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("especialidad"),
                    rs.getInt("experiencia_anios"),
                    rs.getString("estado")
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener estilista: " + e.getMessage());
        }
        
        return estilista;
    }

    // Crear nuevo estilista
    public static boolean crear(Estilista estilista) {
        String query = "INSERT INTO estilistas (id_usuario, nombre, telefono, especialidad, experiencia_anios, estado) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, estilista.getIdUsuario());
            ps.setString(2, estilista.getNombre());
            ps.setString(3, estilista.getTelefono());
            ps.setString(4, estilista.getEspecialidad());
            ps.setInt(5, estilista.getExperienciaAnios());
            ps.setString(6, estilista.getEstado());
            
            ps.executeUpdate();
            System.out.println("Estilista creado exitosamente: " + estilista.getNombre());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear estilista: " + e.getMessage());
            return false;
        }
    }

    // Actualizar estilista
    public static boolean actualizar(Estilista estilista) {
        String query = "UPDATE estilistas SET nombre = ?, telefono = ?, especialidad = ?, experiencia_anios = ?, estado = ? WHERE id_usuario = ?";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, estilista.getNombre());
            ps.setString(2, estilista.getTelefono());
            ps.setString(3, estilista.getEspecialidad());
            ps.setInt(4, estilista.getExperienciaAnios());
            ps.setString(5, estilista.getEstado());
            ps.setInt(6, estilista.getIdUsuario());
            
            ps.executeUpdate();
            System.out.println("Estilista actualizado exitosamente: " + estilista.getNombre());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estilista: " + e.getMessage());
            return false;
        }
    }

    // Eliminar estilista (cambiar estado a Inactivo)
    public static boolean eliminar(int idUsuario) {
        String query = "UPDATE estilistas SET estado = 'Inactivo' WHERE id_usuario = ?";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            System.out.println("Estilista eliminado exitosamente");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar estilista: " + e.getMessage());
            return false;
        }
    }
}
