package org.ac.system;

import java.io.IOException;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;
import org.ac.manager.SesionContext;
import org.ac.model.Usuario;

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

    /**
     * Devuelve la ruta del dashboard correspondiente al rol del usuario con
     * sesion activa. Si no hay sesion o el rol es desconocido, devuelve la
     * ruta del login.
     */
    public static String rutaDashboardSegunRol() {
        Usuario usuario = SesionContext.getInstancia().getUsuarioActual();
        if (usuario == null || usuario.getRol() == null) {
            return "/org/ac/view/fxml/InicioSesionView.fxml";
        }
        switch (usuario.getRol().toLowerCase()) {
            case "admin":
                return "/org/ac/view/fxml/AdminDashboradView.fxml";
            case "empleado":
                return "/org/ac/view/fxml/EmpleadoView.fxml";
            case "cajero":
                return "/org/ac/view/fxml/CajeroView.fxml";
            default:
                return "/org/ac/view/fxml/InicioSesionView.fxml";
        }
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
