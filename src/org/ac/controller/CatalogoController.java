package org.ac.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.ac.dao.LibroDAO;
import org.ac.dao.impl.LibroDAOImpl;
import org.ac.exception.DaoException;
import org.ac.model.Libro;
import org.ac.system.Principal;

public class CatalogoController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TilePane tileCatalogo;
    @FXML private ImageView imgDetalle;
    @FXML private Label lblDetalleTitulo;
    @FXML private Label lblDetalleIsbn;
    @FXML private Label lblDetallePrecio;
    @FXML private Label lblDetalleStock;
    @FXML private Label lblDetalleEditorial;
    @FXML private Label lblDetalleMensaje;

    private static final String DIRECTORIO_FOTOS = "src/imagenes";
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ObservableList<Libro> listaLibros = FXCollections.observableArrayList();
    private final FilteredList<Libro> librosFiltrados = new FilteredList<>(listaLibros, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCatalogo();
        configurarBusqueda();
        imgDetalle.setImage(null);
        lblDetalleTitulo.setText("");
        lblDetalleIsbn.setText("");
        lblDetallePrecio.setText("");
        lblDetalleStock.setText("");
        lblDetalleEditorial.setText("");
        lblDetalleMensaje.setText("Seleccione un libro de la cuadrícula para ver su detalle.");
    }

    private void cargarCatalogo() {
        try {
            listaLibros.setAll(libroDAO.listarTodos());
            renderizar();
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> {
            String busqueda = newValue == null ? "" : newValue.trim().toLowerCase();
            if (busqueda.isEmpty()) {
                librosFiltrados.setPredicate(p -> true);
            } else {
                librosFiltrados.setPredicate(libro ->
                        (libro.getTitulo() != null && libro.getTitulo().toLowerCase().contains(busqueda))
                        || (libro.getIsbn() != null && libro.getIsbn().toLowerCase().contains(busqueda)));
            }
            renderizar();
        });
    }

    private void renderizar() {
        tileCatalogo.getChildren().clear();
        for (Libro libro : librosFiltrados) {
            ImageView imagen = new ImageView(cargarImagenLibro(libro));
            imagen.setFitWidth(110);
            imagen.setFitHeight(150);
            imagen.setPreserveRatio(true);

            Label lblTitulo = new Label(libro.getTitulo());
            lblTitulo.setWrapText(true);
            lblTitulo.setMaxWidth(130);
            lblTitulo.setAlignment(Pos.CENTER);
            lblTitulo.setStyle("-fx-font-weight: bold;");

            Label lblPrecio = new Label(String.format("%.2f", libro.getPrecio()));
            lblPrecio.setAlignment(Pos.CENTER);

            VBox card = new VBox(imagen, lblTitulo, lblPrecio);
            card.setPrefWidth(150);
            card.setAlignment(Pos.CENTER);
            card.setSpacing(6);
            card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-cursor: hand;");
            card.setOnMouseClicked(e -> mostrarDetalle(libro));
            tileCatalogo.getChildren().add(card);
        }
    }

    private Image cargarImagenLibro(Libro libro) {
        if (libro.getUrlFoto() == null || libro.getUrlFoto().trim().isEmpty()) {
            return crearImagenPlaceholder();
        }
        File foto = new File("src", libro.getUrlFoto());
        if (foto.exists()) {
            return new Image(foto.toURI().toString());
        }
        return crearImagenPlaceholder();
    }

    private Image crearImagenPlaceholder() {
        WritableImage placeholder = new WritableImage(200, 260);
        PixelWriter pixelWriter = placeholder.getPixelWriter();
        Color gris = Color.web("#e0e0e0");
        for (int y = 0; y < 260; y++) {
            for (int x = 0; x < 200; x++) {
                pixelWriter.setColor(x, y, gris);
            }
        }
        return placeholder;
    }

    private void mostrarDetalle(Libro libro) {
        imgDetalle.setImage(cargarImagenLibro(libro));
        lblDetalleTitulo.setText(libro.getTitulo());
        lblDetalleIsbn.setText("ISBN: " + libro.getIsbn());
        lblDetallePrecio.setText("Precio: " + String.format("%.2f", libro.getPrecio()));
        lblDetalleStock.setText("Stock: " + libro.getStock());
        lblDetalleEditorial.setText("Editorial: " + libro.getNitEditorial());
        lblDetalleMensaje.setText("");
    }

    @FXML
    private void handleVolver() {
        try {
            Principal.cambiarEscena(Principal.rutaDashboardSegunRol());
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

}
