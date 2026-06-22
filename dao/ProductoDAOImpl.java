package dao;

import config.ConexionDB;
import entities.Categoria;
import entities.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    private static final String SELECT_BASE =
            "SELECT p.id, p.nombre, p.descripcion, p.precio, p.stock, p.imagen, p.disponible, " +
                    "       p.eliminado, p.createdAt, " +
                    "       c.id AS cat_id, c.nombre AS cat_nombre, c.descripcion AS cat_descripcion, " +
                    "       c.eliminado AS cat_eliminado, c.createdAt AS cat_createdAt " +
                    "FROM productos p " +
                    "INNER JOIN categorias c ON p.categoria_id = c.id ";

    @Override
    public Producto guardar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, stock, imagen, disponible, categoria_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getImagen());
            ps.setBoolean(6, Boolean.TRUE.equals(producto.getDisponible()));
            ps.setLong(7, producto.getCategoria().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) producto.setId(rs.getLong(1));
            }
        }
        return producto;
    }

    @Override
    public Producto buscarPorId(Long id) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.id=? AND p.eliminado=false";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Producto> listarTodos() throws SQLException {
        String sql = SELECT_BASE + "WHERE p.eliminado=false ORDER BY p.nombre";
        List<Producto> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public List<Producto> listarPorCategoria(Long categoriaId) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.eliminado=false AND p.categoria_id=? ORDER BY p.nombre";
        List<Producto> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public void actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre=?, descripcion=?, precio=?, stock=?, imagen=?, disponible=?, " +
                "categoria_id=? WHERE id=? AND eliminado=false";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getImagen());
            ps.setBoolean(6, Boolean.TRUE.equals(producto.getDisponible()));
            ps.setLong(7, producto.getCategoria().getId());
            ps.setLong(8, producto.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizarStock(Long id, int nuevoStock) throws SQLException {
        String sql = "UPDATE productos SET stock=? WHERE id=? AND eliminado=false";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        String sql = "UPDATE productos SET eliminado=true WHERE id=?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getLong("cat_id"),
                rs.getString("cat_nombre"),
                rs.getString("cat_descripcion"),
                rs.getBoolean("cat_eliminado"),
                rs.getTimestamp("cat_createdAt").toLocalDateTime()
        );
        return new Producto(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBigDecimal("precio"),
                rs.getInt("stock"),
                rs.getString("imagen"),
                rs.getBoolean("disponible"),
                categoria,
                rs.getBoolean("eliminado"),
                rs.getTimestamp("createdAt").toLocalDateTime()
        );
    }
}