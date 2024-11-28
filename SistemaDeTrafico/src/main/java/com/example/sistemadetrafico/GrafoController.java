package com.example.sistemadetrafico;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GrafoController {
    @FXML
    private MenuItem menuItemAgParada;
    @FXML
    private Pane paneArea;
    @FXML
    private MenuItem menuItemAgRuta;
    @FXML
    private MenuItem menuItemEliminarRuta;
    @FXML
    private MenuItem menuItemEliminarParada;
    @FXML
    private MenuItem menuItemModificarRuta;
    @FXML
    private MenuItem menuItemModificarParada;
    @FXML
    private ComboBox<String> cmbGestionador;
    @FXML
    private Button btnBuscarRuta;

    private ParadaManager paradaManager;
    private RutaManager rutaManager;
    private Grafo grafo;
    private List<Parada> paradasSeleccionadas = new ArrayList<>();
    private String eleccionCriterio = "general";

    private Parada Origen; // Variable para la parada de origen
    private Parada Destino;

    public GrafoController() {
    }

    @FXML
    public void initialize() {
        // Inicializar los managers
        paradaManager = new ParadaManager(paneArea);
        rutaManager = new RutaManager(paradaManager.getParadas());
        grafo = new Grafo(paradaManager.getParadas());

        // Configurar los eventos de menú
        menuItemAgParada.setOnAction(event -> paradaManager.abrirVentanaAgregarParada());
        menuItemAgRuta.setOnAction(event -> rutaManager.abrirVentanaAgregarRuta(paneArea));  // Asegurarse de pasar el paneArea
        menuItemEliminarParada.setOnAction(event -> paradaManager.abrirVentanaEliminarParada());
        menuItemEliminarRuta.setOnAction(event -> rutaManager.abrirVentanaEliminarRuta(paneArea)); // Asegúrate de pasar paneArea
        menuItemModificarParada.setOnAction(event -> paradaManager.abrirVentanaModificarParada());
        menuItemModificarRuta.setOnAction(event -> rutaManager.abrirVentanaModificarRuta(paneArea));

        btnBuscarRuta.setDisable(true);
        configurarEventoSeleccionParada();
        configurarComboBoxGestionador();
    }

    private void configurarEventoSeleccionParada() {
        paneArea.setOnMouseClicked(event -> {
            if (event.getTarget() instanceof Circle circle) {
                String idParada = circle.getId();
                if (idParada != null && idParada.startsWith("Parada-")) {
                    int numeroParada = Integer.parseInt(idParada.replace("Parada-", ""));
                    Parada parada = paradaManager.buscarParada(numeroParada);

                    if (parada != null) {
                        manejarSeleccionParada(parada, circle);
                    }
                }
            }
        });
    }

    private void manejarSeleccionParada(Parada parada, Circle circle) {
        if (paradasSeleccionadas.contains(parada)) {
            // Deseleccionar parada
            paradasSeleccionadas.remove(parada);
            circle.setFill(Color.RED);
        } else {
            // Seleccionar nueva parada
            if (paradasSeleccionadas.size() >= 2) {
                // Deseleccionar la primera parada de la lista
                Parada primeraParada = paradasSeleccionadas.get(0);
                Circle primerCirculo = (Circle) paneArea.lookup("#Parada-" + primeraParada.getNumero());
                if (primerCirculo != null) {
                    primerCirculo.setFill(Color.RED);
                }
                paradasSeleccionadas.remove(0);
            }

            paradasSeleccionadas.add(parada);
            circle.setFill(Color.GREEN);
        }

        if (paradasSeleccionadas.size() == 2) {
            Origen = paradasSeleccionadas.get(0);
            Destino = paradasSeleccionadas.get(1);
        } else {
            Origen = null;
            Destino = null;
        }
        // Actualizar el estado del botón
        btnBuscarRuta.setDisable(paradasSeleccionadas.size() != 2);
        btnBuscarRuta.setOnAction(event -> {

                // Verifica que las paradas de inicio y destino estén seleccionadas
                if (Origen == null || Destino == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Advertencia");
                    alert.setHeaderText("Paradas no seleccionadas");
                    alert.setContentText("Por favor, selecciona una parada de inicio y una de destino.");
                    alert.showAndWait();
                    return;
                }
                grafo.Dijkstra(Origen, Destino, eleccionCriterio, paneArea);
        });
    }

    private void configurarComboBoxGestionador() {
        // Agregar opciones al ComboBox
        cmbGestionador.getItems().addAll("tiempo", "transbordos", "costo");

        // Manejar selección
        cmbGestionador.setOnAction(event -> {
            String seleccion = cmbGestionador.getSelectionModel().getSelectedItem();

            if (seleccion == null || seleccion.isBlank()) {
                eleccionCriterio = "general"; // Selección general por defecto
            } else {
                eleccionCriterio = seleccion; // Guardar selección específica
            }
        });
    }
}
