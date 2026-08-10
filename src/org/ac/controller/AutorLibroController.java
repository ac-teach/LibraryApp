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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ac.dao.AutorDAO;
import org.ac.dao.AutorLibroDAO;
import org.ac.dao.LibroDAO;
import org.ac.dao.impl.AutorDAOImpl;
import org.ac.dao.impl.AutorLibroDAOImpl;
import org.ac.dao.impl.LibroDAOImpl;
import org.ac.exception.DaoException;
import org.ac.exception.ValidacionException;
import org.ac.model.Autor;
import org.ac.model.AutorLibro;
import org.ac.model.Libro;
import org.ac.system.Principal;

public class AutorLibroController implements Initializable {

    @FXML
    private ComboBox<Autor> cmbAutor;
    @FXML
    private ComboBox<Libro> cmbLibro;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<AutorLibro> tablaAutoresLibro;
    @FXML
    private TableColumn colIdAutorLibro;
    @FXML
    private TableColumn colIdAutor;
    @FXML
    private TableColumn colIsbn;
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
    private AutorLibro enEdicion;
    private final AutorLibroDAO autorLibroDAO = new AutorLibroDAOImpl();
    private final AutorDAO autorDAO = new AutorDAOImpl();
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ObservableList<AutorLibro> listaAutoresLibro = FXCollections.observableArrayList();
    private final FilteredList<AutorLibro> autoresLibroFiltrados = new FilteredList<>(listaAutoresLibro, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTabla();
        cargarCombos();
        tablaAutoresLibro.setItems(autoresLibroFiltrados);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colIdAutorLibro.setCellValueFactory(new PropertyValueFactory<AutorLibro, Integer>("idAutorLibro"));
        colIdAutor.setCellValueFactory(new PropertyValueFactory<AutorLibro, Integer>("idAutor"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<AutorLibro, String>("isbn"));
    }

    private void cargarTabla() {
        try {
            listaAutoresLibro.setAll(autorLibroDAO.listarTodos());
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cargarCombos() {
        try {
            cmbAutor.setItems(FXCollections.observableArrayList(autorDAO.listarTodos()));
            cmbLibro.setItems(FXCollections.observableArrayList(libroDAO.listarTodos()));
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarAutoresLibro());
    }

    private void filtrarAutoresLibro() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            autoresLibroFiltrados.setPredicate(p -> true);
        } else {
            autoresLibroFiltrados.setPredicate(autorLibro ->
                    String.valueOf(autorLibro.getIdAutorLibro()).contains(busqueda)
                    || String.valueOf(autorLibro.getIdAutor()).contains(busqueda)
                    || autorLibro.getIsbn().toLowerCase().contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaAutoresLibro.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        cmbAutor.setValue(null);
                        for (Autor autor : cmbAutor.getItems()) {
                            if (autor.getIdAutor() == newSelection.getIdAutor()) {
                                cmbAutor.setValue(autor);
                                break;
                            }
                        }
                        cmbLibro.setValue(null);
                        for (Libro libro : cmbLibro.getItems()) {
                            if (libro.getIsbn().equals(newSelection.getIsbn())) {
                                cmbLibro.setValue(libro);
                                break;
                            }
                        }
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            ValidacionException.validarNoNulo(cmbAutor.getValue(),
                    "Seleccione un autor.");
            ValidacionException.validarNoNulo(cmbLibro.getValue(),
                    "Seleccione un libro.");

            AutorLibro autorLibro = new AutorLibro(
                    modoEdicion ? enEdicion.getIdAutorLibro() : 0,
                    cmbAutor.getValue().getIdAutor(),
                    cmbLibro.getValue().getIsbn());

            boolean guardado;
            if (modoEdicion) {
                guardado = autorLibroDAO.actualizar(autorLibro);
            } else {
                guardado = autorLibroDAO.crear(autorLibro);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Relación autor-libro actualizada exitosamente."
                        : "Relación autor-libro registrada exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar la relación autor-libro.");
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
        tablaAutoresLibro.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        cmbAutor.requestFocus();
    }

    @FXML
    private void handleEditar() {
        AutorLibro seleccion = tablaAutoresLibro.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione una relación autor-libro de la tabla para editar.");
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
        if (!tablaAutoresLibro.getItems().isEmpty()) {
            tablaAutoresLibro.getSelectionModel().selectFirst();
            tablaAutoresLibro.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaAutoresLibro.getItems().isEmpty()) {
            tablaAutoresLibro.getSelectionModel().selectPrevious();
            if (tablaAutoresLibro.getSelectionModel().getSelectedIndex() >= 0) {
                tablaAutoresLibro.scrollTo(tablaAutoresLibro.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaAutoresLibro.getItems().isEmpty()) {
            tablaAutoresLibro.getSelectionModel().selectNext();
            if (tablaAutoresLibro.getSelectionModel().getSelectedIndex() >= 0) {
                tablaAutoresLibro.scrollTo(tablaAutoresLibro.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaAutoresLibro.getItems().isEmpty()) {
            tablaAutoresLibro.getSelectionModel().selectLast();
            tablaAutoresLibro.scrollTo(tablaAutoresLibro.getItems().size() - 1);
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
        cmbAutor.setValue(null);
        cmbLibro.setValue(null);
    }

    private void activarFormulario() {
        cmbAutor.setDisable(false);
        cmbLibro.setDisable(false);
    }

    private void desactivarFormulario() {
        cmbAutor.setDisable(true);
        cmbLibro.setDisable(true);
    }

    private void activarNavegacion() {
        tablaAutoresLibro.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaAutoresLibro.setDisable(true);
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
