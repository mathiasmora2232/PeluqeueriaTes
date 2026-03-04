package peluqueria.Models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Factura {
    private int id;
    private int clienteId;
    private String clienteNombre;
    private Timestamp fecha;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;
    private String estado;

    public Factura(int id, int clienteId, String clienteNombre, Timestamp fecha,
                   BigDecimal subtotal, BigDecimal impuesto, BigDecimal total, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.fecha = fecha;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.total = total;
        this.estado = estado;
    }

    public Factura(int clienteId, BigDecimal subtotal, BigDecimal impuesto, BigDecimal total) {
        this.clienteId = clienteId;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.total = total;
        this.estado = "PENDIENTE";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getImpuesto() { return impuesto; }
    public void setImpuesto(BigDecimal impuesto) { this.impuesto = impuesto; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Para mostrar fecha formateada en tabla
    public String getFechaFormateada() {
        if (fecha == null) return "";
        return fecha.toLocalDateTime().toLocalDate().toString() + " " +
               fecha.toLocalDateTime().toLocalTime().withSecond(0).toString();
    }

    @Override
    public String toString() {
        return "Factura #" + id + " - " + clienteNombre;
    }
}
