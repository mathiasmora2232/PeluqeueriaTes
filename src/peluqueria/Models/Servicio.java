package peluqueria.Models;

import java.math.BigDecimal;

public class Servicio {
    private int id;
    private String nombre;
    private BigDecimal precio;
    private Integer duracionMin;
    private String descripcion;

    public Servicio(int id, String nombre, BigDecimal precio, Integer duracionMin, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.duracionMin = duracionMin;
        this.descripcion = descripcion;
    }

    public Servicio(String nombre, BigDecimal precio, Integer duracionMin, String descripcion) {
        this.nombre = nombre;
        this.precio = precio;
        this.duracionMin = duracionMin;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(Integer duracionMin) {
        this.duracionMin = duracionMin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
