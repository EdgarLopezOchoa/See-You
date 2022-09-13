package com.example.seeyou.adapters;

public class FechasRutas {
    String fecha_rutas;

    public FechasRutas(String fecha_rutas) {
        this.fecha_rutas = fecha_rutas;
    }

    public String getFecha_rutas() {
        return fecha_rutas;
    }

    public void setFecha_rutas(String fecha_rutas) {
        this.fecha_rutas = fecha_rutas;
    }

    @Override
    public String toString() {
        return "FechasRutas{" +
                "fecha_rutas='" + fecha_rutas + '\'' +
                '}';
    }
}
