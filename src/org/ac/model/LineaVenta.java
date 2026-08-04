package org.ac.model;

//Línea de venta: un libro con su cantidad, usada como fila temporal en la pantalla de venta
//antes de guardar los DetalleVenta en la base de datos.
public class LineaVenta {
    private Libro libro;
    private int cantidad;

    public LineaVenta() {
    }

    public LineaVenta(Libro libro, int cantidad) {
        this.libro = libro;
        this.cantidad = cantidad;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getIsbn() {
        return libro.getIsbn();
    }

    public String getTitulo() {
        return libro.getTitulo();
    }

    public double getPrecio() {
        return libro.getPrecio();
    }

    public double getSubtotal() {
        return libro.getPrecio() * cantidad;
    }
}
