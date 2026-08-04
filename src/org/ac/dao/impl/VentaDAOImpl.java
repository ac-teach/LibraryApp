package org.ac.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.ac.dao.DetalleVentaDAO;
import org.ac.dao.VentaDAO;
import org.ac.model.DetalleVenta;
import org.ac.model.LineaVenta;
import org.ac.model.Venta;
import org.ac.util.Conexion;

public class VentaDAOImpl implements VentaDAO {

    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAOImpl();

    @Override
    public ArrayList<Venta> listarTodos() {
        ArrayList<Venta> lista = new ArrayList<>();
        String sql = "{call sp_listar_ventas()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql);
                ResultSet rs = consulta.executeQuery()) {
            while (rs.next()) {
                Venta v = new Venta();
                v.setNoVenta(rs.getInt("no_venta"));
                v.setFechaVenta(rs.getString("fecha_venta"));
                v.setTotalVenta(rs.getDouble("total_venta"));
                v.setCuiCliente(rs.getLong("cui_cliente"));
                v.setIdUsuario(rs.getInt("id_usuario"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error listar ventas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Venta buscarPorId(Integer noVenta) {
        Venta v = null;
        String sql = "{call sp_buscar_venta(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, noVenta);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    v = new Venta();
                    v.setNoVenta(rs.getInt("no_venta"));
                    v.setFechaVenta(rs.getString("fecha_venta"));
                    v.setTotalVenta(rs.getDouble("total_venta"));
                    v.setCuiCliente(rs.getLong("cui_cliente"));
                    v.setIdUsuario(rs.getInt("id_usuario"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscar venta: " + e.getMessage());
        }
        return v;
    }

    @Override
    public boolean crear(Venta venta) {
        String sql = "{call sp_insertar_venta(?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setDouble(1, venta.getTotalVenta());
            consulta.setString(2, String.valueOf(venta.getCuiCliente()));
            consulta.setInt(3, venta.getIdUsuario());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Venta venta) {
        String sql = "{call sp_actualizar_venta(?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, venta.getNoVenta());
            consulta.setDouble(2, venta.getTotalVenta());
            consulta.setLong(3, venta.getCuiCliente());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar venta: " + e.getMessage());
            return false;
        }
    }

    //crearVenta inserta el encabezado de la venta, obtiene el no_venta con
    //LAST_INSERT_ID() y luego inserta cada línea y descuenta el stock.
    //No es atómico (ver plan), por eso se reporta si algún detalle falla.
    @Override
    public int crearVenta(Venta venta, List<LineaVenta> lineas) {
        int noVenta = -1;
        String sql = "{call sp_insertar_venta(?,?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setDouble(1, venta.getTotalVenta());
            consulta.setString(2, String.valueOf(venta.getCuiCliente()));
            consulta.setInt(3, venta.getIdUsuario());
            int filasAfectadas = consulta.executeUpdate();
            if (filasAfectadas > 0) {
                try (Statement sentencia = conexion.createStatement();
                        ResultSet rs = sentencia.executeQuery("SELECT LAST_INSERT_ID()")) {
                    if (rs.next()) {
                        noVenta = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertar venta: " + e.getMessage());
            return -1;
        }

        if (noVenta > 0) {
            boolean completo = true;
            for (LineaVenta linea : lineas) {
                DetalleVenta detalle = new DetalleVenta(0, noVenta,
                        linea.getIsbn(), linea.getCantidad(), linea.getPrecio());
                if (!detalleVentaDAO.crear(detalle)) {
                    completo = false;
                }
                if (!descontarStock(linea.getIsbn(), linea.getCantidad())) {
                    completo = false;
                }
            }
            if (!completo) {
                System.err.println("Venta " + noVenta + " registrada con detalles incompletos.");
            }
        }
        return noVenta;
    }

    private boolean descontarStock(String isbn, int cantidad) {
        String sql = "{call sp_descontarstock(?,?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, isbn);
            consulta.setInt(2, cantidad);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error descontar stock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer noVenta) {
        String sql = "{call sp_eliminar_venta(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, noVenta);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar venta: " + e.getMessage());
            return false;
        }
    }
}
