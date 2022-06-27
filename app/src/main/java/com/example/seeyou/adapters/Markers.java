package com.example.seeyou.adapters;

import androidx.recyclerview.widget.RecyclerView;

public class Markers {
    private int id;
    private String Nombre;

    private double Longitud;
    private double Latitud;
    private String direccion;
    private String descripcion;
    private String habilitado;

    public Markers(String habilitado,int id, String nombre, double longitud, double latitud, String direccion, String descripcion) {
        this.id = id;
        Nombre = nombre;
        Longitud = longitud;
        Latitud = latitud;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.habilitado = habilitado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(String habilitado) {
        this.habilitado = habilitado;
    }

    @Override
    public String toString() {
        return "Markers{" +
                "id=" + id +
                ", Nombre='" + Nombre + '\'' +
                ", Longitud=" + Longitud +
                ", Latitud=" + Latitud +
                ", direccion='" + direccion + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", habilitado='" + habilitado + '\'' +
                '}';
    }
}
