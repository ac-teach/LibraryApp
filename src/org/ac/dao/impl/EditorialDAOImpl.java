package org.ac.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.ac.dao.EditorialDAO;
import org.ac.model.Editorial;
import org.ac.util.Conexion;

public class EditorialDAOImpl implements EditorialDAO {

    @Override
    public ArrayList<Editorial> listarTodos() {
        ArrayList<Editorial> lista = new ArrayList<>();
        String sql = "{call sp_listar_todos_editoriales()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql);
                ResultSet rs = consulta.executeQuery()) {
            while (rs.next()) {
                Editorial e = new Editorial();
                e.setNit(rs.getString("nit"));
                e.setNombreEditorial(rs.getString("nombre_editorial"));
                e.setTelefonoEditorial(rs.getString("telefono_editorial"));
                e.setDireccionEditoria(rs.getString("direccion_editorial"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("Error listar editoriales: " + ex.getMessage());
        }
        return lista;
    }

    @Override
    public Editorial buscarPorId(String nit) {
        Editorial e = null;
        String sql = "{call sp_buscar_editorial_por_id(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, nit);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    e = new Editorial();
                    e.setNit(rs.getString("nit"));
                    e.setNombreEditorial(rs.getString("nombre_editorial"));
                    e.setTelefonoEditorial(rs.getString("telefono_editorial"));
                    e.setDireccionEditoria(rs.getString("direccion_editorial"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error buscar editorial: " + ex.getMessage());
        }
        return e;
    }

    @Override
    public boolean crear(Editorial editorial) {
        String sql = "{call sp_crear_editorial(?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, editorial.getNit());
            consulta.setString(2, editorial.getNombreEditorial());
            consulta.setString(3, editorial.getTelefonoEditorial());
            consulta.setString(4, editorial.getDireccionEditoria());
            return consulta.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error insertar editorial: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Editorial editorial) {
        String sql = "{call sp_actualizar_editorial(?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, editorial.getNit());
            consulta.setString(2, editorial.getNombreEditorial());
            consulta.setString(3, editorial.getTelefonoEditorial());
            consulta.setString(4, editorial.getDireccionEditoria());
            return consulta.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error actualizar editorial: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(String nit) {
        String sql = "{call sp_eliminar_editorial(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, nit);
            return consulta.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error eliminar editorial: " + ex.getMessage());
            return false;
        }
    }
}
