package com.systramer.risk;

public class SitioInteresRiesgos{
    public int Id;
    public int Imagen;
    public String Riesgo;
    public int Impacto;
    public int Probabilidad;
    public String Respondido;

    public SitioInteresRiesgos(int id, int imagen, String riesgo, int impacto, int probabilidad, String respondido) {
        Id = id;
        Imagen = imagen;
        Riesgo = riesgo;
        Impacto = impacto;
        Probabilidad = probabilidad;
        Respondido = respondido;
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

    public String getRiesgo() {
        return Riesgo;
    }

    public void setRiesgo(String riesgo) {
        Riesgo = riesgo;
    }

    public int getImpacto() {
        return Impacto;
    }

    public void setImpacto(int impacto) {
        Impacto = impacto;
    }

    public int getProbabilidad() {
        return Probabilidad;
    }

    public void setProbabilidad(int probabilidad) {
        Probabilidad = probabilidad;
    }

    public String getRespondido() {
        return Respondido;
    }

    public void setRespondido(String respondido) {
        Respondido = respondido;
    }
}
