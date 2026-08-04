package org.ac.dao;

import java.util.ArrayList;

public interface Crud<T, K> {
    boolean crear(T entidad);
    boolean actualizar(T entidad);
    boolean eliminar(K id);
    T buscarPorId(K id);
    ArrayList<T> listarTodos();
}
