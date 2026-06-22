package service;

import dao.CategoriaDAO;
import dao.CategoriaDAOImpl;
import entities.Categoria;
import exception.EntidadNoEncontradaException;
import exception.ReglaNegocioException;

import java.sql.SQLException;
import java.util.List;

public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    public Categoria crear(String nombre, String descripcion) throws SQLException, ReglaNegocioException {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre de la categoría es obligatorio.");
        }
        Categoria categoria = new Categoria(nombre.trim(), descripcion);
        return categoriaDAO.guardar(categoria);
    }

    public List<Categoria> listarTodos() throws SQLException {
        return categoriaDAO.listarTodos();
    }

    public Categoria buscarPorId(Long id) throws SQLException, EntidadNoEncontradaException {
        Categoria categoria = categoriaDAO.buscarPorId(id);
        if (categoria == null) {
            throw new EntidadNoEncontradaException("No se encontró la categoría con id " + id);
        }
        return categoria;
    }

    public void actualizar(Long id, String nombre, String descripcion)
            throws SQLException, EntidadNoEncontradaException, ReglaNegocioException {

        Categoria categoria = buscarPorId(id); // valida existencia
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre de la categoría es obligatorio.");
        }
        categoria.setNombre(nombre.trim());
        categoria.setDescripcion(descripcion);
        categoriaDAO.actualizar(categoria);
    }

    public void eliminar(Long id) throws SQLException, EntidadNoEncontradaException {
        buscarPorId(id); // valida existencia antes de hacer el soft delete
        categoriaDAO.eliminar(id);
    }
}
// Servicio de Categoria con validaciones de negocio
