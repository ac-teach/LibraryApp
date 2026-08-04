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
import javafx.util.StringConverter;
import org.ac.dao.DetalleVentaDAO;
import org.ac.dao.LibroDAO;
import org.ac.dao.VentaDAO;
import org.ac.dao.impl.DetalleVentaDAOImpl;
import org.ac.dao.impl.LibroDAOImpl;
import org.ac.dao.impl.VentaDAOImpl;
import org.ac.exception.ValidacionException;
import org.ac.model.DetalleVenta;
import org.ac.model.Libro;
import org.ac.model.Venta;
import org.ac.system.Principal;

public class DetalleVentaController implements Initializable {

    @FXML
    private ComboBox<Venta> cmbVenta;
    @FXML
    private ComboBox<Libro> cmbLibro;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtPrecio;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<DetalleVenta> tablaDetalleVenta;
    @FXML
    private TableColumn colIdDetalleVenta;
    @FXML
    private TableColumn colNoVenta;
    @FXML
    private TableColumn colIsbn;
    @FXML
    private TableColumn colCantidad;
    @FXML
    private TableColumn colPrecio;
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
    private DetalleVenta enEdicion;
    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAOImpl();
    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ObservableList<DetalleVenta> listaDetalles = FXCollections.observableArrayList();
    private final FilteredList<DetalleVenta> detallesFiltrados = new FilteredList<>(listaDetalles, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTabla();
        cargarCombos();
        tablaDetalleVenta.setItems(detallesFiltrados);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colIdDetalleVenta.setCellValueFactory(new PropertyValueFactory<DetalleVenta, Integer>("idDetalleVenta"));
        colNoVenta.setCellValueFactory(new PropertyValueFactory<DetalleVenta, Integer>("noVenta"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<DetalleVenta, String>("isbn"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<DetalleVenta, Integer>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<DetalleVenta, Double>("precio"));
    }

    private void cargarTabla() {
        listaDetalles.setAll(detalleVentaDAO.listarTodos());
    }

    private void cargarCombos() {
        cmbVenta.setItems(FXCollections.observableArrayList(ventaDAO.listarTodos()));
        cmbVenta.setConverter(new StringConverter<Venta>() {
            @Override
            public String toString(Venta venta) {
                return venta == null ? "" : "Venta #" + venta.getNoVenta();
            }

            @Override
            public Venta fromString(String string) {
                return null;
            }
        });
        cmbLibro.setItems(FXCollections.observableArrayList(libroDAO.listarTodos()));
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarDetalles());
    }

    private void filtrarDetalles() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            detallesFiltrados.setPredicate(p -> true);
        } else {
            detallesFiltrados.setPredicate(detalle ->
                    String.valueOf(detalle.getIdDetalleVenta()).contains(busqueda)
                    || String.valueOf(detalle.getNoVenta()).contains(busqueda)
                    || detalle.getIsbn().toLowerCase().contains(busqueda)
                    || String.valueOf(detalle.getCantidad()).contains(busqueda)
                    || String.valueOf(detalle.getPrecio()).contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaDetalleVenta.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        cmbVenta.setValue(null);
                        for (Venta venta : cmbVenta.getItems()) {
                            if (venta.getNoVenta() == newSelection.getNoVenta()) {
                                cmbVenta.setValue(venta);
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
                        txtCantidad.setText(String.valueOf(newSelection.getCantidad()));
                        txtPrecio.setText(String.valueOf(newSelection.getPrecio()));
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            ValidacionException.validarNoNulo(cmbVenta.getValue(),
                    "Seleccione una venta.");
            ValidacionException.validarNoNulo(cmbLibro.getValue(),
                    "Seleccione un libro.");
            ValidacionException.validarNoVacio(txtCantidad.getText(), "cantidad");
            ValidacionException.validarPositivo(txtCantidad.getText(), "cantidad");
            ValidacionException.validarNoVacio(txtPrecio.getText(), "precio");
            ValidacionException.validarDecimal(txtPrecio.getText(), "precio");

            DetalleVenta detalle = new DetalleVenta(
                    modoEdicion ? enEdicion.getIdDetalleVenta() : 0,
                    cmbVenta.getValue().getNoVenta(),
                    cmbLibro.getValue().getIsbn(),
                    Integer.parseInt(txtCantidad.getText().trim()),
                    Double.parseDouble(txtPrecio.getText().trim()));

            boolean guardado;
            if (modoEdicion) {
                guardado = detalleVentaDAO.actualizar(detalle);
            } else {
                guardado = detalleVentaDAO.crear(detalle);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Detalle de venta actualizado exitosamente."
                        : "Detalle de venta registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar el detalle de venta.");
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
        tablaDetalleVenta.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        cmbVenta.requestFocus();
    }

    @FXML
    private void handleEditar() {
        DetalleVenta seleccion = tablaDetalleVenta.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un detalle de venta de la tabla para editar.");
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
        if (!tablaDetalleVenta.getItems().isEmpty()) {
            tablaDetalleVenta.getSelectionModel().selectFirst();
            tablaDetalleVenta.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaDetalleVenta.getItems().isEmpty()) {
            tablaDetalleVenta.getSelectionModel().selectPrevious();
            if (tablaDetalleVenta.getSelectionModel().getSelectedIndex() >= 0) {
                tablaDetalleVenta.scrollTo(tablaDetalleVenta.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaDetalleVenta.getItems().isEmpty()) {
            tablaDetalleVenta.getSelectionModel().selectNext();
            if (tablaDetalleVenta.getSelectionModel().getSelectedIndex() >= 0) {
                tablaDetalleVenta.scrollTo(tablaDetalleVenta.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaDetalleVenta.getItems().isEmpty()) {
            tablaDetalleVenta.getSelectionModel().selectLast();
            tablaDetalleVenta.scrollTo(tablaDetalleVenta.getItems().size() - 1);
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
        cmbVenta.setValue(null);
        cmbLibro.setValue(null);
        txtCantidad.clear();
        txtPrecio.clear();
    }

    private void activarFormulario() {
        cmbVenta.setDisable(false);
        cmbLibro.setDisable(false);
        txtCantidad.setDisable(false);
        txtPrecio.setDisable(false);
    }

    private void desactivarFormulario() {
        cmbVenta.setDisable(true);
        cmbLibro.setDisable(true);
        txtCantidad.setDisable(true);
        txtPrecio.setDisable(true);
    }

    private void activarNavegacion() {
        tablaDetalleVenta.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaDetalleVenta.setDisable(true);
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
