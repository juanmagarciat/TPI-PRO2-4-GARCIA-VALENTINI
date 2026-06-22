package dao;

import entities.Categoria;
import java.sql.SQLException;
import java.util.List;

public interface CategoriaDAO {
    Categoria guardar(Categoria categoria) throws SQLException;
    Categoria buscarPorId(Long id) throws SQLException;
    List<Categoria> listarTodos() throws SQLException;
    void actualizar(Categoria categoria) throws SQLException;
    void eliminar(Long id) throws SQLException;
}