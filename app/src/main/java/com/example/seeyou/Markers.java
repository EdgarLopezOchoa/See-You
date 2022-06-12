package com.example.seeyou;

import androidx.recyclerview.widget.RecyclerView;

public class Markers {
    private int id;
    private String name;
    private String Ubicacion;
    private String coordenadas;
    private String descripction;
    private String titulo;

    public Markers(int id, String name, String ubicacion, String coordenadas, String descripction, String titulo) {
        this.id = id;
        this.name = name;
        Ubicacion = ubicacion;
        this.coordenadas = coordenadas;
        this.descripction = descripction;
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUbicacion() {
        return Ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        Ubicacion = ubicacion;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getDescripction() {
        return descripction;
    }

    public void setDescripction(String descripction) {
        this.descripction = descripction;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public String toString() {
        return "Markers{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", Ubicacion='" + Ubicacion + '\'' +
                ", coordenadas='" + coordenadas + '\'' +
                ", descripction='" + descripction + '\'' +
                ", titulo='" + titulo + '\'' +
                '}';
    }
}
