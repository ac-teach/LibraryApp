package org.ac.controller;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ac.dao.UsuarioDAO;
import org.ac.dao.impl.UsuarioDAOImpl;
import org.ac.exception.DaoException;
import org.ac.exception.ValidacionException;
import org.ac.manager.SesionContext;
import org.ac.model.Usuario;
import org.ac.system.Principal;
import org.ac.util.SecurityUtil;

public class UsuarioController implements Initializable {

    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private ComboBox<String> cmbRol;
    @FXML
    private CheckBox chkActivo;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colUsername;
    @FXML
    private TableColumn colEmail;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colApellido;
    @FXML
    private TableColumn colRol;
    @FXML
    private TableColumn colActivo;
    @FXML
    private TableColumn colFecha;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnPrimero;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnUltimo;
    @FXML
    private Button btnCambiarPassword;
    @FXML
    private Button btnDesactivar;
    @FXML
    private Button btnEliminar;
    @FXML
    private TextField txtBuscar;

    private boolean modoEdicion = false;
    private Usuario enEdicion;
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private final FilteredList<Usuario> usuariosFiltrados = new FilteredList<>(listaUsuarios, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbRol.setItems(FXCollections.observableArrayList("admin", "empleado", "cajero"));
        cargarTabla();
        tablaUsuarios.setItems(usuariosFiltrados);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
        desactivarFormulario();
    }

    public void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<Usuario, Integer>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<Usuario, String>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<Usuario, String>("email"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Usuario, String>("firstName"));
        colApellido.setCellValueFactory(new PropertyValueFactory<Usuario, String>("lastName"));
        colRol.setCellValueFactory(new PropertyValueFactory<Usuario, String>("rol"));
        colActivo.setCellValueFactory(new PropertyValueFactory<Usuario, Boolean>("activo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<Usuario, Timestamp>("fechaCreacion"));
    }

    private void cargarTabla() {
        try {
            listaUsuarios.setAll(usuarioDAO.listarTodosUsuarios());
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarUsuarios());
    }

    private void filtrarUsuarios() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            usuariosFiltrados.setPredicate(p -> true);
        } else {
            usuariosFiltrados.setPredicate(usuario ->
                    String.valueOf(usuario.getId()).contains(busqueda)
                    || usuario.getUsername().toLowerCase().contains(busqueda)
                    || (usuario.getEmail() != null && usuario.getEmail().toLowerCase().contains(busqueda))
                    || usuario.getRol().toLowerCase().contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        mostrarEnFormulario(newSelection);
                        desactivarFormulario();
                    }
                });
    }

    private void mostrarEnFormulario(Usuario usuario) {
        txtUsername.setText(usuario.getUsername());
        txtEmail.setText(usuario.getEmail());
        txtNombre.setText(usuario.getFirstName());
        txtApellido.setText(usuario.getLastName());
        cmbRol.setValue(usuario.getRol());
        chkActivo.setSelected(usuario.isActivo());
        txtPassword.clear();
    }

    @FXML
    private void handleGuardar() {
        try {
            ValidacionException.validarNoVacio(txtUsername.getText(), "username");
            ValidacionException.validarNoVacio(txtEmail.getText(), "correo electrónico");
            ValidacionException.validarFormatoEmail(txtEmail.getText(), "El correo electrónico no es válido.");
            ValidacionException.validarNoNulo(cmbRol.getValue(), "Debe seleccionar un rol.");

            Usuario usuario = new Usuario();
            usuario.setId(modoEdicion ? enEdicion.getId() : 0);
            usuario.setUsername(txtUsername.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setFirstName(txtNombre.getText().trim());
            usuario.setLastName(txtApellido.getText().trim());
            usuario.setRol(cmbRol.getValue());
            usuario.setActivo(chkActivo.isSelected());

            boolean guardado;
            if (modoEdicion) {
                guardado = usuarioDAO.actualizarUsuario(usuario);
            } else {
                ValidacionException.validarNoVacio(txtPassword.getText(), "contraseña");
                ValidacionException.validarLongitudMinima(txtPassword.getText(), 6,
                        "La contraseña debe tener al menos 6 caracteres.");
                usuario.setPasswordHash(SecurityUtil.hashSHA256(txtPassword.getText()));
                guardado = usuarioDAO.crearUsuario(usuario);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Usuario actualizado exitosamente."
                        : "Usuario registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
                enEdicion = null;
            } else {
                mostrarError("No se pudo guardar el usuario.");
            }
        } catch (ValidacionException e) {
            mostrarAdvertencia(e.getMessage());
            lblMensaje.setText(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        limpiarFormulario();
        desactivarFormulario();
        activarNavegacion();
        modoEdicion = false;
        enEdicion = null;
        lblMensaje.setText("");
    }

    @FXML
    private void handleNuevo() {
        modoEdicion = false;
        enEdicion = null;
        limpiarFormulario();
        chkActivo.setSelected(true);
        cmbRol.setValue("empleado");
        activarFormulario();
        desactivarNavegacion();
        tablaUsuarios.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtUsername.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Usuario seleccion = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un usuario de la tabla para editar.");
            return;
        }
        modoEdicion = true;
        enEdicion = seleccion;
        mostrarEnFormulario(seleccion);
        activarFormulario();
        desactivarNavegacion();
        lblMensaje.setText("");
    }

    @FXML
    private void handleCambiarPassword() {
        Usuario seleccion = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un usuario para cambiar la contraseña.");
            return;
        }
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Cambiar Contraseña");
        dialogo.setHeaderText("Nueva contraseña para: " + seleccion.getUsername());
        dialogo.setContentText("Contraseña:");
        dialogo.showAndWait().ifPresent(password -> {
            try {
                ValidacionException.validarNoVacio(password, "contraseña");
                ValidacionException.validarLongitudMinima(password, 6,
                        "La contraseña debe tener al menos 6 caracteres.");
                String hash = SecurityUtil.hashSHA256(password);
                if (usuarioDAO.cambiarPassword(seleccion.getId(), hash)) {
                    lblMensaje.setText("Contraseña actualizada exitosamente.");
                } else {
                    mostrarError("No se pudo cambiar la contraseña.");
                }
            } catch (ValidacionException e) {
                mostrarAdvertencia(e.getMessage());
            } catch (DaoException e) {
                mostrarError(e.getMessage());
            }
        });
    }

    @FXML
    private void handleDesactivar() {
        Usuario seleccion = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un usuario para desactivar.");
            return;
        }
        if (esUsuarioActual(seleccion)) {
            mostrarError("No puede desactivar su propio usuario.");
            return;
        }
        if (!confirmar("Desactivar usuario", "¿Desea desactivar al usuario " + seleccion.getUsername() + "?")) {
            return;
        }
        try {
            if (usuarioDAO.desactivarUsuario(seleccion.getId())) {
                lblMensaje.setText("Usuario desactivado exitosamente.");
                cargarTabla();
            } else {
                mostrarError("No se pudo desactivar el usuario.");
            }
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        Usuario seleccion = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un usuario para eliminar.");
            return;
        }
        if (esUsuarioActual(seleccion)) {
            mostrarError("No puede eliminar su propio usuario.");
            return;
        }
        if (!confirmar("Eliminar usuario",
                "¿Desea eliminar definitivamente al usuario " + seleccion.getUsername() + "?")) {
            return;
        }
        try {
            if (usuarioDAO.eliminarUsuario(seleccion.getId())) {
                lblMensaje.setText("Usuario eliminado exitosamente.");
                cargarTabla();
            } else {
                mostrarError("No se pudo eliminar el usuario.");
            }
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private boolean esUsuarioActual(Usuario usuario) {
        Usuario actual = SesionContext.getInstancia().getUsuarioActual();
        return actual != null && actual.getId() == usuario.getId();
    }

    @FXML
    private void handlePrimero() {
        if (!tablaUsuarios.getItems().isEmpty()) {
            tablaUsuarios.getSelectionModel().selectFirst();
            tablaUsuarios.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaUsuarios.getItems().isEmpty()) {
            tablaUsuarios.getSelectionModel().selectPrevious();
            if (tablaUsuarios.getSelectionModel().getSelectedIndex() >= 0) {
                tablaUsuarios.scrollTo(tablaUsuarios.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaUsuarios.getItems().isEmpty()) {
            tablaUsuarios.getSelectionModel().selectNext();
            if (tablaUsuarios.getSelectionModel().getSelectedIndex() >= 0) {
                tablaUsuarios.scrollTo(tablaUsuarios.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaUsuarios.getItems().isEmpty()) {
            tablaUsuarios.getSelectionModel().selectLast();
            tablaUsuarios.scrollTo(tablaUsuarios.getItems().size() - 1);
        }
    }

    @FXML
    private void handleVolver() {
        try {
            Principal.cambiarEscena("/org/ac/view/fxml/AdminDashboradView.fxml");
        } catch (Exception e) {
            mostrarError("Error al volver al menú: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtUsername.clear();
        txtEmail.clear();
        txtNombre.clear();
        txtApellido.clear();
        cmbRol.setValue(null);
        chkActivo.setSelected(false);
        txtPassword.clear();
    }

    private void activarFormulario() {
        txtUsername.setDisable(false);
        txtEmail.setDisable(false);
        txtNombre.setDisable(false);
        txtApellido.setDisable(false);
        cmbRol.setDisable(false);
        chkActivo.setDisable(false);
        txtPassword.setDisable(modoEdicion);
    }

    private void desactivarFormulario() {
        txtUsername.setDisable(true);
        txtEmail.setDisable(true);
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        cmbRol.setDisable(true);
        chkActivo.setDisable(true);
        txtPassword.setDisable(true);
    }

    private void activarNavegacion() {
        tablaUsuarios.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        btnCambiarPassword.setDisable(false);
        btnDesactivar.setDisable(false);
        btnEliminar.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaUsuarios.setDisable(true);
        btnNuevo.setDisable(true);
        btnEditar.setDisable(true);
        btnPrimero.setDisable(true);
        btnAnterior.setDisable(true);
        btnSiguiente.setDisable(true);
        btnUltimo.setDisable(true);
        btnCambiarPassword.setDisable(true);
        btnDesactivar.setDisable(true);
        btnEliminar.setDisable(true);
        txtBuscar.setDisable(true);
    }

    private boolean confirmar(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
