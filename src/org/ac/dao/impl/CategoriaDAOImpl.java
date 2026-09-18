package org.ac.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.ac.dao.CategoriaDAO;
import org.ac.exception.DaoException;
import org.ac.model.Categoria;
import org.ac.util.Conexion;

/**
 * Implementación de la interfaz {@link CategoriaDAO} para la gestión de datos de categorías.
 * Utiliza JDBC y procedimientos almacenados para realizar operaciones CRUD en la base de datos.
 *
 * @author Alvaro Calderón
 * @version 1.0
 * @see CategoriaDAO
 * @see Categoria
 */
public class CategoriaDAOImpl implements CategoriaDAO {

    /**
     * Obtiene la lista completa de todas las categorías registradas.
     * Ejecuta el procedimiento almacenado {@code sp_listarcategorias()}.
     *
     * @return Una lista de tipo {@link ArrayList} con los objetos {@link Categoria} encontrados.
     * @throws DaoException Si ocurre un error al comunicarse con la base de datos.
     */
    @Override
    public ArrayList<Categoria> listarTodos() {
        ArrayList<Categoria> lista = new ArrayList<>();
        String sql = "{call sp_listarcategorias()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql);
                ResultSet rs = consulta.executeQuery()) {
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombreCategoria(rs.getString("nombre_categoria"));
                lista.add(c);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar categorias: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Busca y retorna una categoría según su identificador único.
     * Ejecuta el procedimiento almacenado {@code sp_buscarcategoria(?)}.
     *
     * @param idCategoria El identificador único de la categoría a consultar.
     * @return El objeto {@link Categoria} si se encuentra, o {@code null} si no existe registro.
     * @throws DaoException Si ocurre un error al comunicarse con la base de datos.
     */
    @Override
    public Categoria buscarPorId(Integer idCategoria) {
        Categoria c = null;
        String sql = "{call sp_buscarcategoria(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, idCategoria);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    c = new Categoria();
                    c.setIdCategoria(rs.getInt("id_categoria"));
                    c.setNombreCategoria(rs.getString("nombre_categoria"));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar categoria: " + e.getMessage(), e);
        }
        return c;
    }

    /**
     * Registra una nueva categoría en la base de datos.
     * Ejecuta el procedimiento almacenado {@code sp_insertarcategoria(?)}.
     *
     * @param categoria El objeto {@link Categoria} con la información a insertar.
     * @return {@code true} si la inserción fue exitosa; {@code false} en caso contrario.
     * @throws DaoException Si ocurre un error al comunicarse con la base de datos.
     */
    @Override
    public boolean crear(Categoria categoria) {
        String sql = "{call sp_insertarcategoria(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, categoria.getNombreCategoria());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al insertar categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza la información de una categoría existente.
     * Ejecuta el procedimiento almacenado {@code sp_actualizarcategoria(?,?)}.
     *
     * @param categoria El objeto {@link Categoria} que contiene los datos actualizados y el ID correspondiente.
     * @return {@code true} si el registro fue actualizado correctamente; {@code false} en caso contrario.
     * @throws DaoException Si ocurre un error al comunicarse con la base de datos.
     */
    @Override
    public boolean actualizar(Categoria categoria) {
        String sql = "{call sp_actualizarcategoria(?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, categoria.getIdCategoria());
            consulta.setString(2, categoria.getNombreCategoria());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una categoría de la base de datos mediante su ID.
     * Ejecuta el procedimiento almacenado {@code sp_eliminarcategoria(?)}.
     *
     * @param idCategoria El identificador único de la categoría a eliminar.
     * @return {@code true} si la eliminación fue exitosa; {@code false} en caso contrario.
     * @throws DaoException Si ocurre un error al comunicarse con la base de datos.
     */
    @Override
    public boolean eliminar(Integer idCategoria) {
        String sql = "{call sp_eliminarcategoria(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, idCategoria);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar categoria: " + e.getMessage(), e);
        }
    }
}