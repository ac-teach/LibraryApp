package org.ac.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.ac.dao.AutorDAO;
import org.ac.model.Autor;
import org.ac.util.Conexion;

public class AutorDAOImpl implements AutorDAO {

    @Override
    public ArrayList<Autor> listarTodos() {
        ArrayList<Autor> lista = new ArrayList<>();
        String sql = "{call sp_listarautores()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql);
                ResultSet rs = consulta.executeQuery()) {
            while (rs.next()) {
                Autor a = new Autor();
                a.setIdAutor(rs.getInt("id_autor"));
                a.setNombreAutor(rs.getString("nombre_autor"));
                a.setApellidoAutor(rs.getString("apellido_autor"));
                a.setNacionalidad(rs.getString("nacionalidad"));
                a.setBiografia(rs.getString("biografia"));
                lista.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error listar autores: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Autor buscarPorId(Integer idAutor) {
        Autor a = null;
        String sql = "{call sp_buscarautor(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, idAutor);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    a = new Autor();
                    a.setIdAutor(rs.getInt("id_autor"));
                    a.setNombreAutor(rs.getString("nombre_autor"));
                    a.setApellidoAutor(rs.getString("apellido_autor"));
                    a.setNacionalidad(rs.getString("nacionalidad"));
                    a.setBiografia(rs.getString("biografia"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscar autor: " + e.getMessage());
        }
        return a;
    }

    @Override
    public boolean crear(Autor autor) {
        String sql = "{call sp_insertarautor(?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, autor.getNombreAutor());
            consulta.setString(2, autor.getApellidoAutor());
            consulta.setString(3, autor.getNacionalidad());
            consulta.setString(4, autor.getBiografia());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar autor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Autor autor) {
        String sql = "{call sp_actualizarautor(?,?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, autor.getIdAutor());
            consulta.setString(2, autor.getNombreAutor());
            consulta.setString(3, autor.getApellidoAutor());
            consulta.setString(4, autor.getNacionalidad());
            consulta.setString(5, autor.getBiografia());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar autor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer idAutor) {
        String sql = "{call sp_eliminarautor(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, idAutor);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar autor: " + e.getMessage());
            return false;
        }
    }
}
