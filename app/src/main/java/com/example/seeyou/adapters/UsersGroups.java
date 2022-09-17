package com.example.seeyou.adapters;

public class UsersGroups {
    private int id_grupo;
    private int id_usuario;
    private String foto;
    private String Nombre;
    private String Apellido;

    public UsersGroups(int id_grupo, int id_usuario, String foto, String nombre, String apellido) {
        this.id_grupo = id_grupo;
        this.id_usuario = id_usuario;
        this.foto = foto;
        Nombre = nombre;
        Apellido = apellido;
    }

    public int getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(int id_grupo) {
        this.id_grupo = id_grupo;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    @Override
    public String toString() {
        return "UsersGroups{" +
                "id_grupo=" + id_grupo +
                ", id_usuario=" + id_usuario +
                ", foto='" + foto + '\'' +
                ", Nombre='" + Nombre + '\'' +
                ", Apellido='" + Apellido + '\'' +
                '}';
    }
}
