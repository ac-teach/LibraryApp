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

public class AdminDashboradController implements Initializable {

    @FXML private Label lblBienvenida;
    @FXML private Label lblRol;
    @FXML private Button btnCerrarSesion;
    @FXML private Circle avatarCircle;

    @FXML private Button btnUsuario;
    @FXML private Button btnLibro;
    @FXML private Button btnAutor;
    @FXML private Button btnCategoria;
    @FXML private Button btnEditorial;
    @FXML private Button btnVentas;
    @FXML private Button btnAutorLibro;
    @FXML private Button btnDetalleVenta;

    @FXML private VBox cardNuevoLibro;
    @FXML private VBox cardAgregarVenta;
    @FXML private VBox cardVerInventario;
    @FXML private VBox cardGestionarUsuarios;
    @FXML private VBox cardReportes;
    @FXML private VBox cardConfiguracion;

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
    public void irAUsuario(ActionEvent evento) {
        navegar("/org/ac/view/fxml/UsuarioView.fxml");
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
    public void irAVentas(ActionEvent evento) {
        navegar("/org/ac/view/fxml/ListaVentasView.fxml");
    }

    @FXML
    public void irAAutorLibro(ActionEvent evento) {
        navegar("/org/ac/view/fxml/AutorLibroView.fxml");
    }

    @FXML
    public void irADetalleVenta(ActionEvent evento) {
        navegar("/org/ac/view/fxml/DetalleVentaView.fxml");
    }

    @FXML
    public void irAClientes(ActionEvent evento) {
        try {
            Principal.cambiarEscena("/org/ac/view/fxml/ClienteView.fxml");
        } catch (IOException e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
            
        }
    }

    @FXML
    public void nuevoLibro(MouseEvent evento) {
        navegar("/org/ac/view/fxml/LibroFormView.fxml");
    }

    @FXML
    public void agregarVenta(MouseEvent evento) {
        navegar("/org/ac/view/fxml/VentaView.fxml");
    }

    @FXML
    public void verInventario(MouseEvent evento) {
        navegar("/org/ac/view/fxml/InventarioView.fxml");
    }

    @FXML
    public void gestionarUsuarios(MouseEvent evento) {
        navegar("/org/ac/view/fxml/GestionUsuariosView.fxml");
    }

    @FXML
    public void reportes(MouseEvent evento) {
        navegar("/org/ac/view/fxml/ReportesView.fxml");
    }

    @FXML
    public void configuracion(MouseEvent evento) {
        navegar("/org/ac/view/fxml/ConfiguracionView.fxml");
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

    public void iniciarUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        lblBienvenida.setText(usuario.getUsername());
        String iniciales = usuario.getUsername()
                .substring(0, Math.min(2, usuario.getUsername().length()))
                .toUpperCase();
        lblRol.setText(iniciales + " · " + capitalize(usuario.getRol()));
    }
}
