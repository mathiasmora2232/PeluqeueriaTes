package peluqueria.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:postgresql://localhost:5432/peluqueria";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "Lu246988";

    private static Connection conexion = null;

    // Obtener conexion (singleton)
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("org.postgresql.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("Conexion a PostgreSQL exitosa");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar a PostgreSQL: " + e.getMessage());
        }
        return conexion;
    }

    // Cerrar conexion
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexion cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexion: " + e.getMessage());
        }
    }

    // Probar conexion
    public static boolean probarConexion() {
        Connection conn = getConexion();
        return conn != null;
    }
}
