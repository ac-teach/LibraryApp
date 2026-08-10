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
import org.ac.dao.ClienteDAO;
import org.ac.dao.impl.ClienteDAOImpl;
import org.ac.exception.DaoException;
import org.ac.exception.ValidacionException;
import org.ac.model.Cliente;
import org.ac.system.Principal;

public class ClienteController implements Initializable {

    @FXML
    private TextField txtCui;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<Cliente> tablaClientes;//Tabla de entidad: cliente
    @FXML
    private TableColumn colCUI;
    @FXML
    private TableColumn colNombreCliente;
    @FXML
    private TableColumn colApellidoCliente;
    @FXML
    private TableColumn colCorreoElectronico;
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
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();//Entidad:Cliente
    private final FilteredList<Cliente> clientesFiltrados = new FilteredList<>(listaClientes, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTabla();
        tablaClientes.setItems(clientesFiltrados);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colCUI.setCellValueFactory(new PropertyValueFactory<Cliente, Long>("cui"));
        colNombreCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("nombreCliente"));
        colApellidoCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("apellidoCliente"));
        colCorreoElectronico.setCellValueFactory(new PropertyValueFactory<Cliente, String>("correoElectronico"));
    }

    private void cargarTabla() {
        try {
            listaClientes.setAll(clienteDAO.listarTodos());
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarClientes());
    }

    private void filtrarClientes() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            clientesFiltrados.setPredicate(p -> true);
        } else {
            clientesFiltrados.setPredicate(cliente ->
                    String.valueOf(cliente.getCui()).contains(busqueda)
                    || cliente.getNombreCliente().toLowerCase().contains(busqueda)
                    || cliente.getApellidoCliente().toLowerCase().contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtCui.setText(String.valueOf(newSelection.getCui()));
                        txtNombre.setText(newSelection.getNombreCliente());
                        txtApellido.setText(newSelection.getApellidoCliente());
                        txtCorreo.setText(newSelection.getCorreoElectronico());
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            ValidacionException.validarNoVacio(txtCui.getText(), "CUI");
            ValidacionException.validarNoVacio(txtNombre.getText(), "nombre");
            ValidacionException.validarNoVacio(txtApellido.getText(), "apellido");
            ValidacionException.validarNoVacio(txtCorreo.getText(), "correo electrónico");
            ValidacionException.validarNumero(txtCui.getText(), "CUI");
            ValidacionException.validarLongitudExacta(txtCui.getText().trim(), 13,
                    "El CUI debe tener exactamente 13 dígitos.");
            ValidacionException.validarFormatoEmail(txtCorreo.getText(),
                    "El correo electrónico no tiene un formato válido.");

            Cliente cliente = new Cliente();
            cliente.setCui(Long.parseLong(txtCui.getText().trim()));
            cliente.setNombreCliente(txtNombre.getText().trim());
            cliente.setApellidoCliente(txtApellido.getText().trim());
            cliente.setCorreoElectronico(txtCorreo.getText().trim());

            boolean guardado;
            if (modoEdicion) {
                guardado = clienteDAO.actualizar(cliente);
            } else {
                guardado = clienteDAO.crear(cliente);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Cliente actualizado exitosamente."
                        : "Cliente registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar el cliente.");
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
    private void handleNuevoCliente() {
        modoEdicion = false;
        limpiarFormulario();
        activarFormulario();
        desactivarNavegacion();
        tablaClientes.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtCui.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Cliente seleccion = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un cliente de la tabla para editar.");
            return;
        }
        modoEdicion = true;
        activarFormulario();
        desactivarNavegacion();
        lblMensaje.setText("");
    }

    @FXML
    private void handlePrimero() {
        if (!tablaClientes.getItems().isEmpty()) {
            tablaClientes.getSelectionModel().selectFirst();
            tablaClientes.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaClientes.getItems().isEmpty()) {
            tablaClientes.getSelectionModel().selectPrevious();
            if (tablaClientes.getSelectionModel().getSelectedIndex() >= 0) {
                tablaClientes.scrollTo(tablaClientes.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaClientes.getItems().isEmpty()) {
            tablaClientes.getSelectionModel().selectNext();
            if (tablaClientes.getSelectionModel().getSelectedIndex() >= 0) {
                tablaClientes.scrollTo(tablaClientes.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaClientes.getItems().isEmpty()) {
            tablaClientes.getSelectionModel().selectLast();
            tablaClientes.scrollTo(tablaClientes.getItems().size() - 1);
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
        txtCui.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtCorreo.clear();
    }

    private void activarFormulario() {
        txtCui.setDisable(false);
        txtNombre.setDisable(false);
        txtApellido.setDisable(false);
        txtCorreo.setDisable(false);
    }

    private void desactivarFormulario() {
        txtCui.setDisable(true);
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtCorreo.setDisable(true);
    }

    private void activarNavegacion() {
        tablaClientes.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaClientes.setDisable(true);
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
