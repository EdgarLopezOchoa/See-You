package com.example.seeyou.adapters;

public class Usuarios {

    int idusuario;
    String Nombre;
    String Apellido;
    String foto;

    public Usuarios(int idusuario, String nombre, String apellido, String foto) {
        this.idusuario = idusuario;
        Nombre = nombre;
        Apellido = apellido;
        this.foto = foto;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Usuarios{" +
                "idusuario=" + idusuario +
                ", Nombre='" + Nombre + '\'' +
                ", Apellido='" + Apellido + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}
