package com.systramer.risk;

public class Cita {
    public int Id;
    public int Imagen;
    public int Tipo;
    public String NombreTipo;
    public String Descripcion;
    public String Fecha;
    public String Hora;
    public String Titulo;

    public Cita(int id, int imagen, int tipo, String nombreTipo, String descripcion, String fecha, String hora, String titulo) {
        Id = id;
        Imagen = imagen;
        Tipo = tipo;
        NombreTipo = nombreTipo;
        Descripcion = descripcion;
        Fecha = fecha;
        Hora = hora;
        Titulo = titulo;
    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getImagen() {
        return Imagen;
    }

    public void setImagen(int imagen) {
        Imagen = imagen;
    }

    public int getTipo() {
        return Tipo;
    }

    public void setTipo(int tipo) {
        Tipo = tipo;
    }

    public String getNombreTipo() {
        return NombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        NombreTipo = nombreTipo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }
}
