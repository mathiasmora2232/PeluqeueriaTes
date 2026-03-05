package peluqueria.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import peluqueria.Models.Usuario;

public class UsuarioDAO {

    // Obtener todos los usuarios
    public static List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT id, username, password, rol FROM usuarios ORDER BY username";

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("rol")
                ));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    // Validar usuario por username y password
    public static Usuario validarUsuario(String username, String password) {
        Usuario usuario = null;
        String query = "SELECT id, username, password, rol FROM usuarios WHERE username = ? AND password = ? AND COALESCE(estado, 'Activo') = 'Activo'";

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
            System.out.println("Usuario creado exitosamente: " + usuario.getUsername());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }

    // Actualizar usuario
    public static boolean actualizar(Usuario usuario) {
        String query = "UPDATE usuarios SET username = ?, password = ?, rol = ? WHERE id = ?";

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getRol());
            ps.setInt(4, usuario.getId());

            ps.executeUpdate();
            System.out.println("Usuario actualizado: " + usuario.getUsername());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // Soft delete - cambiar estado a Inactivo
    public static boolean eliminar(int id) {
        String query = "UPDATE usuarios SET estado = 'Inactivo' WHERE id = ?";

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Usuario eliminado");
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}
