package org.ac.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.ac.dao.ClienteDAO;
import org.ac.model.Cliente;
import org.ac.util.Conexion;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public ArrayList<Cliente> listarTodos() {
        ArrayList<Cliente> lista = new ArrayList<>();
        String sql = "{call sp_listarclientes()}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consulta = conexion.prepareCall(sql); ResultSet rs = consulta.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setCui(rs.getLong("cui"));
                c.setNombreCliente(rs.getString("nombre_cliente"));
                c.setApellidoCliente(rs.getString("apellido_cliente"));
                c.setCorreoElectronico(rs.getString("correo_electronico"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error listar clientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(Long cui) {
        Cliente c = null;
        String sql = "{call sp_buscarcliente(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setLong(1, cui);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    c = new Cliente();
                    c.setCui(rs.getLong("cui"));
                    c.setNombreCliente(rs.getString("nombre_cliente"));
                    c.setApellidoCliente(rs.getString("apellido_cliente"));
                    c.setCorreoElectronico(rs.getString("correo_electronico"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscar cliente: " + e.getMessage());
        }
        return c;
    }

    @Override
    public boolean crear(Cliente cliente) {
        String sql = "{call sp_insertarcliente(?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setLong(1, cliente.getCui());
            consulta.setString(2, cliente.getNombreCliente());
            consulta.setString(3, cliente.getApellidoCliente());
            consulta.setString(4, cliente.getCorreoElectronico());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Cliente cliente) {
        String sql = "{call sp_actualizarcliente(?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setLong(1, cliente.getCui());
            consulta.setString(2, cliente.getNombreCliente());
            consulta.setString(3, cliente.getApellidoCliente());
            consulta.setString(4, cliente.getCorreoElectronico());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Long cui) {
        String sql = "{call sp_eliminarcliente(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setLong(1, cui);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}
