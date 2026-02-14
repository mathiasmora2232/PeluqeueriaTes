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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button entrar;
    @FXML private Label statusLabel;

    public void initialize(){
        statusLabel.setText("");
    }

    // LOGIN NORMAL
    @FXML
    private void handleLogin(){

        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()){
            statusLabel.setText("Complete todos los campos");
            statusLabel.setStyle("-fx-text-fill:red;");
            return;
        }

        if(username.equals("admin") && password.equals("1234")){

            abrirSistema();

        }else{
            statusLabel.setText("Usuario incorrecto");
            statusLabel.setStyle("-fx-text-fill:red;");
        }
    }

    // BOTON BYPASS
    @FXML
    private void forcelogin(){
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
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();

        }catch(Exception e){
            e.printStackTrace();
            statusLabel.setText("Error al abrir el sistema");
        }
    }
}
