package com.noticias.model;

public class Tema {

    private int idTema;
    private String nombre;
    private String descripcion;

    public Tema() {
    }

    public int getIdTema() { return idTema; }
    public void setIdTema(int idTema) { this.idTema = idTema; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
