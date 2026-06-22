import entities.Categoria;
import entities.Pedido;
import entities.Producto;
import entities.Usuario;
import enums.Estado;
import enums.FormaPago;
import enums.Rol;
import exception.EntidadNoEncontradaException;
import exception.ReglaNegocioException;
import service.CategoriaService;
import service.PedidoService;
import service.ProductoService;
import service.UsuarioService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final CategoriaService categoriaService = new CategoriaService();
    private static final ProductoService  productoService  = new ProductoService();
    private static final UsuarioService   usuarioService   = new UsuarioService();
    private static final PedidoService    pedidoService    = new PedidoService();

    public static void main(String[] args) {
        System.out.println("=== Bienvenido a Food Store ===");
        boolean salir = false;

        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1 -> menuCategorias();
                    case 2 -> menuProductos();
                    case 3 -> menuUsuarios();
                    case 4 -> menuPedidos();
                    case 0 -> { salir = true; System.out.println("¡Hasta luego!"); }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("===== MENÚ PRINCIPAL =====");
        System.out.println("1. Categorías");
        System.out.println("2. Productos");
        System.out.println("3. Usuarios");
        System.out.println("4. Pedidos");
        System.out.println("0. Salir");
    }

    // ================================================================
    // CATEGORÍAS
    // ================================================================

    private static void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- CATEGORÍAS ---");
            System.out.println("1. Listar  2. Crear  3. Editar  4. Eliminar  0. Volver");
            int op = leerEntero("Seleccione: ");
            try {
                switch (op) {
                    case 1 -> listarCategorias();
                    case 2 -> crearCategoria();
                    case 3 -> editarCategoria();
                    case 4 -> eliminarCategoria();
                    case 0 -> volver = true;
                    default -> System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (ReglaNegocioException | EntidadNoEncontradaException e) {
                System.out.println("Aviso: " + e.getMessage());
            }
        }
    }

    private static void listarCategorias() throws SQLException {
        List<Categoria> lista = categoriaService.listarTodos();
        if (lista.isEmpty()) { System.out.println("No hay categorías registradas."); return; }
        lista.forEach(System.out::println);
    }

    private static void crearCategoria() throws SQLException, ReglaNegocioException {
        String nombre      = leerTexto("Nombre: ");
        String descripcion = leerTexto("Descripción: ");
        Categoria c = categoriaService.crear(nombre, descripcion);
        System.out.println("Creada: " + c);
    }

    private static void editarCategoria() throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {
        Long   id          = leerLong("Id a editar: ");
        String nombre      = leerTexto("Nuevo nombre: ");
        String descripcion = leerTexto("Nueva descripción: ");
        categoriaService.actualizar(id, nombre, descripcion);
        System.out.println("Categoría actualizada.");
    }

    private static void eliminarCategoria() throws SQLException, EntidadNoEncontradaException {
        Long id = leerLong("Id a eliminar: ");
        if (!confirmar()) { System.out.println("Operación cancelada."); return; }
        categoriaService.eliminar(id);
        System.out.println("Categoría eliminada.");
    }

    // ================================================================
    // PRODUCTOS
    // ================================================================

    private static void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- PRODUCTOS ---");
            System.out.println("1. Listar  2. Crear  3. Editar  4. Eliminar  0. Volver");
            int op = leerEntero("Seleccione: ");
            try {
                switch (op) {
                    case 1 -> listarProductos();
                    case 2 -> crearProducto();
                    case 3 -> editarProducto();
                    case 4 -> eliminarProducto();
                    case 0 -> volver = true;
                    default -> System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (ReglaNegocioException | EntidadNoEncontradaException e) {
                System.out.println("Aviso: " + e.getMessage());
            }
        }
    }

    private static void listarProductos() throws SQLException {
        List<Producto> lista = productoService.listarTodos();
        if (lista.isEmpty()) { System.out.println("No hay productos registrados."); return; }
        lista.forEach(System.out::println);
    }

    private static void crearProducto() throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {
        listarCategorias();
        String     nombre      = leerTexto("Nombre: ");
        String     descripcion = leerTexto("Descripción: ");
        BigDecimal precio      = leerBigDecimal("Precio: ");
        int        stock       = leerEntero("Stock: ");
        String     imagen      = leerTexto("Imagen (URL o nombre de archivo): ");
        boolean    disponible  = leerSiNo("¿Disponible?");
        Long       catId       = leerLong("Id de categoría: ");

        Producto p = productoService.crear(nombre, descripcion, precio, stock, imagen, disponible, catId);
        System.out.println("Creado: " + p);
    }

    private static void editarProducto() throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {
        Long       id          = leerLong("Id a editar: ");
        String     nombre      = leerTexto("Nuevo nombre: ");
        String     descripcion = leerTexto("Nueva descripción: ");
        BigDecimal precio      = leerBigDecimal("Nuevo precio: ");
        int        stock       = leerEntero("Nuevo stock: ");
        String     imagen      = leerTexto("Nueva imagen: ");
        boolean    disponible  = leerSiNo("¿Disponible?");
        Long       catId       = leerLong("Id de categoría: ");

        productoService.actualizar(id, nombre, descripcion, precio, stock, imagen, disponible, catId);
        System.out.println("Producto actualizado.");
    }

    private static void eliminarProducto() throws SQLException, EntidadNoEncontradaException {
        Long id = leerLong("Id a eliminar: ");
        if (!confirmar()) { System.out.println("Operación cancelada."); return; }
        productoService.eliminar(id);
        System.out.println("Producto eliminado.");
    }

    // ================================================================
    // USUARIOS
    // ================================================================

    private static void menuUsuarios() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- USUARIOS ---");
            System.out.println("1. Listar  2. Crear  3. Editar  4. Eliminar  0. Volver");
            int op = leerEntero("Seleccione: ");
            try {
                switch (op) {
                    case 1 -> listarUsuarios();
                    case 2 -> crearUsuario();
                    case 3 -> editarUsuario();
                    case 4 -> eliminarUsuario();
                    case 0 -> volver = true;
                    default -> System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (ReglaNegocioException | EntidadNoEncontradaException e) {
                System.out.println("Aviso: " + e.getMessage());
            }
        }
    }

    private static void listarUsuarios() throws SQLException {
        List<Usuario> lista = usuarioService.listarTodos();
        if (lista.isEmpty()) { System.out.println("No hay usuarios registrados."); return; }
        lista.forEach(System.out::println);
    }

    private static void crearUsuario() throws SQLException, ReglaNegocioException {
        String nombre   = leerTexto("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        String mail     = leerTexto("Mail: ");
        String celular  = leerTexto("Celular: ");
        String password = leerTexto("Contraseña: ");
        Rol    rol      = leerRol();

        Usuario u = usuarioService.crear(nombre, apellido, mail, celular, password, rol);
        System.out.println("Creado: " + u);
    }

    private static void editarUsuario() throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {
        Long   id       = leerLong("Id a editar: ");
        String nombre   = leerTexto("Nuevo nombre: ");
        String apellido = leerTexto("Nuevo apellido: ");
        String mail     = leerTexto("Nuevo mail: ");
        String celular  = leerTexto("Nuevo celular: ");
        String password = leerTexto("Nueva contraseña: ");
        Rol    rol      = leerRol();

        usuarioService.actualizar(id, nombre, apellido, mail, celular, password, rol);
        System.out.println("Usuario actualizado.");
    }

    private static void eliminarUsuario() throws SQLException, EntidadNoEncontradaException {
        Long id = leerLong("Id a eliminar: ");
        if (!confirmar()) { System.out.println("Operación cancelada."); return; }
        usuarioService.eliminar(id);
        System.out.println("Usuario eliminado.");
    }

    private static Rol leerRol() {
        System.out.println("Rol: 1. ADMIN  2. USUARIO");
        return leerEntero("Seleccione: ") == 1 ? Rol.ADMIN : Rol.USUARIO;
    }

    // ================================================================
    // PEDIDOS
    // ================================================================

    private static void menuPedidos() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- PEDIDOS ---");
            System.out.println("1. Listar  2. Crear  3. Cambiar estado  4. Eliminar  0. Volver");
            int op = leerEntero("Seleccione: ");
            try {
                switch (op) {
                    case 1 -> listarPedidos();
                    case 2 -> crearPedido();
                    case 3 -> editarEstadoPedido();
                    case 4 -> eliminarPedido();
                    case 0 -> volver = true;
                    default -> System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (ReglaNegocioException | EntidadNoEncontradaException e) {
                System.out.println("Aviso: " + e.getMessage());
            }
        }
    }

    private static void listarPedidos() throws SQLException {
        List<Pedido> lista = pedidoService.listarTodos();
        if (lista.isEmpty()) { System.out.println("No hay pedidos registrados."); return; }
        for (Pedido p : lista) {
            System.out.println(p);
            p.getDetalles().forEach(d -> System.out.println("   -> " + d));
        }
    }

    private static void crearPedido() throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {
        listarUsuarios();
        Long usuarioId = leerLong("Id del usuario: ");

        System.out.println("Forma de pago: 1. TARJETA  2. TRANSFERENCIA  3. EFECTIVO");
        FormaPago formaPago = switch (leerEntero("Seleccione: ")) {
            case 1 -> FormaPago.TARJETA;
            case 2 -> FormaPago.TRANSFERENCIA;
            default -> FormaPago.EFECTIVO;
        };

        listarProductos();
        Map<Long, Integer> items = new LinkedHashMap<>();
        do {
            Long productoId = leerLong("Id del producto: ");
            int  cantidad   = leerEntero("Cantidad: ");
            items.put(productoId, cantidad);
        } while (leerSiNo("¿Agregar otro producto?"));

        Pedido pedido = pedidoService.crear(usuarioId, formaPago, items);
        System.out.println("Pedido creado: " + pedido);
    }

    private static void editarEstadoPedido() throws SQLException, ReglaNegocioException, EntidadNoEncontradaException {
        Long id = leerLong("Id del pedido: ");
        System.out.println("1. PENDIENTE  2. CONFIRMADO  3. TERMINADO  4. CANCELADO");
        Estado estado = switch (leerEntero("Nuevo estado: ")) {
            case 1 -> Estado.PENDIENTE;
            case 2 -> Estado.CONFIRMADO;
            case 3 -> Estado.TERMINADO;
            default -> Estado.CANCELADO;
        };
        pedidoService.actualizarEstado(id, estado);
        System.out.println("Estado actualizado.");
    }

    private static void eliminarPedido() throws SQLException, EntidadNoEncontradaException {
        Long id = leerLong("Id a eliminar: ");
        if (!confirmar()) { System.out.println("Operación cancelada."); return; }
        pedidoService.eliminar(id);
        System.out.println("Pedido eliminado.");
    }

    // ================================================================
    // UTILIDADES
    // ================================================================

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Ingrese un número entero válido."); }
        }
    }

    private static Long leerLong(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try { return Long.parseLong(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Ingrese un número válido."); }
        }
    }

    private static BigDecimal leerBigDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try { return new BigDecimal(scanner.nextLine().trim().replace(",", ".")); }
            catch (NumberFormatException e) { System.out.println("Ingrese un valor numérico válido (ej: 10.50)."); }
        }
    }

    /** Pide confirmación S/N. Retorna true solo si el usuario escribe 'S' o 's'. */
    private static boolean confirmar() {
        System.out.print("¿Confirma la operación? (S/N): ");
        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }

    /** Pide una respuesta S/N para una pregunta y retorna el booleano equivalente. */
    private static boolean leerSiNo(String pregunta) {
        System.out.print(pregunta + " (S/N): ");
        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }
}