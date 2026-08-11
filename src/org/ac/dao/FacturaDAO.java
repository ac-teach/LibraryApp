package org.ac.dao;

import java.util.ArrayList;
import org.ac.model.LineaFactura;

public interface FacturaDAO {
    ArrayList<LineaFactura> buscarFactura(int noVenta);
}
