package org.ac.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.ac.model.Usuario;
import org.ac.system.Principal;
import org.ac.manager.SesionContext;

public class EmpleadoController implements Initializable {

    @FXML private Label lblBienvenida;
    @FXML private Label lblRol;
    @FXML private Button btnCerrarSesion;
    @FXML private Circle avatarCircle;

    @FXML private Button btnInventario;
    @FXML private Button btnLibro;
    @FXML private Button btnAutor;
    @FXML private Button btnCategoria;
    @FXML private Button btnEditorial;
    @FXML private Button btnClientes;

    @FXML private VBox cardVerInventario;
    @FXML private VBox cardNuevoLibro;
    @FXML private VBox cardNuevoAutor;
    @FXML private VBox cardNuevaCategoria;
    @FXML private VBox cardNuevaEditorial;
    @FXML private VBox cardNuevoCliente;

    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioActual = SesionContext.getInstancia().getUsuarioActual();
        if (usuarioActual != null) {
            lblBienvenida.setText(usuarioActual.getUsername());
            String iniciales = usuarioActual.getUsername()
                    .substring(0, Math.min(2, usuarioActual.getUsername().length()))
                    .toUpperCase();
            lblRol.setText(iniciales + " · " + capitalize(usuarioActual.getRol()));
        } else {
            lblBienvenida.setText("Invitado");
            lblRol.setText("?? · Sin sesión");
        }
    }

    private String capitalize(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    @FXML
    public void cerrarSesion(ActionEvent evento) {
        SesionContext.getInstancia().cerrarSesion();
        navegar("/org/ac/view/fxml/InicioSesionView.fxml");
    }

    @FXML
    public void irAInventario(ActionEvent evento) {
        navegar("/org/ac/view/fxml/InventarioView.fxml");
    }

    @FXML
    public void irALibro(ActionEvent evento) {
        navegar("/org/ac/view/fxml/LibroView.fxml");
    }

    @FXML
    public void irAAutor(ActionEvent evento) {
        navegar("/org/ac/view/fxml/AutorView.fxml");
    }

    @FXML
    public void irACategoria(ActionEvent evento) {
        navegar("/org/ac/view/fxml/CategoriaView.fxml");
    }

    @FXML
    public void irAEditorial(ActionEvent evento) {
        navegar("/org/ac/view/fxml/EditorialView.fxml");
    }

    @FXML
    public void irAClientes(ActionEvent evento) {
        navegar("/org/ac/view/fxml/ClienteView.fxml");
    }

    @FXML
    public void verInventario(MouseEvent evento) {
        navegar("/org/ac/view/fxml/InventarioView.fxml");
    }

    @FXML
    public void nuevoLibro(MouseEvent evento) {
        navegar("/org/ac/view/fxml/LibroView.fxml");
    }

    @FXML
    public void nuevoAutor(MouseEvent evento) {
        navegar("/org/ac/view/fxml/AutorView.fxml");
    }

    @FXML
    public void nuevaCategoria(MouseEvent evento) {
        navegar("/org/ac/view/fxml/CategoriaView.fxml");
    }

    @FXML
    public void nuevaEditorial(MouseEvent evento) {
        navegar("/org/ac/view/fxml/EditorialView.fxml");
    }

    @FXML
    public void nuevoCliente(MouseEvent evento) {
        navegar("/org/ac/view/fxml/ClienteView.fxml");
    }

    private void navegar(String ruta) {
        try {
            Principal.cambiarEscena(ruta);
        } catch (IOException | NullPointerException e) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION,
                    "Esta sección estará disponible próximamente.", ButtonType.OK);
            alerta.setTitle("En construcción");
            alerta.setHeaderText(null);
            alerta.showAndWait();
        }
    }
}
