package peluqueria.Models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Pago {
    private int id;
    private int facturaId;
    private String metodoPago;
    private BigDecimal monto;
    private Timestamp fecha;

    public Pago(int facturaId, String metodoPago, BigDecimal monto) {
        this.facturaId = facturaId;
        this.metodoPago = metodoPago;
        this.monto = monto;
    }

    public Pago(int id, int facturaId, String metodoPago, BigDecimal monto, Timestamp fecha) {
        this.id = id;
        this.facturaId = facturaId;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFacturaId() { return facturaId; }
    public void setFacturaId(int facturaId) { this.facturaId = facturaId; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
}
