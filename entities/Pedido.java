package entities;

import enums.Estado;
import enums.FormaPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido extends Base implements Calculable {

    private Usuario usuario;
    private LocalDate fecha;
    private Estado estado;
    private FormaPago formaPago;
    private List<DetallePedido> detalles;
    private BigDecimal total;

    public Pedido() {
        super();
        this.detalles = new ArrayList<>();
        this.estado = Estado.PENDIENTE;
        this.fecha = LocalDate.now();
        this.total = BigDecimal.ZERO;
    }

    public Pedido(Usuario usuario, FormaPago formaPago) {
        this();
        this.usuario = usuario;
        this.formaPago = formaPago;
    }

    public Pedido(Long id, Usuario usuario, LocalDate fecha, Estado estado, FormaPago formaPago,
                  List<DetallePedido> detalles, boolean eliminado, LocalDateTime createdAt) {
        super(id, eliminado, createdAt);
        this.usuario = usuario;
        this.fecha = fecha;
        this.estado = estado;
        this.formaPago = formaPago;
        this.detalles = detalles != null ? detalles : new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }

    /**
     * Método obligatorio según la consigna.
     * Crea el DetallePedido internamente y lo agrega a la lista.
     */
    public void addDetallePedido(int cantidad, Double precio, Producto producto) {
        DetallePedido detalle = new DetallePedido(
                this, producto, cantidad, BigDecimal.valueOf(precio));
        this.detalles.add(detalle);
        calcularTotal();
    }

    @Override
    public void calcularTotal() {
        this.total = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public FormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(FormaPago formaPago) { this.formaPago = formaPago; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
        calcularTotal();
    }

    @Override
    public String toString() {
        String nombreUsuario = usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : "N/A";
        return "Pedido{id=%d, usuario='%s', fecha=%s, estado=%s, formaPago=%s, items=%d, total=%s}"
                .formatted(id, nombreUsuario, fecha, estado, formaPago, detalles.size(), total);
    }
}