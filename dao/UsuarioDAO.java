package dao;

import entities.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {
    Usuario guardar(Usuario usuario) throws SQLException;
    Usuario buscarPorId(Long id) throws SQLException;
    Usuario buscarPorMail(String mail) throws SQLException;
    List<Usuario> listarTodos() throws SQLException;
    void actualizar(Usuario usuario) throws SQLException;
    void eliminar(Long id) throws SQLException;
}
// Interface DAO de Usuario
