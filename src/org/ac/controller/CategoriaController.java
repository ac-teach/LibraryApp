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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ac.dao.CategoriaDAO;
import org.ac.dao.impl.CategoriaDAOImpl;
import org.ac.exception.DaoException;
import org.ac.exception.ValidacionException;
import org.ac.model.Categoria;
import org.ac.system.Principal;

public class CategoriaController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<Categoria> tablaCategorias;
    @FXML
    private TableColumn colIdCategoria;
    @FXML
    private TableColumn colNombreCategoria;
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
    private Categoria enEdicion;
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private final FilteredList<Categoria> categoriasFiltradas = new FilteredList<>(listaCategorias, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTabla();
        tablaCategorias.setItems(categoriasFiltradas);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colIdCategoria.setCellValueFactory(new PropertyValueFactory<Categoria, Integer>("idCategoria"));
        colNombreCategoria.setCellValueFactory(new PropertyValueFactory<Categoria, String>("nombreCategoria"));
    }

    private void cargarTabla() {
        try {
            listaCategorias.setAll(categoriaDAO.listarTodos());
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarCategorias());
    }

    private void filtrarCategorias() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            categoriasFiltradas.setPredicate(p -> true);
        } else {
            categoriasFiltradas.setPredicate(categoria ->
                    String.valueOf(categoria.getIdCategoria()).contains(busqueda)
                    || categoria.getNombreCategoria().toLowerCase().contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaCategorias.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtNombre.setText(newSelection.getNombreCategoria());
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            ValidacionException.validarNoVacio(txtNombre.getText(), "nombre de la categoría");

            Categoria categoria = new Categoria(
                    modoEdicion ? enEdicion.getIdCategoria() : 0,
                    txtNombre.getText().trim());

            boolean guardado;
            if (modoEdicion) {
                guardado = categoriaDAO.actualizar(categoria);
            } else {
                guardado = categoriaDAO.crear(categoria);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Categoría actualizada exitosamente."
                        : "Categoría registrada exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar la categoría.");
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
        tablaCategorias.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtNombre.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Categoria seleccion = tablaCategorias.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione una categoría de la tabla para editar.");
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
        if (!tablaCategorias.getItems().isEmpty()) {
            tablaCategorias.getSelectionModel().selectFirst();
            tablaCategorias.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaCategorias.getItems().isEmpty()) {
            tablaCategorias.getSelectionModel().selectPrevious();
            if (tablaCategorias.getSelectionModel().getSelectedIndex() >= 0) {
                tablaCategorias.scrollTo(tablaCategorias.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaCategorias.getItems().isEmpty()) {
            tablaCategorias.getSelectionModel().selectNext();
            if (tablaCategorias.getSelectionModel().getSelectedIndex() >= 0) {
                tablaCategorias.scrollTo(tablaCategorias.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaCategorias.getItems().isEmpty()) {
            tablaCategorias.getSelectionModel().selectLast();
            tablaCategorias.scrollTo(tablaCategorias.getItems().size() - 1);
        }
    }

    @FXML
    private void handleVolver() {
        try {
            Principal.cambiarEscena(Principal.rutaDashboardSegunRol());
        } catch (Exception e) {
            mostrarError("Error al volver al menú: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
    }

    private void activarFormulario() {
        txtNombre.setDisable(false);
    }

    private void desactivarFormulario() {
        txtNombre.setDisable(true);
    }

    private void activarNavegacion() {
        tablaCategorias.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaCategorias.setDisable(true);
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
