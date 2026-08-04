package org.ac.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ac.dao.AutorDAO;
import org.ac.dao.impl.AutorDAOImpl;
import org.ac.exception.ValidacionException;
import org.ac.model.Autor;
import org.ac.system.Principal;

public class AutorController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtNacionalidad;
    @FXML
    private TextArea txtBiografia;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<Autor> tablaAutores;
    @FXML
    private TableColumn colIdAutor;
    @FXML
    private TableColumn colNombreAutor;
    @FXML
    private TableColumn colApellidoAutor;
    @FXML
    private TableColumn colNacionalidad;
    @FXML
    private TableColumn colBiografia;
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
    private TextField txtBuscar;

    private boolean modoEdicion = false;
    private Autor enEdicion;
    private final AutorDAO autorDAO = new AutorDAOImpl();
    private final ObservableList<Autor> listaAutores = FXCollections.observableArrayList();
    private final FilteredList<Autor> autoresFiltrados = new FilteredList<>(listaAutores, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTabla();
        tablaAutores.setItems(autoresFiltrados);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colIdAutor.setCellValueFactory(new PropertyValueFactory<Autor, Integer>("idAutor"));
        colNombreAutor.setCellValueFactory(new PropertyValueFactory<Autor, String>("nombreAutor"));
        colApellidoAutor.setCellValueFactory(new PropertyValueFactory<Autor, String>("apellidoAutor"));
        colNacionalidad.setCellValueFactory(new PropertyValueFactory<Autor, String>("nacionalidad"));
        colBiografia.setCellValueFactory(new PropertyValueFactory<Autor, String>("biografia"));
    }

    private void cargarTabla() {
        listaAutores.setAll(autorDAO.listarTodos());
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarAutores());
    }

    private void filtrarAutores() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            autoresFiltrados.setPredicate(p -> true);
        } else {
            autoresFiltrados.setPredicate(autor ->
                    String.valueOf(autor.getIdAutor()).contains(busqueda)
                    || autor.getNombreAutor().toLowerCase().contains(busqueda)
                    || autor.getApellidoAutor().toLowerCase().contains(busqueda)
                    || autor.getNacionalidad().toLowerCase().contains(busqueda)
                    || autor.getBiografia().toLowerCase().contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaAutores.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtNombre.setText(newSelection.getNombreAutor());
                        txtApellido.setText(newSelection.getApellidoAutor());
                        txtNacionalidad.setText(newSelection.getNacionalidad());
                        txtBiografia.setText(newSelection.getBiografia());
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            ValidacionException.validarNoVacio(txtNombre.getText(), "nombre");
            ValidacionException.validarNoVacio(txtApellido.getText(), "apellido");
            ValidacionException.validarNoVacio(txtNacionalidad.getText(), "nacionalidad");

            Autor autor = new Autor(
                    modoEdicion ? enEdicion.getIdAutor() : 0,
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtNacionalidad.getText().trim(),
                    txtBiografia.getText().trim());

            boolean guardado;
            if (modoEdicion) {
                guardado = autorDAO.actualizar(autor);
            } else {
                guardado = autorDAO.crear(autor);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Autor actualizado exitosamente."
                        : "Autor registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar el autor.");
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
        activarFormulario();
        desactivarNavegacion();
        tablaAutores.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtNombre.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Autor seleccion = tablaAutores.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un autor de la tabla para editar.");
            return;
        }
        modoEdicion = true;
        enEdicion = seleccion;
        activarFormulario();
        desactivarNavegacion();
        lblMensaje.setText("");
    }

    @FXML
    private void handlePrimero() {
        if (!tablaAutores.getItems().isEmpty()) {
            tablaAutores.getSelectionModel().selectFirst();
            tablaAutores.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaAutores.getItems().isEmpty()) {
            tablaAutores.getSelectionModel().selectPrevious();
            if (tablaAutores.getSelectionModel().getSelectedIndex() >= 0) {
                tablaAutores.scrollTo(tablaAutores.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaAutores.getItems().isEmpty()) {
            tablaAutores.getSelectionModel().selectNext();
            if (tablaAutores.getSelectionModel().getSelectedIndex() >= 0) {
                tablaAutores.scrollTo(tablaAutores.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaAutores.getItems().isEmpty()) {
            tablaAutores.getSelectionModel().selectLast();
            tablaAutores.scrollTo(tablaAutores.getItems().size() - 1);
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
        txtNombre.clear();
        txtApellido.clear();
        txtNacionalidad.clear();
        txtBiografia.clear();
    }

    private void activarFormulario() {
        txtNombre.setDisable(false);
        txtApellido.setDisable(false);
        txtNacionalidad.setDisable(false);
        txtBiografia.setDisable(false);
    }

    private void desactivarFormulario() {
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtNacionalidad.setDisable(true);
        txtBiografia.setDisable(true);
    }

    private void activarNavegacion() {
        tablaAutores.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaAutores.setDisable(true);
        btnNuevo.setDisable(true);
        btnEditar.setDisable(true);
        btnPrimero.setDisable(true);
        btnAnterior.setDisable(true);
        btnSiguiente.setDisable(true);
        btnUltimo.setDisable(true);
        txtBuscar.setDisable(true);
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
