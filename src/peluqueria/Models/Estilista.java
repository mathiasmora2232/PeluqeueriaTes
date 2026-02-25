package peluqueria.Models;

public class Estilista {
    private int id;
    private int idUsuario;
    private String nombre;
    private String telefono;
    private String especialidad;
    private int experienciaAnios;
    private String estado;

    public Estilista(int idUsuario, String nombre, String telefono, String especialidad, int experienciaAnios, String estado) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.experienciaAnios = experienciaAnios;
        this.estado = estado;
    }

    public Estilista(int id, int idUsuario, String nombre, String telefono, String especialidad, int experienciaAnios, String estado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.experienciaAnios = experienciaAnios;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public int getExperienciaAnios() {
        return experienciaAnios;
    }

    public void setExperienciaAnios(int experienciaAnios) {
        this.experienciaAnios = experienciaAnios;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
