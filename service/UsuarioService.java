package service;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import entities.Usuario;
import enums.Rol;
import exception.EntidadNoEncontradaException;
import exception.ReglaNegocioException;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public Usuario crear(String nombre, String apellido, String mail, String celular, String password, Rol rol)
            throws SQLException, ReglaNegocioException {

        validarDatosBasicos(nombre, apellido, mail, password);
        validarMailUnico(mail, null);

        Usuario usuario = new Usuario(nombre.trim(), apellido.trim(),
                mail.trim().toLowerCase(), celular, password, rol);
        return usuarioDAO.guardar(usuario);
    }

    public List<Usuario> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(Long id) throws SQLException, EntidadNoEncontradaException {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw new EntidadNoEncontradaException("No se encontró el usuario con id " + id);
        }
        return usuario;
    }

    public void actualizar(Long id, String nombre, String apellido, String mail,
                           String celular, String password, Rol rol)
            throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {

        Usuario usuario = buscarPorId(id);
        validarDatosBasicos(nombre, apellido, mail, password);
        validarMailUnico(mail, id);

        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setMail(mail.trim().toLowerCase());
        usuario.setCelular(celular);
        usuario.setPassword(password);
        usuario.setRol(rol);

        usuarioDAO.actualizar(usuario);
    }

    public void eliminar(Long id) throws SQLException, EntidadNoEncontradaException {
        buscarPorId(id);
        usuarioDAO.eliminar(id);
    }

    private void validarDatosBasicos(String nombre, String apellido,
                                     String mail, String password) throws ReglaNegocioException {
        if (nombre == null || nombre.isBlank())
            throw new ReglaNegocioException("El nombre del usuario es obligatorio.");
        if (apellido == null || apellido.isBlank())
            throw new ReglaNegocioException("El apellido del usuario es obligatorio.");
        if (mail == null || mail.isBlank() || !mail.contains("@"))
            throw new ReglaNegocioException("El mail no es válido.");
        if (password == null || password.isBlank())
            throw new ReglaNegocioException("La contraseña es obligatoria.");
    }

    private void validarMailUnico(String mail, Long idActual) throws SQLException, ReglaNegocioException {
        Usuario existente = usuarioDAO.buscarPorMail(mail.trim().toLowerCase());
        if (existente != null && !existente.getId().equals(idActual)) {
            throw new ReglaNegocioException("Ya existe un usuario con el mail: " + mail);
        }
    }
}
// Servicio de Usuario con validacion de mail unico
