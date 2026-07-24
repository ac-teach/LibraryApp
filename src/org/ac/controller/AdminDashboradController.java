package org.ac.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.ac.model.Usuario;

public class AdminDashboradController implements Initializable {

    @FXML private Label lblBienvenida;
    private Usuario usuarioActual;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void iniciarUsuario(Usuario usuario){
        this.usuarioActual = usuario;
        lblBienvenida.setText("Bienvenido administrador " + usuario.getUsername());
        //instrucciones
    }
    
    
}
