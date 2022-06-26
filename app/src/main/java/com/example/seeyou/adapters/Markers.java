package com.example.seeyou.adapters;

import androidx.recyclerview.widget.RecyclerView;

public class Markers {
    private int id;
    private String Nombre;

    private double Longitud;
    private double Latitud;
    private String direccion;
    private String descripcion;

    public Markers(int id, String nombre, double Longitud, double Latitud, String direccion, String descripcion) {
        this.id = id;
        Nombre = nombre;
        this.Longitud = Longitud;
        this.Latitud = Latitud;
        this.direccion = direccion;
        this.descripcion = descripcion;
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
        this.Longitud = longitud;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        this.Latitud = latitud;
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

    @Override
    public String toString() {
        return "Markers{" +
                "id=" + id +
                ", Nombre='" + Nombre + '\'' +
                ", longitud='" + Longitud + '\'' +
                ", latitud='" + Latitud + '\'' +
                ", direccion='" + direccion + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
