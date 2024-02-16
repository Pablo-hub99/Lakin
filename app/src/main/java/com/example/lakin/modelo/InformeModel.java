package com.example.lakin.modelo;

public class InformeModel {
    private String id;
    private String usuario;
    private String fechaHora;

    public InformeModel(String id, String usuario, String fechaHora) {
        this.id = id;
        this.usuario = usuario;
        this.fechaHora = fechaHora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}

