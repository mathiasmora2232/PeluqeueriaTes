package peluqueria.Models;

import java.math.BigDecimal;

public class FacturaDetalle {
    private int id;
    private int facturaId;
    private int servicioId;
    private String servicioNombre;
    private int estilistaId;
    private String estilistaNombre;
    private BigDecimal precio;
    private int cantidad;
    private BigDecimal subtotal;

    public FacturaDetalle(int servicioId, String servicioNombre, int estilistaId,
                          String estilistaNombre, BigDecimal precio, int cantidad) {
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.estilistaId = estilistaId;
        this.estilistaNombre = estilistaNombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
    }

    public FacturaDetalle(int id, int facturaId, int servicioId, String servicioNombre,
                          int estilistaId, String estilistaNombre, BigDecimal precio,
                          int cantidad, BigDecimal subtotal) {
        this.id = id;
        this.facturaId = facturaId;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.estilistaId = estilistaId;
        this.estilistaNombre = estilistaNombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFacturaId() { return facturaId; }
    public void setFacturaId(int facturaId) { this.facturaId = facturaId; }
    public int getServicioId() { return servicioId; }
    public void setServicioId(int servicioId) { this.servicioId = servicioId; }
    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }
    public int getEstilistaId() { return estilistaId; }
    public void setEstilistaId(int estilistaId) { this.estilistaId = estilistaId; }
    public String getEstilistaNombre() { return estilistaNombre; }
    public void setEstilistaNombre(String estilistaNombre) { this.estilistaNombre = estilistaNombre; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
