package dao;

import config.ConexionDB;
import entities.*;
import enums.Estado;
import enums.FormaPago;
import enums.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    private static final String SELECT_PEDIDO_BASE =
            "SELECT p.id, p.fecha, p.estado, p.formaPago, p.eliminado, p.createdAt, " +
                    "       u.id AS usu_id, u.nombre AS usu_nombre, u.apellido AS usu_apellido, " +
                    "       u.mail AS usu_mail, u.celular AS usu_celular, " +
                    "       u.password AS usu_password, u.rol AS usu_rol, " +
                    "       u.eliminado AS usu_eliminado, u.createdAt AS usu_createdAt " +
                    "FROM pedidos p " +
                    "INNER JOIN usuarios u ON p.usuario_id = u.id ";

    private static final String SELECT_DETALLE_BASE =
            "SELECT d.id, d.cantidad, d.precioUnitario, d.subtotal, d.eliminado, d.createdAt, " +
                    "       pr.id AS prod_id, pr.nombre AS prod_nombre, pr.descripcion AS prod_descripcion, " +
                    "       pr.precio AS prod_precio, pr.stock AS prod_stock, " +
                    "       pr.imagen AS prod_imagen, pr.disponible AS prod_disponible, " +
                    "       pr.eliminado AS prod_eliminado, pr.createdAt AS prod_createdAt, " +
                    "       c.id AS cat_id, c.nombre AS cat_nombre, c.descripcion AS cat_descripcion, " +
                    "       c.eliminado AS cat_eliminado, c.createdAt AS cat_createdAt " +
                    "FROM detalles_pedido d " +
                    "INNER JOIN productos pr ON d.producto_id = pr.id " +
                    "INNER JOIN categorias c ON pr.categoria_id = c.id " +
                    "WHERE d.pedido_id = ? AND d.eliminado = false";

    @Override
    public Pedido guardar(Pedido pedido) throws SQLException {
        String sqlPedido  = "INSERT INTO pedidos (usuario_id, fecha, estado, formaPago) VALUES (?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precioUnitario, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);

            try (PreparedStatement psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                psPedido.setLong(1, pedido.getUsuario().getId());
                psPedido.setDate(2, Date.valueOf(pedido.getFecha()));
                psPedido.setString(3, pedido.getEstado().name());
                psPedido.setString(4, pedido.getFormaPago().name());
                psPedido.executeUpdate();

                try (ResultSet rs = psPedido.getGeneratedKeys()) {
                    if (rs.next()) pedido.setId(rs.getLong(1));
                }
            }

            try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle, Statement.RETURN_GENERATED_KEYS)) {
                for (DetallePedido detalle : pedido.getDetalles()) {
                    psDetalle.setLong(1, pedido.getId());
                    psDetalle.setLong(2, detalle.getProducto().getId());
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setBigDecimal(4, detalle.getPrecioUnitario());
                    psDetalle.setBigDecimal(5, detalle.getSubtotal());
                    psDetalle.executeUpdate();

                    try (ResultSet rs = psDetalle.getGeneratedKeys()) {
                        if (rs.next()) detalle.setId(rs.getLong(1));
                    }
                    detalle.setPedido(pedido);
                }
            }

            con.commit();
            return pedido;

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @Override
    public Pedido buscarPorId(Long id) throws SQLException {
        String sql = SELECT_PEDIDO_BASE + "WHERE p.id=? AND p.eliminado=false";
        try (Connection con = ConexionDB.getConexion()) {
            Pedido pedido = null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) pedido = mapearPedido(rs);
                }
            }
            if (pedido != null) pedido.setDetalles(cargarDetalles(con, pedido));
            return pedido;
        }
    }

    @Override
    public List<Pedido> listarTodos() throws SQLException {
        String sql = SELECT_PEDIDO_BASE + "WHERE p.eliminado=false ORDER BY p.fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) pedidos.add(mapearPedido(rs));
            }
            for (Pedido p : pedidos) p.setDetalles(cargarDetalles(con, p));
        }
        return pedidos;
    }

    @Override
    public List<Pedido> listarPorUsuario(Long usuarioId) throws SQLException {
        String sql = SELECT_PEDIDO_BASE + "WHERE p.eliminado=false AND p.usuario_id=? ORDER BY p.fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, usuarioId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) pedidos.add(mapearPedido(rs));
                }
            }
            for (Pedido p : pedidos) p.setDetalles(cargarDetalles(con, p));
        }
        return pedidos;
    }

    @Override
    public void actualizarEstado(Long id, Estado estado) throws SQLException {
        String sql = "UPDATE pedidos SET estado=? WHERE id=? AND eliminado=false";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        String sqlDetalles = "UPDATE detalles_pedido SET eliminado=true WHERE pedido_id=?";
        String sqlPedido   = "UPDATE pedidos SET eliminado=true WHERE id=?";

        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlDetalles)) {
                ps.setLong(1, id); ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                ps.setLong(1, id); ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    private List<DetallePedido> cargarDetalles(Connection con, Pedido pedido) throws SQLException {
        List<DetallePedido> detalles = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SELECT_DETALLE_BASE)) {
            ps.setLong(1, pedido.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) detalles.add(mapearDetalle(rs, pedido));
            }
        }
        return detalles;
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario(
                rs.getLong("usu_id"),
                rs.getString("usu_nombre"),
                rs.getString("usu_apellido"),
                rs.getString("usu_mail"),
                rs.getString("usu_celular"),
                rs.getString("usu_password"),
                Rol.valueOf(rs.getString("usu_rol")),
                rs.getBoolean("usu_eliminado"),
                rs.getTimestamp("usu_createdAt").toLocalDateTime()
        );
        return new Pedido(
                rs.getLong("id"),
                usuario,
                rs.getDate("fecha").toLocalDate(),
                Estado.valueOf(rs.getString("estado")),
                FormaPago.valueOf(rs.getString("formaPago")),
                new ArrayList<>(),
                rs.getBoolean("eliminado"),
                rs.getTimestamp("createdAt").toLocalDateTime()
        );
    }

    private DetallePedido mapearDetalle(ResultSet rs, Pedido pedido) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getLong("cat_id"),
                rs.getString("cat_nombre"),
                rs.getString("cat_descripcion"),
                rs.getBoolean("cat_eliminado"),
                rs.getTimestamp("cat_createdAt").toLocalDateTime()
        );
        Producto producto = new Producto(
                rs.getLong("prod_id"),
                rs.getString("prod_nombre"),
                rs.getString("prod_descripcion"),
                rs.getBigDecimal("prod_precio"),
                rs.getInt("prod_stock"),
                rs.getString("prod_imagen"),
                rs.getBoolean("prod_disponible"),
                categoria,
                rs.getBoolean("prod_eliminado"),
                rs.getTimestamp("prod_createdAt").toLocalDateTime()
        );
        return new DetallePedido(
                rs.getLong("id"),
                pedido,
                producto,
                rs.getInt("cantidad"),
                rs.getBigDecimal("precioUnitario"),
                rs.getBigDecimal("subtotal"),
                rs.getBoolean("eliminado"),
                rs.getTimestamp("createdAt").toLocalDateTime()
        );
    }
}
// Implementacion JDBC de PedidoDAO
