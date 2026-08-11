package org.ac.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ac.dao.FacturaDAO;
import org.ac.dao.impl.FacturaDAOImpl;
import org.ac.exception.DaoException;
import org.ac.model.LineaFactura;
import org.ac.system.Principal;

public class FacturaController implements Initializable {

    //Mecanismo del proyecto: no hay paso de datos entre vistas, se usa un campo
    //estatico que ListaVentasController setea antes de abrir la vista.
    private static int noVentaSeleccionada;

    public static void setNoVentaSeleccionada(int noVenta) {
        noVentaSeleccionada = noVenta;
    }

    private final FacturaDAO facturaDAO = new FacturaDAOImpl();
    private final ObservableList<LineaFactura> lineasFactura = FXCollections.observableArrayList();

    @FXML
    private Label lblNoFactura;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblCliente;
    @FXML
    private Label lblCui;
    @FXML
    private Label lblCorreo;
    @FXML
    private Label lblUsuario;
    @FXML
    private Label lblTotal;
    @FXML
    private TableView<LineaFactura> tablaLineas;
    @FXML
    private TableColumn colTitulo;
    @FXML
    private TableColumn colIsbn;
    @FXML
    private TableColumn colCantidad;
    @FXML
    private TableColumn colPrecioUnitario;
    @FXML
    private TableColumn colSubtotal;
    @FXML
    private Button btnImprimir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarFactura();
    }

    public void configurarTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<LineaFactura, String>("tituloLibro"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<LineaFactura, String>("isbnLibro"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<LineaFactura, Integer>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<LineaFactura, Double>("precioUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<LineaFactura, Double>("subtotal"));
    }

    private void cargarFactura() {
        try {
            lineasFactura.setAll(facturaDAO.buscarFactura(noVentaSeleccionada));
            if (lineasFactura.isEmpty()) {
                mostrarError("No se encontró la factura de la venta " + noVentaSeleccionada + ".");
                return;
            }
            //La primera fila trae el encabezado repetido; se usa para llenar los labels.
            LineaFactura encabezado = lineasFactura.get(0);
            lblNoFactura.setText("# " + encabezado.getNumeroFactura());
            lblFecha.setText(encabezado.getFechaEmision());
            lblCliente.setText(encabezado.getNombreCliente());
            lblCui.setText(String.valueOf(encabezado.getCuiCliente()));
            lblCorreo.setText(encabezado.getCorreoCliente());
            lblUsuario.setText(encabezado.getUsuarioAtendio());
            lblTotal.setText(String.format("Q %.2f", encabezado.getGranTotal()));
            tablaLineas.setItems(lineasFactura);
        } catch (DaoException e) {
            mostrarError(e.getMessage());
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

    @FXML
    private void handleImprimir() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Imprimir");
        alert.setHeaderText(null);
        alert.setContentText("La impresión de la factura está en desarrollo.");
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
