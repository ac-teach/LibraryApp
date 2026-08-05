package org.ac.system;

import java.io.IOException;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class Principal extends Application {

    private static Stage escenarioPrincipal;
    private static final Logger log = Logger.getLogger(Principal.class.getName());

    public static void cambiarEscena(String rutaFXML) throws IOException {
        log.log(Level.INFO, "Se cambio de escena a: {0}", rutaFXML);
        Parent raiz = FXMLLoader.load(
                Principal.class.getResource(rutaFXML));
        Scene escena = new Scene(raiz);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        escenarioPrincipal.centerOnScreen();
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        log.info("Se inicio el programa");
        launch(args);

    }

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        Principal.escenarioPrincipal = escenarioPrincipal;
        cambiarEscena("/org/ac/view/fxml/InicioSesionView.fxml");
    }
}
