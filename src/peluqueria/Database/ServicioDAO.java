package peluqueria.Database;

import peluqueria.Models.Servicio;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    // Obtener todos los servicios
    public static List<Servicio> obtenerTodos() {
        List<Servicio> servicios = new ArrayList<>();
        String query = "SELECT id, nombre, precio, duracion_min, descripcion FROM servicios ORDER BY nombre";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Servicio servicio = new Servicio(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getBigDecimal("precio"),
                    rs.getInt("duracion_min"),
                    rs.getString("descripcion")
                );
                servicios.add(servicio);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener servicios: " + e.getMessage());
        }
        
        return servicios;
    }

    // Obtener servicio por ID
    public static Servicio obtenerPorId(int id) {
        Servicio servicio = null;
        String query = "SELECT id, nombre, precio, duracion_min, descripcion FROM servicios WHERE id = ?";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                servicio = new Servicio(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getBigDecimal("precio"),
                    rs.getInt("duracion_min"),
                    rs.getString("descripcion")
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener servicio: " + e.getMessage());
        }
        
        return servicio;
    }

    // Crear nuevo servicio
    public static boolean crear(Servicio servicio) {
        String query = "INSERT INTO servicios (nombre, precio, duracion_min, descripcion) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, servicio.getNombre());
            ps.setBigDecimal(2, servicio.getPrecio());
            ps.setInt(3, servicio.getDuracionMin());
            ps.setString(4, servicio.getDescripcion());
            
            ps.executeUpdate();
            System.out.println("Servicio creado exitosamente: " + servicio.getNombre());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear servicio: " + e.getMessage());
            return false;
        }
    }

    // Actualizar servicio
    public static boolean actualizar(Servicio servicio) {
        String query = "UPDATE servicios SET nombre = ?, precio = ?, duracion_min = ?, descripcion = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, servicio.getNombre());
            ps.setBigDecimal(2, servicio.getPrecio());
            ps.setInt(3, servicio.getDuracionMin());
            ps.setString(4, servicio.getDescripcion());
            ps.setInt(5, servicio.getId());
            
            ps.executeUpdate();
            System.out.println("Servicio actualizado exitosamente: " + servicio.getNombre());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar servicio: " + e.getMessage());
            return false;
        }
    }

    // Eliminar servicio
    public static boolean eliminar(int id) {
        String query = "DELETE FROM servicios WHERE id = ?";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Servicio eliminado exitosamente");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar servicio: " + e.getMessage());
            return false;
        }
    }
}
