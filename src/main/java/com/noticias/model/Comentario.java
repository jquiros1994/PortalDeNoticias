package com.noticias.model;

import java.util.Date;

public class Comentario {

    private int idComentario;
    private String contenido;
    private Date fechaComentario;
    private int idNoticia;
    private int idUsuario;
    private String nombreAutor;

    public Comentario() {
    }

    public int getIdComentario() { return idComentario; }
    public void setIdComentario(int idComentario) { this.idComentario = idComentario; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Date getFechaComentario() { return fechaComentario; }
    public void setFechaComentario(Date fechaComentario) { this.fechaComentario = fechaComentario; }

    public int getIdNoticia() { return idNoticia; }
    public void setIdNoticia(int idNoticia) { this.idNoticia = idNoticia; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreAutor() { return nombreAutor; }
    public void setNombreAutor(String nombreAutor) { this.nombreAutor = nombreAutor; }
}
