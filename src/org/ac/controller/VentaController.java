package org.ac.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ac.dao.ClienteDAO;
import org.ac.dao.LibroDAO;
import org.ac.dao.VentaDAO;
import org.ac.dao.impl.ClienteDAOImpl;
import org.ac.dao.impl.LibroDAOImpl;
import org.ac.dao.impl.VentaDAOImpl;
import org.ac.exception.ValidacionException;
import org.ac.manager.SesionContext;
import org.ac.model.Cliente;
import org.ac.model.Libro;
import org.ac.model.LineaVenta;
import org.ac.model.Venta;
import org.ac.system.Principal;

//Controlador de la venta: arma líneas (libro + cantidad) en una tabla temporal,
//calcula el total automáticamente y al guardar crea la Venta y sus DetalleVenta
//con el stock descontado.
public class VentaController implements Initializable {

    @FXML
    private ComboBox<Cliente> cmbCliente;
    @FXML
    private ComboBox<Libro> cmbLibro;
    @FXML
    private Spinner<Integer> spCantidad;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnQuitar;
    @FXML
    private Button btnVaciar;
    @FXML
    private TableView<LineaVenta> tablaLineas;
    @FXML
    private TableColumn colIsbn;
    @FXML
    private TableColumn colTitulo;
    @FXML
    private TableColumn colPrecio;
    @FXML
    private TableColumn colCantidad;
    @FXML
    private TableColumn colSubtotal;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblMensaje;

    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ObservableList<LineaVenta> lineasVenta = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCombos();
        tablaLineas.setItems(lineasVenta);
        configurarTabla();
        configurarSpinner();
        calcularTotal();
    }

    private void cargarCombos() {
        cmbCliente.setItems(FXCollections.observableArrayList(clienteDAO.listarTodos()));
        cmbLibro.setItems(FXCollections.observableArrayList(libroDAO.listarTodos()));
    }

    private void configurarTabla() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<LineaVenta, String>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<LineaVenta, String>("titulo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<LineaVenta, Double>("precio"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<LineaVenta, Integer>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<LineaVenta, Double>("subtotal"));
    }

    private void configurarSpinner() {
        spCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
    }

    private void calcularTotal() {
        double total = 0;
        for (LineaVenta linea : lineasVenta) {
            total += linea.getSubtotal();
        }
        lblTotal.setText(String.format("Total: Q%.2f", total));
    }

    @FXML
    private void handleAgregarLinea() {
        Libro libro = cmbLibro.getValue();
        if (libro == null) {
            mostrarAdvertencia("Seleccione un libro para agregar a la venta.");
            return;
        }
        int cantidad = spCantidad.getValue();
        if (libro.getStock() < cantidad) {
            mostrarAdvertencia("Stock insuficiente. Disponible: " + libro.getStock() + ".");
            return;
        }
        lineasVenta.add(new LineaVenta(libro, cantidad));
        calcularTotal();
        lblMensaje.setText("");
        cmbLibro.setValue(null);
        spCantidad.getValueFactory().setValue(1);
    }

    @FXML
    private void handleQuitarLinea() {
        LineaVenta seleccion = tablaLineas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAdvertencia("Seleccione una línea de la tabla para quitar.");
            return;
        }
        lineasVenta.remove(seleccion);
        calcularTotal();
    }

    @FXML
    private void handleVaciar() {
        lineasVenta.clear();
        calcularTotal();
        lblMensaje.setText("");
    }

    @FXML
    private void handleRegistrarVenta() {
        try {
            ValidacionException.validarNoNulo(cmbCliente.getValue(),
                    "Seleccione el cliente de la venta.");
            if (lineasVenta.isEmpty()) {
                throw new ValidacionException("Agregue al menos un libro a la venta.");
            }

            //1. Guardar el encabezado de la venta con sus líneas y el stock.
            double total = 0;
            for (LineaVenta linea : lineasVenta) {
                total += linea.getSubtotal();
            }
            Venta venta = new Venta(0, null, total, cmbCliente.getValue().getCui(),
                    SesionContext.getInstancia().getUsuarioActual().getId());
            int noVenta = ventaDAO.crearVenta(venta, lineasVenta);

            if (noVenta <= 0) {
                mostrarError("No se pudo registrar la venta.");
                return;
            }

            lblMensaje.setText("Venta #" + noVenta + " registrada exitosamente.");
            limpiarVenta();
        } catch (ValidacionException e) {
            mostrarAdvertencia(e.getMessage());
            lblMensaje.setText(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al registrar la venta: " + e.getMessage());
        }
    }

    private void limpiarVenta() {
        lineasVenta.clear();
        cmbCliente.setValue(null);
        cmbLibro.setValue(null);
        spCantidad.getValueFactory().setValue(1);
        calcularTotal();
    }

    @FXML
    private void handleVolver() {
        try {
            Principal.cambiarEscena("/org/ac/view/fxml/AdminDashboradView.fxml");
        } catch (Exception e) {
            mostrarError("Error al volver al menú: " + e.getMessage());
        }
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
