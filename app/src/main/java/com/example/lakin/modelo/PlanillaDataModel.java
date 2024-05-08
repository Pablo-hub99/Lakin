package com.example.lakin.modelo;

import java.util.List;

public class PlanillaDataModel {
    private String usuario;
    private String lote;

    private String finca;
    private String fechaHora;
    private List<String> plagasSeleccionadas;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getFinca() {
        return finca;
    }

    public void setFinca(String finca) {
        this.finca = finca;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public List<String> getPlagasSeleccionadas() {
        return plagasSeleccionadas;
    }

    public void setPlagasSeleccionadas(List<String> plagasSeleccionadas) {
        this.plagasSeleccionadas = plagasSeleccionadas;
    }
}
