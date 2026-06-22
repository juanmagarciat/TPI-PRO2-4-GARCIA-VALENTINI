package dao;

import entities.Pedido;
import enums.Estado;
import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {
    Pedido guardar(Pedido pedido) throws SQLException;
    Pedido buscarPorId(Long id) throws SQLException;
    List<Pedido> listarTodos() throws SQLException;
    List<Pedido> listarPorUsuario(Long usuarioId) throws SQLException;
    void actualizarEstado(Long id, Estado estado) throws SQLException;
    void eliminar(Long id) throws SQLException;
}