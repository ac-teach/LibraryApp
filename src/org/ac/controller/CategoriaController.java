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

/**
 * Controlador de la interfaz gráfica de usuario para la gestión de categorías.
 * Administra la presentación, búsqueda, creación, edición y navegación de registros de {@link Categoria} en JavaFX.
 *
 * @author Alvaro Calderón
 * @version 1.0
 * @see javafx.fxml.Initializable
 * @see org.ac.model.Categoria
 */
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

    /**
     * Inicializa el controlador al cargar la vista FXML.
     * Configura el mapeo de columnas, carga los datos desde la base de datos y 
     * establece los escuchadores de eventos para la búsqueda y selección de elementos.
     *
     * @param location La ubicación utilizada para resolver rutas relativas para el objeto raíz, o {@code null}.
     * @param resources Los recursos utilizados para localizar el objeto raíz, o {@code null}.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTabla();
        tablaCategorias.setItems(categoriasFiltradas);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
    }

    /**
     * Configura las columnas de la tabla vinculando las propiedades del modelo {@link Categoria}.
     */
    public void configurarTabla() {
        colIdCategoria.setCellValueFactory(new PropertyValueFactory<Categoria, Integer>("idCategoria"));
        colNombreCategoria.setCellValueFactory(new PropertyValueFactory<Categoria, String>("nombreCategoria"));
    }

    /**
     * Carga la lista completa de categorías desde la base de datos a la colección observable.
     */
    private void cargarTabla() {
        try {
            listaCategorias.setAll(categoriaDAO.listarTodos());
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Configura un escuchador de cambio de texto en el campo de búsqueda para filtrar la tabla dinámicamente.
     */
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarCategorias());
    }

    /**
     * Filtra la lista de categorías según el texto ingresado en el campo de búsqueda,
     * evaluando coincidencia tanto en el ID como en el nombre de la categoría.
     */
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

    /**
     * Configura el escuchador de selección de filas en la tabla para reflejar la categoría 
     * seleccionada en los campos del formulario.
     */
    private void seleccionarFila() {
        tablaCategorias.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtNombre.setText(newSelection.getNombreCategoria());
                        desactivarFormulario();
                    }
                });
    }

    /**
     * Maneja el evento para guardar una categoría (crear o actualizar).
     * Valida la entrada, invoca las operaciones del DAO y actualiza la vista.
     */
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

    /**
     * Maneja el evento para cancelar la operación actual de registro o edición,
     * restaurando el estado original de la interfaz.
     */
    @FXML
    private void handleCancelar() {
        limpiarFormulario();
        desactivarFormulario();
        activarNavegacion();
        modoEdicion = false;
        enEdicion = null;
        lblMensaje.setText("");
    }

    /**
     * Maneja el evento para habilitar el formulario en modo de creación de un nuevo registro.
     */
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

    /**
     * Maneja el evento para preparar el formulario en modo de edición con el elemento seleccionado en la tabla.
     */
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

    /**
     * Selecciona y desplaza la vista hacia el primer registro de la tabla.
     */
    @FXML
    private void handlePrimero() {
        if (!tablaCategorias.getItems().isEmpty()) {
            tablaCategorias.getSelectionModel().selectFirst();
            tablaCategorias.scrollTo(0);
        }
    }

    /**
     * Selecciona y desplaza la vista hacia el registro anterior en la tabla.
     */
    @FXML
    private void handleAnterior() {
        if (!tablaCategorias.getItems().isEmpty()) {
            tablaCategorias.getSelectionModel().selectPrevious();
            if (tablaCategorias.getSelectionModel().getSelectedIndex() >= 0) {
                tablaCategorias.scrollTo(tablaCategorias.getSelectionModel().getSelectedIndex());
            }
        }
    }

    /**
     * Selecciona y desplaza la vista hacia el registro siguiente en la tabla.
     */
    @FXML
    private void handleSiguiente() {
        if (!tablaCategorias.getItems().isEmpty()) {
            tablaCategorias.getSelectionModel().selectNext();
            if (tablaCategorias.getSelectionModel().getSelectedIndex() >= 0) {
                tablaCategorias.scrollTo(tablaCategorias.getSelectionModel().getSelectedIndex());
            }
        }
    }

    /**
     * Selecciona y desplaza la vista hacia el último registro de la tabla.
     */
    @FXML
    private void handleUltimo() {
        if (!tablaCategorias.getItems().isEmpty()) {
            tablaCategorias.getSelectionModel().selectLast();
            tablaCategorias.scrollTo(tablaCategorias.getItems().size() - 1);
        }
    }

    /**
     * Maneja el evento para regresar a la vista del dashboard principal de la aplicación.
     */
    @FXML
    private void handleVolver() {
        try {
            Principal.cambiarEscena(Principal.rutaDashboardSegunRol());
        } catch (Exception e) {
            mostrarError("Error al volver al menú: " + e.getMessage());
        }
    }

    /**
     * Limpia los textos ingresados en los campos de entrada del formulario.
     */
    private void limpiarFormulario() {
        txtNombre.clear();
    }

    /**
     * Habilita los campos de entrada del formulario para permitir la edición o creación.
     */
    private void activarFormulario() {
        txtNombre.setDisable(false);
    }

    /**
     * Deshabilita los campos de entrada del formulario para prevenir modificaciones no deseadas.
     */
    private void desactivarFormulario() {
        txtNombre.setDisable(true);
    }

    /**
     * Habilita los botones de navegación, la tabla y la barra de búsqueda.
     */
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

    /**
     * Deshabilita los botones de navegación, la tabla y la barra de búsqueda durante operaciones de edición o creación.
     */
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

    /**
     * Muestra una alerta emergente de tipo Error con el mensaje especificado.
     *
     * @param mensaje El texto explicativo del error a mostrar.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta emergente de tipo Advertencia con el mensaje especificado.
     *
     * @param mensaje El texto explicativo de la advertencia a mostrar.
     */
    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}