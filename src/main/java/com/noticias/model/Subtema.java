package com.noticias.model;

public class Subtema {

    private int idSubtema;
    private String nombre;
    private int idTema;
    private String nombreTema;

    public Subtema() {
    }

    public int getIdSubtema() { return idSubtema; }
    public void setIdSubtema(int idSubtema) { this.idSubtema = idSubtema; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getIdTema() { return idTema; }
    public void setIdTema(int idTema) { this.idTema = idTema; }

    public String getNombreTema() { return nombreTema; }
    public void setNombreTema(String nombreTema) { this.nombreTema = nombreTema; }
}
