package service;

import dao.ProductoDAO;
import dao.ProductoDAOImpl;
import entities.Categoria;
import entities.Producto;
import exception.EntidadNoEncontradaException;
import exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductoService {

    private final ProductoDAO productoDAO;
    private final CategoriaService categoriaService;

    public ProductoService() {
        this.productoDAO = new ProductoDAOImpl();
        this.categoriaService = new CategoriaService();
    }

    public Producto crear(String nombre, String descripcion, BigDecimal precio, Integer stock,
                          String imagen, Boolean disponible, Long categoriaId)
            throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {

        validarDatos(nombre, precio, stock);
        Categoria categoria = categoriaService.buscarPorId(categoriaId);

        Producto producto = new Producto(nombre.trim(), descripcion, precio, stock, imagen, disponible, categoria);
        return productoDAO.guardar(producto);
    }

    public List<Producto> listarTodos() throws SQLException {
        return productoDAO.listarTodos();
    }

    public List<Producto> listarPorCategoria(Long categoriaId) throws SQLException {
        return productoDAO.listarPorCategoria(categoriaId);
    }

    public Producto buscarPorId(Long id) throws SQLException, EntidadNoEncontradaException {
        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            throw new EntidadNoEncontradaException("No se encontró el producto con id " + id);
        }
        return producto;
    }

    public void actualizar(Long id, String nombre, String descripcion, BigDecimal precio,
                           Integer stock, String imagen, Boolean disponible, Long categoriaId)
            throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {

        Producto producto = buscarPorId(id);
        validarDatos(nombre, precio, stock);
        Categoria categoria = categoriaService.buscarPorId(categoriaId);

        producto.setNombre(nombre.trim());
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setImagen(imagen);
        producto.setDisponible(disponible);
        producto.setCategoria(categoria);

        productoDAO.actualizar(producto);
    }

    public void eliminar(Long id) throws SQLException, EntidadNoEncontradaException {
        buscarPorId(id);
        productoDAO.eliminar(id);
    }

    private void validarDatos(String nombre, BigDecimal precio, Integer stock) throws ReglaNegocioException {
        if (nombre == null || nombre.isBlank())
            throw new ReglaNegocioException("El nombre del producto es obligatorio.");
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0)
            throw new ReglaNegocioException("El precio no puede ser negativo.");
        if (stock == null || stock < 0)
            throw new ReglaNegocioException("El stock no puede ser negativo.");
    }
}
// Servicio de Producto con validacion de precio y stock
