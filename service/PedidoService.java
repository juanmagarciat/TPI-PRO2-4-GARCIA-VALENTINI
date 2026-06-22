package service;

import dao.PedidoDAO;
import dao.PedidoDAOImpl;
import entities.Pedido;
import entities.Producto;
import entities.Usuario;
import enums.Estado;
import enums.FormaPago;
import exception.EntidadNoEncontradaException;
import exception.ReglaNegocioException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public PedidoService() {
        this.pedidoDAO = new PedidoDAOImpl();
        this.usuarioService = new UsuarioService();
        this.productoService = new ProductoService();
    }

    public Pedido crear(Long usuarioId, FormaPago formaPago, Map<Long, Integer> items)
            throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {

        if (usuarioId == null)
            throw new ReglaNegocioException("El pedido debe tener un usuario asignado.");

        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        if (formaPago == null)
            throw new ReglaNegocioException("Debe indicar una forma de pago.");
        if (items == null || items.isEmpty())
            throw new ReglaNegocioException("El pedido debe contener al menos un producto.");

        Pedido pedido = new Pedido(usuario, formaPago);

        for (Map.Entry<Long, Integer> item : items.entrySet()) {
            Long productoId = item.getKey();
            Integer cantidad = item.getValue();

            if (cantidad == null || cantidad <= 0)
                throw new ReglaNegocioException("La cantidad del producto id " + productoId + " debe ser > 0.");

            Producto producto = productoService.buscarPorId(productoId);

            // Usa el método obligatorio definido en la consigna
            pedido.addDetallePedido(cantidad, producto.getPrecio().doubleValue(), producto);
        }

        return pedidoDAO.guardar(pedido);
    }

    public List<Pedido> listarTodos() throws SQLException {
        return pedidoDAO.listarTodos();
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) throws SQLException {
        return pedidoDAO.listarPorUsuario(usuarioId);
    }

    public Pedido buscarPorId(Long id) throws SQLException, EntidadNoEncontradaException {
        Pedido pedido = pedidoDAO.buscarPorId(id);
        if (pedido == null)
            throw new EntidadNoEncontradaException("No se encontró el pedido con id " + id);
        return pedido;
    }

    public void actualizarEstado(Long id, Estado estado)
            throws SQLException, EntidadNoEncontradaException, ReglaNegocioException {

        buscarPorId(id);
        if (estado == null)
            throw new ReglaNegocioException("El estado es obligatorio.");
        pedidoDAO.actualizarEstado(id, estado);
    }

    public void eliminar(Long id) throws SQLException, EntidadNoEncontradaException {
        buscarPorId(id);
        pedidoDAO.eliminar(id);
    }
}