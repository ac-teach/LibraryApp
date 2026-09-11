package org.ac.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.ac.dao.FacturaDAO;
import org.ac.exception.DaoException;
import org.ac.model.LineaFactura;
import org.ac.util.Conexion;

public class FacturaDAOImpl implements FacturaDAO {

    @Override
    public ArrayList<LineaFactura> buscarFactura(int noVenta) {
        ArrayList<LineaFactura> lista = new ArrayList<>();
        String sql = "{call sp_buscar_factura(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, noVenta);
            try (ResultSet rs = consulta.executeQuery()) {
                while (rs.next()) {
                    LineaFactura linea = new LineaFactura();
                    linea.setNumeroFactura(rs.getInt("numero_factura"));
                    linea.setFechaEmision(rs.getString("fecha_emision"));
                    linea.setCuiCliente(rs.getLong("cui_cliente"));
                    linea.setNombreCliente(rs.getString("nombre_cliente"));
                    linea.setCorreoCliente(rs.getString("correo_cliente"));
                    linea.setIsbnLibro(rs.getString("isbn_libro"));
                    linea.setTituloLibro(rs.getString("titulo_libro"));
                    linea.setCantidad(rs.getInt("cantidad"));
                    linea.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    linea.setSubtotal(rs.getDouble("subtotal"));
                    linea.setUsuarioAtendio(rs.getString("usuario_atendio"));
                    linea.setGranTotal(rs.getDouble("gran_total"));
                    lista.add(linea);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar la factura: " + e.getMessage(), e);
        }
        return lista;
    }
}
