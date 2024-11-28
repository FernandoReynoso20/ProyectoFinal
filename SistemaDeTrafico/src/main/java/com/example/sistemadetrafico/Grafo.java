package com.example.sistemadetrafico;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;

public class Grafo {
    private List<Parada> paradas;
    private List<Ruta> rutas;
    private List<List<Parada>> listaAdyacencia;

    public Grafo(List<Parada> paradas) {
        this.paradas = paradas;  // Agregar las paradas pasadas al constructor
        rutas = new ArrayList<>();
        listaAdyacencia = new ArrayList<>();

    }

    public List<Parada> Dijkstra(Parada inicio, Parada destino, String criterio, Pane paneArea) {
        int n = paradas.size();
        boolean[] visitado = new boolean[n];
        Parada[] anterior = new Parada[n];
        float[] valores = new float[n];

        // Inicializar arreglos
        for (int i = 0; i < n; i++) {
            valores[i] = Float.MAX_VALUE;
            visitado[i] = false;
            anterior[i] = null;
        }

        int inicioIndex = paradas.indexOf(inicio);
        valores[inicioIndex] = 0;

        // Encontrar el camino más corto
        for (int cont = 0; cont < n - 1; cont++) {
            float minValor = Float.MAX_VALUE;
            int minIndex = -1;

            // Encontrar el vértice no visitado con el menor valor
            for (int v = 0; v < n; v++) {
                if (!visitado[v] && valores[v] <= minValor) {
                    minValor = valores[v];
                    minIndex = v;
                }
            }

            if (minIndex == -1) break;

            visitado[minIndex] = true;
            Parada paradaActual = paradas.get(minIndex);

            // Actualizar los valores de los vértices adyacentes
            for (Ruta ruta : paradaActual.getRutasConectadas()) {
                int destIndex = paradas.indexOf(ruta.getDestino());
                if (!visitado[destIndex]) {
                    float nuevoValor = 0;

                    // Selección del criterio para calcular el valor
                    switch (criterio) {
                        case "tiempo":
                            nuevoValor = valores[minIndex] + ruta.getTiempo();
                            break;
                        case "costo":
                            nuevoValor = valores[minIndex] + ruta.getCosto();
                            break;
                        case "transbordos":
                            nuevoValor = valores[minIndex] + ruta.getTransbordos(); // Cada ruta añade un transbordo
                            break;
                        default: // Combinación general
                            nuevoValor = valores[minIndex] + ruta.getTiempo() + ruta.getCosto();
                            break;
                    }

                    // Actualización del costo si se encuentra un valor más bajo
                    if (nuevoValor < valores[destIndex]) {
                        valores[destIndex] = nuevoValor;
                        anterior[destIndex] = paradaActual;
                    }
                }
            }
        }

        // Construir el trayecto
        List<Parada> trayecto = new ArrayList<>();
        Parada actual = destino;
        while (actual != null) {
            trayecto.add(0, actual);
            int actualIndice = paradas.indexOf(actual);
            actual = anterior[actualIndice];
        }

        // Cambiar el color de las líneas en el trayecto
        if (trayecto.size() > 1) {
            for (int i = 0; i < trayecto.size() - 1; i++) {
                Parada origen = trayecto.get(i);
                Parada destinoRuta = trayecto.get(i + 1);

                // Buscar la línea en el pane y cambiar su color
                Line linea = (Line) paneArea.lookup("#Ruta-" + origen.getNumero() + "-" + destinoRuta.getNumero());
                if (linea != null) {
                    linea.setStroke(Color.GREEN);  // Cambiar el color a verde
                }
            }
        }

        return trayecto;
    }

    public List<Parada> getParadas() {
        return paradas;
    }

    public List<Ruta> getRutas() {
        return rutas;
    }
}
