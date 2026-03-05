package peluqueria.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import peluqueria.Database.UsuarioDAO;
import peluqueria.Models.Usuario;

public class LoginController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private Button botonEntrar;
    @FXML private Button entrar;
    @FXML private Label etiquetaEstado;

    public void initialize(){
        etiquetaEstado.setText("");
    }

    // LOGIN NORMAL
    @FXML
    private void manejarEntrada(){

        String usuario = campoUsuario.getText();
        String contrasena = campoContrasena.getText();

        if(usuario.isEmpty() || contrasena.isEmpty()){
            etiquetaEstado.setText("Complete todos los campos");
            etiquetaEstado.setStyle("-fx-text-fill:red;");
            return;
        }

        // Validar usuario contra la base de datos
        Usuario usuarioValido = UsuarioDAO.validarUsuario(usuario, contrasena);

        if(usuarioValido != null && (usuarioValido.getRol().equals("admin") || usuarioValido.getRol().equals("barbero") || usuarioValido.getRol().equals("recepcion"))){
            abrirSistema();
        }else{
            etiquetaEstado.setText("Usuario o contrasena incorrecto");
            etiquetaEstado.setStyle("-fx-text-fill:red;");
        }
    }

    // BOTON BYPASS
    @FXML
    private void entradaDirecta(){
        abrirSistema();
    }

    // METODO QUE ABRE EL SISTEMA
    private void abrirSistema(){

        try{
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/peluqueria/Vistas/Dashboard.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema Peluqueria - Dashboard");
            stage.setMaximized(true);
            stage.show();

            // cerrar login
            Stage ventanaActual = (Stage) botonEntrar.getScene().getWindow();
            ventanaActual.close();

        }catch(Exception e){
            e.printStackTrace();
            etiquetaEstado.setText("Error al abrir el sistema");
        }
    }
}
