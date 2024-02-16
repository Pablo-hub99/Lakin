package com.example.lakin.modelo;

public class PlagasModel {
    // Propiedades de una plaga
    String Descripcion, Fecha_Fin, Fecha_Inicio, nombre;
    boolean isSelected;

    // Constructor vacío requerido para Firebase
    public PlagasModel() {}

    // Constructor personalizado para inicializar propiedades
    public PlagasModel(String Descripcion, String Fecha_Fin, String Fecha_Inicio, String nombre) {
        this.Descripcion = Descripcion;
        this.Fecha_Fin = Fecha_Fin;
        this.Fecha_Inicio = Fecha_Inicio;
        this.nombre = nombre;
        this.isSelected = false;
    }

    // Getter y Setter para la propiedad Descripcion
    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    // Getter y Setter para la propiedad Fecha_Fin
    public String getFecha_Fin() {
        return Fecha_Fin;
    }

    public void setFecha_Fin(String fecha_Fin) {
        Fecha_Fin = fecha_Fin;
    }

    // Getter y Setter para la propiedad Fecha_Inicio
    public String getFecha_Inicio() {
        return Fecha_Inicio;
    }

    public void setFecha_Inicio(String fecha_Inicio) {
        Fecha_Inicio = fecha_Inicio;
    }

    // Getter y Setter para la propiedad nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}