package peluqueria.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import peluqueria.Models.Usuario;

public class UsuarioDAO {

    // Validar usuario por username y password
    public static Usuario validarUsuario(String username, String password) {
        Usuario usuario = null;
        String query = "SELECT id, username, password, rol FROM usuarios WHERE username = ? AND password = ?";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("rol")
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
        }
        
        return usuario;
    }

    // Obtener usuario por username
    public static Usuario obtenerUsuarioPorUsername(String username) {
        Usuario usuario = null;
        String query = "SELECT id, username, password, rol FROM usuarios WHERE username = ?";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("rol")
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }
        
        return usuario;
    }

    // Crear nuevo usuario
    public static boolean crearUsuario(Usuario usuario) {
        String query = "INSERT INTO usuarios (username, password, rol) VALUES (?, ?, ?)";
        
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getRol());
            
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }
}
