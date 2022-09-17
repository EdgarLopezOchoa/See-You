package com.example.seeyou.adapters;

public class Grupos {

    private String Nombre;
    private int id;
    private String Usuarios;
    private String codigo;
    private int id_admin;

    public Grupos(String nombre, int id, String usuarios, String codigo, int id_admin) {
        Nombre = nombre;
        this.id = id;
        Usuarios = usuarios;
        this.codigo = codigo;
        this.id_admin = id_admin;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuarios() {
        return Usuarios;
    }

    public void setUsuarios(String usuarios) {
        Usuarios = usuarios;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getId_admin() {
        return id_admin;
    }

    public void setId_admin(int id_admin) {
        this.id_admin = id_admin;
    }

    @Override
    public String toString() {
        return "Grupos{" +
                "Nombre='" + Nombre + '\'' +
                ", id=" + id +
                ", Usuarios='" + Usuarios + '\'' +
                ", codigo='" + codigo + '\'' +
                ", id_admin=" + id_admin +
                '}';
    }
}
