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
import org.ac.dao.CategoriaDAO;
import org.ac.dao.EditorialDAO;
import org.ac.dao.LibroDAO;
import org.ac.dao.impl.CategoriaDAOImpl;
import org.ac.dao.impl.EditorialDAOImpl;
import org.ac.dao.impl.LibroDAOImpl;
import org.ac.exception.ValidacionException;
import org.ac.model.Categoria;
import org.ac.model.Editorial;
import org.ac.model.Libro;
import org.ac.system.Principal;

public class LibroController implements Initializable {

    @FXML
    private TextField txtIsbn;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtFecha;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private ComboBox<Categoria> cmbCategoria;
    @FXML
    private ComboBox<Editorial> cmbEditorial;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<Libro> tablaLibros;
    @FXML
    private TableColumn colIsbn;
    @FXML
    private TableColumn colTitulo;
    @FXML
    private TableColumn colFecha;
    @FXML
    private TableColumn colPrecio;
    @FXML
    private TableColumn colStock;
    @FXML
    private TableColumn colIdCategoria;
    @FXML
    private TableColumn colNitEditorial;
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
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
    private final EditorialDAO editorialDAO = new EditorialDAOImpl();
    private final ObservableList<Libro> listaLibros = FXCollections.observableArrayList();
    private final FilteredList<Libro> librosFiltrados = new FilteredList<>(listaLibros, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTabla();
        cargarCombos();
        tablaLibros.setItems(librosFiltrados);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<Libro, String>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<Libro, String>("titulo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<Libro, String>("fechaPublicacion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<Libro, Double>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<Libro, Integer>("stock"));
        colIdCategoria.setCellValueFactory(new PropertyValueFactory<Libro, Integer>("idCategoria"));
        colNitEditorial.setCellValueFactory(new PropertyValueFactory<Libro, String>("nitEditorial"));
    }

    private void cargarTabla() {
        listaLibros.setAll(libroDAO.listarTodos());
    }

    private void cargarCombos() {
        cmbCategoria.setItems(FXCollections.observableArrayList(categoriaDAO.listarTodos()));
        cmbEditorial.setItems(FXCollections.observableArrayList(editorialDAO.listarTodos()));
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarLibros());
    }

    private void filtrarLibros() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            librosFiltrados.setPredicate(p -> true);
        } else {
            librosFiltrados.setPredicate(libro ->
                    libro.getIsbn().toLowerCase().contains(busqueda)
                    || libro.getTitulo().toLowerCase().contains(busqueda)
                    || libro.getFechaPublicacion().toLowerCase().contains(busqueda)
                    || String.valueOf(libro.getPrecio()).contains(busqueda)
                    || String.valueOf(libro.getStock()).contains(busqueda)
                    || String.valueOf(libro.getIdCategoria()).contains(busqueda)
                    || libro.getNitEditorial().toLowerCase().contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaLibros.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtIsbn.setText(newSelection.getIsbn());
                        txtTitulo.setText(newSelection.getTitulo());
                        txtFecha.setText(newSelection.getFechaPublicacion());
                        txtPrecio.setText(String.valueOf(newSelection.getPrecio()));
                        txtStock.setText(String.valueOf(newSelection.getStock()));
                        cmbCategoria.setValue(null);
                        for (Categoria categoria : cmbCategoria.getItems()) {
                            if (categoria.getIdCategoria() == newSelection.getIdCategoria()) {
                                cmbCategoria.setValue(categoria);
                                break;
                            }
                        }
                        cmbEditorial.setValue(null);
                        for (Editorial editorial : cmbEditorial.getItems()) {
                            if (editorial.getNit().equals(newSelection.getNitEditorial())) {
                                cmbEditorial.setValue(editorial);
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
            ValidacionException.validarNoVacio(txtIsbn.getText(), "ISBN");
            ValidacionException.validarNoVacio(txtTitulo.getText(), "título");
            ValidacionException.validarNoVacio(txtFecha.getText(), "fecha de publicación");
            ValidacionException.validarNoVacio(txtPrecio.getText(), "precio");
            ValidacionException.validarDecimal(txtPrecio.getText(), "precio");
            ValidacionException.validarNoVacio(txtStock.getText(), "stock");
            ValidacionException.validarNumero(txtStock.getText(), "stock");
            if (Integer.parseInt(txtStock.getText().trim()) < 0) {
                throw new ValidacionException("El campo stock no puede ser negativo.");
            }
            ValidacionException.validarFormatoFecha(txtFecha.getText(),
                    "La fecha de publicación debe tener formato YYYY-MM-DD.");
            ValidacionException.validarNoNulo(cmbCategoria.getValue(),
                    "Seleccione una categoría.");
            ValidacionException.validarNoNulo(cmbEditorial.getValue(),
                    "Seleccione una editorial.");

            Libro libro = new Libro(
                    txtIsbn.getText().trim(),
                    txtTitulo.getText().trim(),
                    txtFecha.getText().trim(),
                    Double.parseDouble(txtPrecio.getText().trim()),
                    cmbCategoria.getValue().getIdCategoria(),
                    cmbEditorial.getValue().getNit(),
                    Integer.parseInt(txtStock.getText().trim()));

            boolean guardado;
            if (modoEdicion) {
                guardado = libroDAO.actualizar(libro);
            } else {
                guardado = libroDAO.crear(libro);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Libro actualizado exitosamente."
                        : "Libro registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar el libro.");
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
        lblMensaje.setText("");
    }

    @FXML
    private void handleNuevo() {
        modoEdicion = false;
        limpiarFormulario();
        activarFormulario();
        desactivarNavegacion();
        tablaLibros.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtIsbn.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Libro seleccion = tablaLibros.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un libro de la tabla para editar.");
            return;
        }
        modoEdicion = true;
        activarFormulario();
        desactivarNavegacion();
        lblMensaje.setText("");
    }

    @FXML
    private void handlePrimero() {
        if (!tablaLibros.getItems().isEmpty()) {
            tablaLibros.getSelectionModel().selectFirst();
            tablaLibros.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaLibros.getItems().isEmpty()) {
            tablaLibros.getSelectionModel().selectPrevious();
            if (tablaLibros.getSelectionModel().getSelectedIndex() >= 0) {
                tablaLibros.scrollTo(tablaLibros.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaLibros.getItems().isEmpty()) {
            tablaLibros.getSelectionModel().selectNext();
            if (tablaLibros.getSelectionModel().getSelectedIndex() >= 0) {
                tablaLibros.scrollTo(tablaLibros.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaLibros.getItems().isEmpty()) {
            tablaLibros.getSelectionModel().selectLast();
            tablaLibros.scrollTo(tablaLibros.getItems().size() - 1);
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
        txtIsbn.clear();
        txtTitulo.clear();
        txtFecha.clear();
        txtPrecio.clear();
        txtStock.clear();
        cmbCategoria.setValue(null);
        cmbEditorial.setValue(null);
    }

    private void activarFormulario() {
        txtIsbn.setDisable(false);
        txtTitulo.setDisable(false);
        txtFecha.setDisable(false);
        txtPrecio.setDisable(false);
        txtStock.setDisable(false);
        cmbCategoria.setDisable(false);
        cmbEditorial.setDisable(false);
    }

    private void desactivarFormulario() {
        txtIsbn.setDisable(true);
        txtTitulo.setDisable(true);
        txtFecha.setDisable(true);
        txtPrecio.setDisable(true);
        txtStock.setDisable(true);
        cmbCategoria.setDisable(true);
        cmbEditorial.setDisable(true);
    }

    private void activarNavegacion() {
        tablaLibros.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaLibros.setDisable(true);
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
