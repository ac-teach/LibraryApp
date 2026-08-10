package org.ac.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.ac.dao.LibroDAO;
import org.ac.exception.DaoException;
import org.ac.model.Libro;
import org.ac.util.Conexion;

public class LibroDAOImpl implements LibroDAO {

    @Override
    public ArrayList<Libro> listarTodos() {
        ArrayList<Libro> lista = new ArrayList<>();
        String sql = "{call sp_listar_todos_libros()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql);
                ResultSet rs = consulta.executeQuery()) {
            while (rs.next()) {
                Libro l = new Libro();
                l.setIsbn(rs.getString("isbn"));
                l.setTitulo(rs.getString("titulo"));
                l.setFechaPublicacion(rs.getString("fecha_publicacion"));
                l.setPrecio(rs.getDouble("precio"));
                l.setIdCategoria(rs.getInt("id_categoria"));
                l.setNitEditorial(rs.getString("nit_editorial"));
                l.setStock(rs.getInt("stock"));
                lista.add(l);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar libros: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Libro buscarPorId(String isbn) {
        Libro l = null;
        String sql = "{call sp_buscar_libro_id(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, isbn);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    l = new Libro();
                    l.setIsbn(rs.getString("isbn"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setFechaPublicacion(rs.getString("fecha_publicacion"));
                    l.setPrecio(rs.getDouble("precio"));
                    l.setIdCategoria(rs.getInt("id_categoria"));
                    l.setNitEditorial(rs.getString("nit_editorial"));
                    l.setStock(rs.getInt("stock"));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar libro: " + e.getMessage(), e);
        }
        return l;
    }

    @Override
    public boolean crear(Libro libro) {
        String sql = "{call sp_crear_libro(?,?,?,?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, libro.getIsbn());
            consulta.setString(2, libro.getTitulo());
            consulta.setString(3, libro.getFechaPublicacion());
            consulta.setDouble(4, libro.getPrecio());
            consulta.setInt(5, libro.getIdCategoria());
            consulta.setString(6, libro.getNitEditorial());
            consulta.setInt(7, libro.getStock());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al insertar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = "{call sp_actualizar_libro(?,?,?,?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, libro.getIsbn());
            consulta.setString(2, libro.getTitulo());
            consulta.setString(3, libro.getFechaPublicacion());
            consulta.setDouble(4, libro.getPrecio());
            consulta.setInt(5, libro.getIdCategoria());
            consulta.setString(6, libro.getNitEditorial());
            consulta.setInt(7, libro.getStock());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(String isbn) {
        String sql = "{call sp_eliminar_libro(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, isbn);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar libro: " + e.getMessage(), e);
        }
    }
}
