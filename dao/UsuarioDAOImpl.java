package dao;

import config.ConexionDB;
import entities.Usuario;
import enums.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SELECT_BASE =
            "SELECT id, nombre, apellido, mail, celular, password, rol, eliminado, createdAt FROM usuarios ";

    @Override
    public Usuario guardar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, apellido, mail, celular, password, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getMail());
            ps.setString(4, usuario.getCelular());
            ps.setString(5, usuario.getPassword());
            ps.setString(6, usuario.getRol().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) usuario.setId(rs.getLong(1));
            }
        }
        return usuario;
    }

    @Override
    public Usuario buscarPorId(Long id) throws SQLException {
        String sql = SELECT_BASE + "WHERE id = ? AND eliminado = false";
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
    public Usuario buscarPorMail(String mail) throws SQLException {
        String sql = SELECT_BASE + "WHERE mail = ? AND eliminado = false";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        String sql = SELECT_BASE + "WHERE eliminado = false ORDER BY apellido, nombre";
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre=?, apellido=?, mail=?, celular=?, password=?, rol=? " +
                "WHERE id=? AND eliminado=false";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getMail());
            ps.setString(4, usuario.getCelular());
            ps.setString(5, usuario.getPassword());
            ps.setString(6, usuario.getRol().name());
            ps.setLong(7, usuario.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        String sql = "UPDATE usuarios SET eliminado=true WHERE id=?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("mail"),
                rs.getString("celular"),
                rs.getString("password"),
                Rol.valueOf(rs.getString("rol")),
                rs.getBoolean("eliminado"),
                rs.getTimestamp("createdAt").toLocalDateTime()
        );
    }
}