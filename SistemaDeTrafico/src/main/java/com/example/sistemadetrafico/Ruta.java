package com.example.sistemadetrafico;

public class Ruta {
    private Parada origen;
    private Parada destino;
    private int id;
    private int tiempo;// minutos
    private int distancia;
    private float costo;
    private int transbordos;// cantidad de transbordos

    public Ruta(Parada origen, Parada destino, int tiempo, int distancia, float costo, int transbordos) {
        this.origen = origen;
        this.destino = destino;
        this.tiempo = tiempo;
        this.transbordos = transbordos;
        this.distancia = distancia;
        this.costo = costo;
    }

    public Parada getOrigen() {
        return origen;
    }

    public void setOrigen(Parada origen) {
        this.origen = origen;
    }

    public Parada getDestino() {
        return destino;
    }

    public void setDestino(Parada destino) {
        this.destino = destino;
    }

    public int getId() {
        return id;
    }

    public int getTiempo() {
        return tiempo;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public int getTransbordos() {
        return transbordos;
    }

    public void setTransbordos(int transbordos) {
        this.transbordos = transbordos;
    }
}