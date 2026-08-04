package org.ac.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.ac.dao.CategoriaDAO;
import org.ac.model.Categoria;
import org.ac.util.Conexion;

public class CategoriaDAOImpl implements CategoriaDAO {

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
            System.err.println("Error listar categorias: " + e.getMessage());
        }
        return lista;
    }

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
            System.err.println("Error buscar categoria: " + e.getMessage());
        }
        return c;
    }

    @Override
    public boolean crear(Categoria categoria) {
        String sql = "{call sp_insertarcategoria(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, categoria.getNombreCategoria());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar categoria: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Categoria categoria) {
        String sql = "{call sp_actualizarcategoria(?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, categoria.getIdCategoria());
            consulta.setString(2, categoria.getNombreCategoria());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar categoria: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer idCategoria) {
        String sql = "{call sp_eliminarcategoria(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, idCategoria);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar categoria: " + e.getMessage());
            return false;
        }
    }
}
