package org.ac.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.ac.dao.ClienteDAO;
import org.ac.dao.UsuarioDAO;
import org.ac.dao.VentaDAO;
import org.ac.dao.impl.ClienteDAOImpl;
import org.ac.dao.impl.UsuarioDAOImpl;
import org.ac.dao.impl.VentaDAOImpl;
import org.ac.exception.DaoException;
import org.ac.exception.ValidacionException;
import org.ac.manager.SesionContext;
import org.ac.model.Cliente;
import org.ac.model.Usuario;
import org.ac.model.Venta;
import org.ac.system.Principal;

public class ListaVentasController implements Initializable {

    @FXML
    private TextField txtTotal;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<Usuario> cmbUsuario;
    @FXML
    private ComboBox<Cliente> cmbCliente;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<Venta> tablaVentas;
    @FXML
    private TableColumn colNoVenta;
    @FXML
    private TableColumn colFechaVenta;
    @FXML
    private TableColumn colTotalVenta;
    @FXML
    private TableColumn colCuiCliente;
    @FXML
    private TableColumn colUsuario;
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
    private Venta enEdicion;
    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();
    private final FilteredList<Venta> ventasFiltradas = new FilteredList<>(listaVentas, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTabla();
        cargarClientes();
        cargarUsuarios();
        tablaVentas.setItems(ventasFiltradas);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colNoVenta.setCellValueFactory(new PropertyValueFactory<Venta, Integer>("noVenta"));
        colFechaVenta.setCellValueFactory(new PropertyValueFactory<Venta, String>("fechaVenta"));
        colTotalVenta.setCellValueFactory(new PropertyValueFactory<Venta, Double>("totalVenta"));
        colCuiCliente.setCellValueFactory(new PropertyValueFactory<Venta, Long>("cuiCliente"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<Venta, Integer>("idUsuario"));
    }

    private void cargarTabla() {
        try {
            listaVentas.setAll(ventaDAO.listarTodos());
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cargarClientes() {
        try {
            cmbCliente.setItems(FXCollections.observableArrayList(clienteDAO.listarTodos()));
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cargarUsuarios() {
        cmbUsuario.setConverter(new StringConverter<Usuario>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario == null ? "" : usuario.getId() + " - " + usuario.getUsername();
            }

            @Override
            public Usuario fromString(String string) {
                return null;
            }
        });
        try {
            cmbUsuario.setItems(FXCollections.observableArrayList(usuarioDAO.listarTodosUsuarios()));
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarVentas());
    }

    private void filtrarVentas() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            ventasFiltradas.setPredicate(p -> true);
        } else {
            ventasFiltradas.setPredicate(venta ->
                    String.valueOf(venta.getNoVenta()).contains(busqueda)
                    || venta.getFechaVenta().toLowerCase().contains(busqueda)
                    || String.valueOf(venta.getTotalVenta()).contains(busqueda)
                    || String.valueOf(venta.getCuiCliente()).contains(busqueda)
                    || String.valueOf(venta.getIdUsuario()).contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaVentas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtTotal.setText(String.valueOf(newSelection.getTotalVenta()));
                        cmbCliente.setValue(null);
                        for (Cliente cliente : cmbCliente.getItems()) {
                            if (cliente.getCui() == newSelection.getCuiCliente()) {
                                cmbCliente.setValue(cliente);
                                break;
                            }
                        }
                        desactivarFormulario();
                        String fecha = newSelection.getFechaVenta();
                        if (fecha != null && !fecha.isEmpty()) {
                            try {
                                dpFecha.setValue(LocalDate.parse(fecha.substring(0, 10)));
                            } catch (Exception e) {
                                dpFecha.setValue(null);
                            }
                        } else {
                            dpFecha.setValue(null);
                        }
                        cmbUsuario.setValue(null);
                        for (Usuario usuario : cmbUsuario.getItems()) {
                            if (usuario.getId() == newSelection.getIdUsuario()) {
                                cmbUsuario.setValue(usuario);
                                break;
                            }
                        }
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            ValidacionException.validarNoVacio(txtTotal.getText(), "total");
            ValidacionException.validarDecimal(txtTotal.getText(), "total");
            ValidacionException.validarNoNulo(cmbCliente.getValue(),
                    "Seleccione un cliente.");
            ValidacionException.validarNoNulo(cmbUsuario.getValue(), "Seleccione el usuario que atendió.");

            Venta venta = new Venta(
                    modoEdicion ? enEdicion.getNoVenta() : 0,
                    dpFecha.getValue() != null ? dpFecha.getValue().toString() : null,
                    Double.parseDouble(txtTotal.getText().trim()),
                    cmbCliente.getValue().getCui(),
                    cmbUsuario.getValue() != null
                            ? cmbUsuario.getValue().getId()
                            : SesionContext.getInstancia().getUsuarioActual().getId());

            boolean guardado;
            if (modoEdicion) {
                guardado = ventaDAO.actualizar(venta);
            } else {
                guardado = ventaDAO.crear(venta);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Venta actualizada exitosamente."
                        : "Venta registrada exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar la venta.");
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
        tablaVentas.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        dpFecha.setValue(LocalDate.now());
        txtTotal.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Venta seleccion = tablaVentas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione una venta de la tabla para editar.");
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
        if (!tablaVentas.getItems().isEmpty()) {
            tablaVentas.getSelectionModel().selectFirst();
            tablaVentas.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaVentas.getItems().isEmpty()) {
            tablaVentas.getSelectionModel().selectPrevious();
            if (tablaVentas.getSelectionModel().getSelectedIndex() >= 0) {
                tablaVentas.scrollTo(tablaVentas.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaVentas.getItems().isEmpty()) {
            tablaVentas.getSelectionModel().selectNext();
            if (tablaVentas.getSelectionModel().getSelectedIndex() >= 0) {
                tablaVentas.scrollTo(tablaVentas.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaVentas.getItems().isEmpty()) {
            tablaVentas.getSelectionModel().selectLast();
            tablaVentas.scrollTo(tablaVentas.getItems().size() - 1);
        }
    }

    @FXML
    private void handleVerFactura() {
        Venta seleccion = tablaVentas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione una venta de la tabla para ver su factura.");
            return;
        }
        FacturaController.setNoVentaSeleccionada(seleccion.getNoVenta());
        try {
            Principal.cambiarEscena("/org/ac/view/fxml/FacturaView.fxml");
        } catch (Exception e) {
            mostrarError("Error al abrir la factura: " + e.getMessage());
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
        txtTotal.clear();
        dpFecha.setValue(null);
        cmbUsuario.setValue(null);
        cmbCliente.setValue(null);
    }

    private void activarFormulario() {
        txtTotal.setDisable(false);
        dpFecha.setDisable(false);
        cmbCliente.setDisable(false);
        cmbUsuario.setDisable(false);
    }

    private void desactivarFormulario() {
        txtTotal.setDisable(true);
        dpFecha.setDisable(true);
        cmbUsuario.setDisable(true);
        cmbCliente.setDisable(true);
    }

    private void activarNavegacion() {
        tablaVentas.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaVentas.setDisable(true);
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
