package com.example.seeyou.adapters;

public class FechasRutas {
    String fecha_rutas;
    String mes;
    String dia;

    public FechasRutas(String fecha_rutas, String mes, String dia) {
        this.fecha_rutas = fecha_rutas;
        this.mes = mes;
        this.dia = dia;
    }

    public String getFecha_rutas() {
        return fecha_rutas;
    }

    public void setFecha_rutas(String fecha_rutas) {
        this.fecha_rutas = fecha_rutas;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    @Override
    public String toString() {
        return "FechasRutas{" +
                "fecha_rutas='" + fecha_rutas + '\'' +
                ", mes='" + mes + '\'' +
                ", dia='" + dia + '\'' +
                '}';
    }
}
