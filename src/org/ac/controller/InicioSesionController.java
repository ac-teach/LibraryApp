package org.ac.controller;

import java.io.IOException;
import org.ac.dao.UsuarioDAO;
import org.ac.dao.impl.UsuarioDAOImpl;
import org.ac.exception.DaoException;
import org.ac.exception.ValidacionException;
import org.ac.util.SecurityUtil;
import org.ac.model.Usuario;
import org.ac.system.Principal;
import org.ac.manager.SesionContext;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class InicioSesionController implements Initializable {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnIniciarSesion;
    @FXML
    private Label lblMensaje;

    private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO = new UsuarioDAOImpl();
        lblMensaje.setText("");
        //Deteccion de teclado: Enter en usuario o contrasena dispara el login,
        //igual que el boton INICIAR.
        txtUsuario.setOnAction(this::eventoInicioSesion);
        txtPassword.setOnAction(this::eventoInicioSesion);
    }

    @FXML
    public void eventoInicioSesion(ActionEvent evento) {
        try {
            ValidacionException.validarNoVacio(txtUsuario.getText(), "usuario");
            ValidacionException.validarNoVacio(txtPassword.getText(), "contraseña");
            String usuario = txtUsuario.getText();
            String password = txtPassword.getText();
            String passwordHash = SecurityUtil.hashSHA256(password);
            Usuario usuarioIniciado = usuarioDAO.iniciarSesion(usuario, passwordHash);

            if (usuarioIniciado != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Inicio correcto");
                abrirDashboard(usuarioIniciado);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Usuario o contraseña incorrectos");
            }
        } catch (ValidacionException e) {
            mostrarAlerta(Alert.AlertType.WARNING, e.getMessage());
            lblMensaje.setText(e.getMessage());
        } catch (DaoException e) {
            mostrarAlerta(Alert.AlertType.ERROR, e.getMessage());
            lblMensaje.setText("Error al iniciar sesión");
        }
    }

    @FXML
    public void eventoRegistrarse(ActionEvent evento) {
        try {
            Principal.cambiarEscena("/org/ac/view/fxml/RegistrarUsuarioView.fxml");
        } catch (IOException e) {
            System.err.println("Error al cargar registro: " + e.getMessage());
            lblMensaje.setText("Error interno");
        }
    }

    private void abrirDashboard(Usuario usuario) {
        SesionContext.getInstancia().setUsuarioActual(usuario);

        String rutaFXML = Principal.rutaDashboardSegunRol();
        if (rutaFXML.equals("/org/ac/view/fxml/InicioSesionView.fxml")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Rol desconocido: " + usuario.getRol());
            SesionContext.getInstancia().cerrarSesion();
            return;
        }
        try {
            Principal.cambiarEscena(rutaFXML);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista:" + rutaFXML + e.getMessage());
            lblMensaje.setText("Error interno");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.showAndWait();
    }
}
