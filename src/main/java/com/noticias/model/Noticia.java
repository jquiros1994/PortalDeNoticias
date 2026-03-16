package com.noticias.model;

import java.util.Date;

public class Noticia {

    private int idNoticia;
    private String titulo;
    private String cuerpo;
    private String estado;
    private Date fechaCreacion;
    private Date fechaPublicacion;
    private double promedioCalificacion;
    private int idAutor;
    private int idTema;
    private int idSubtema;

    private String nombreTema;
    private String nombreSubtema;
    private String nombreAutor;

    public Noticia() {
    }

    public int getIdNoticia() { return idNoticia; }
    public void setIdNoticia(int idNoticia) { this.idNoticia = idNoticia; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCuerpo() { return cuerpo; }
    public void setCuerpo(String cuerpo) { this.cuerpo = cuerpo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Date getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Date fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public double getPromedioCalificacion() { return promedioCalificacion; }
    public void setPromedioCalificacion(double promedioCalificacion) { this.promedioCalificacion = promedioCalificacion; }

    public int getIdAutor() { return idAutor; }
    public void setIdAutor(int idAutor) { this.idAutor = idAutor; }

    public int getIdTema() { return idTema; }
    public void setIdTema(int idTema) { this.idTema = idTema; }

    public int getIdSubtema() { return idSubtema; }
    public void setIdSubtema(int idSubtema) { this.idSubtema = idSubtema; }

    public String getNombreTema() { return nombreTema; }
    public void setNombreTema(String nombreTema) { this.nombreTema = nombreTema; }

    public String getNombreSubtema() { return nombreSubtema; }
    public void setNombreSubtema(String nombreSubtema) { this.nombreSubtema = nombreSubtema; }

    public String getNombreAutor() { return nombreAutor; }
    public void setNombreAutor(String nombreAutor) { this.nombreAutor = nombreAutor; }
}
