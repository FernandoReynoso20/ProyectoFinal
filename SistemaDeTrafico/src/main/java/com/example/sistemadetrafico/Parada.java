package com.example.sistemadetrafico;

import java.util.*;

public class Parada {
    private int numero;
    private double x; // Coordenada X
    private double y; // Coordenada Y
    private List<Ruta> rutasConectadas;

    // Constructor
    public Parada(int numero, double x, double y) {
        this.numero = numero;
        this.x = x;
        this.y = y;
        this.rutasConectadas = new ArrayList<>();
    }

    // Getter y Setter para el número de parada
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    // Getter y Setter para la coordenada X
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    // Getter y Setter para la coordenada Y
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    // Métodos para manejar las rutas conectadas
    public List<Ruta> getRutasConectadas() {
        return rutasConectadas;
    }

    public void agregarRuta(Ruta ruta) {
        rutasConectadas.add(ruta);
    }

    public void eliminarRuta(Ruta ruta) {
        rutasConectadas.remove(ruta);
    }

    // Sobrescritura de toString para una representación más legible
    @Override
    public String toString() {
        return "Parada #" + numero + " (" + x + ", " + y + ")";
    }
}
