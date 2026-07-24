package org.ac.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Alvaro Calderon
 */
public class Principal extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        //convertir .fxml en nodo raiz
        Parent raiz = FXMLLoader.load(
                getClass().getResource("/org/ac/view/InicioSesionView.fxml"));        
        Scene escena = new Scene(raiz);
        
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }    
}
