package org.ac.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.ac.dao.DetalleVentaDAO;
import org.ac.model.DetalleVenta;
import org.ac.util.Conexion;

public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    @Override
    public ArrayList<DetalleVenta> listarTodos() {
        ArrayList<DetalleVenta> lista = new ArrayList<>();
        String sql = "{call sp_listar_detalle_venta()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql);
                ResultSet rs = consulta.executeQuery()) {
            while (rs.next()) {
                DetalleVenta dv = new DetalleVenta();
                dv.setIdDetalleVenta(rs.getInt("id_detalle_venta"));
                dv.setNoVenta(rs.getInt("no_venta"));
                dv.setIsbn(rs.getString("isbn"));
                dv.setCantidad(rs.getInt("cantidad"));
                dv.setPrecio(rs.getDouble("precio"));
                lista.add(dv);
            }
        } catch (SQLException e) {
            System.err.println("Error listar detalle_venta: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public DetalleVenta buscarPorId(Integer idDetalleVenta) {
        DetalleVenta dv = null;
        String sql = "{call sp_buscar_detalle_venta(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, idDetalleVenta);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    dv = new DetalleVenta();
                    dv.setIdDetalleVenta(rs.getInt("id_detalle_venta"));
                    dv.setNoVenta(rs.getInt("no_venta"));
                    dv.setIsbn(rs.getString("isbn"));
                    dv.setCantidad(rs.getInt("cantidad"));
                    dv.setPrecio(rs.getDouble("precio"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscar detalle_venta: " + e.getMessage());
        }
        return dv;
    }

    @Override
    public boolean crear(DetalleVenta detalleVenta) {
        String sql = "{call sp_insertar_detalle_venta(?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, detalleVenta.getNoVenta());
            consulta.setString(2, detalleVenta.getIsbn());
            consulta.setInt(3, detalleVenta.getCantidad());
            consulta.setDouble(4, detalleVenta.getPrecio());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar detalle_venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(DetalleVenta detalleVenta) {
        String sql = "{call sp_actualizar_detalle_venta(?,?,?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, detalleVenta.getIdDetalleVenta());
            consulta.setInt(2, detalleVenta.getNoVenta());
            consulta.setString(3, detalleVenta.getIsbn());
            consulta.setInt(4, detalleVenta.getCantidad());
            consulta.setDouble(5, detalleVenta.getPrecio());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar detalle_venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer idDetalleVenta) {
        String sql = "{call sp_eliminar_detalle_venta(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, idDetalleVenta);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar detalle_venta: " + e.getMessage());
            return false;
        }
    }
}
