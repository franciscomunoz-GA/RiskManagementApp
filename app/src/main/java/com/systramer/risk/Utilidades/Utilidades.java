package com.systramer.risk.Utilidades;

public class Utilidades {
    public static final String TablaClientes = "Clientes";
    public static final String IdCliente     = "Id";
    public static final String NombreCliente = "Nombre";
    public static final String Clientes      = "CREATE TABLE "+TablaClientes+" ("+IdCliente+" INTEGER, "+NombreCliente+" TEXT)";

    public static final String TablaClienteAreas   = "ClienteAreas";
    public static final String IdClienteArea       = "Id";
    public static final String FKIdCliente         = "IdCliente";
    public static final String NombreClienteArea   = "Nombre";
    public static final String Area                = "Area";
    public static final String ClienteAreas        = "CREATE TABLE "+TablaClienteAreas+" ("+IdClienteArea+" INTEGER, "+FKIdCliente+" INTEGER, "+NombreClienteArea+" TEXT, "+Area+" TEXT)";

    public static final String TablaClienteAreasRiesgos = "ClienteAreasRiesgos";
    public static final String IdClienteAreasRiesgo     = "Id";
    public static final String FKIdClienteArea          = "IdCliente";
    public static final String FKIdClienteArea2         = "IdClienteArea";
    public static final String NombreClienteRiesgo      = "Nombre";
    public static final String ClienteImpacto           = "Impacto";
    public static final String ClienteProbabilidad      = "Probabilidad";
    public static final String ClienteRespondido        = "Respondido";
    public static final String ClienteAreasRiesgos      = "CREATE TABLE "+TablaClienteAreasRiesgos+" ("+IdClienteAreasRiesgo+" INTEGER, "+FKIdClienteArea+" INTEGER,  "+FKIdClienteArea2+" INTEGER, "+NombreClienteRiesgo+" TEXT, "+ClienteImpacto+" INTEGER, "+ClienteProbabilidad+" INTEGER, "+ClienteRespondido+" TEXT)";

    public static final String TablaSitioInteres   = "SitioInteres";
    public static final String IdSitioInteres      = "Id";
    public static final String NombreSitioInteres  = "Nombre";
    public static final String SitioInteres        = "CREATE TABLE "+TablaSitioInteres+" ("+IdSitioInteres+" INTEGER, "+NombreSitioInteres+" TEXT)";

    public static final String TablaSitioInteresRiesgos = "SitioInteresRiesgos";
    public static final String IdSitioInteresRiesgo     = "Id";
    public static final String FKIdSitioInteres         = "IdSitioInteres";
    public static final String NombreSitioInteresRiesgo = "Nombre";
    public static final String SitioInteresImpacto      = "Impacto";
    public static final String SitioInteresProbabilidad = "Probabilidad";
    public static final String SitioInteresRespondido   = "Respondido";
    public static final String SitioInteresRiesgos      = "CREATE TABLE "+TablaSitioInteresRiesgos+" ("+IdSitioInteresRiesgo+" INTEGER, "+FKIdSitioInteres+" INTEGER, "+NombreSitioInteresRiesgo+" TEXT, "+SitioInteresImpacto+" INTEGER, "+SitioInteresProbabilidad+" INTEGER, "+SitioInteresRespondido+" TEXT)";
}
