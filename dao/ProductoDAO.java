package dao;

import entities.Producto;
import java.sql.SQLException;
import java.util.List;

public interface ProductoDAO {
    Producto guardar(Producto producto) throws SQLException;
    Producto buscarPorId(Long id) throws SQLException;
    List<Producto> listarTodos() throws SQLException;
    List<Producto> listarPorCategoria(Long categoriaId) throws SQLException;
    void actualizar(Producto producto) throws SQLException;
    void actualizarStock(Long id, int nuevoStock) throws SQLException;
    void eliminar(Long id) throws SQLException;
}