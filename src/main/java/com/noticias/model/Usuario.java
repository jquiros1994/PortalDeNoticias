package com.noticias.model;


public class Usuario {

    private int idUsuario;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private String rol;
    private int activo;

    public Usuario() {
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}
