package com.example.sistemadetrafico;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;
import java.util.List;

public class ParadaManager {
    private List<Parada> paradas;
    private Pane paneArea;

    public ParadaManager(Pane paneArea) {
        this.paradas = new ArrayList<>();
        this.paneArea = paneArea;
    }

    public void abrirVentanaAgregarParada() {
        Stage ventana = new Stage();
        ventana.setTitle("Agregar Parada");
        ventana.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label tituloFormulario = new Label("Agregar Parada");
        tituloFormulario.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField textFieldNumeroParada = new TextField();
        textFieldNumeroParada.setPromptText("Ingrese el número de la parada");
        textFieldNumeroParada.setMaxWidth(200);

        TextField textFieldX = new TextField();
        textFieldX.setPromptText("Ingrese la coordenada X");
        textFieldX.setMaxWidth(200);

        TextField textFieldY = new TextField();
        textFieldY.setPromptText("Ingrese la coordenada Y");
        textFieldY.setMaxWidth(200);

        Button btnAgregar = new Button("Agregar");
        Button btnCancelar = new Button("Cancelar");

        btnAgregar.setOnAction(event -> {
            String numeroParada = textFieldNumeroParada.getText().trim();
            String coordenadaX = textFieldX.getText().trim();
            String coordenadaY = textFieldY.getText().trim();

            if (numeroParada.isEmpty() || coordenadaX.isEmpty() || coordenadaY.isEmpty()) {
                mostrarAlerta(AlertType.ERROR, "Error", "Todos los campos deben ser completados.");
            } else {
                try {
                    int numero = Integer.parseInt(numeroParada);
                    double x = Double.parseDouble(coordenadaX);
                    double y = Double.parseDouble(coordenadaY);

                    for (Parada paradaExistente : paradas) {
                        if (paradaExistente.getNumero() == numero) {
                            mostrarAlerta(AlertType.ERROR, "Error",
                                    "Ya hay una parada con el número " + numero);
                            return;
                        }
                    }

                    // Validar coordenadas de parada existente
                    for (Parada paradaExistente : paradas) {
                        if (paradaExistente.getX() == x && paradaExistente.getY() == y) {
                            mostrarAlerta(AlertType.ERROR, "Error",
                                    "Ya hay una parada en las coordenadas (" + x + ", " + y + ")");
                            return;
                        }
                    }
                    if (x >= 100 && x <= 800 && y >= 50 && y <= 600) {
                        Parada nuevaParada = new Parada(numero, x, y);
                        paradas.add(nuevaParada);

                        Circle circulo = new Circle(x, y, 15);
                        circulo.setStyle("-fx-fill: #ff0000;");
                        circulo.setId("Parada-" + numero);

                        Label labelNumero = new Label(String.valueOf(numero));
                        labelNumero.setId("TextoParada-" + numero);
                        labelNumero.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #000000;");
                        labelNumero.setLayoutX(x - 4);
                        labelNumero.setLayoutY(y - 11);

                        paneArea.getChildren().addAll(circulo, labelNumero);

                        mostrarAlerta(AlertType.INFORMATION, "Parada Agregada", "La parada se ha agregado correctamente.");
                        ventana.close();
                    } else {
                        mostrarAlerta(AlertType.ERROR, "Error", "Las coordenadas están fuera del área válida.");
                    }

                } catch (NumberFormatException e) {
                    mostrarAlerta(AlertType.ERROR, "Error", "El número de la parada y las coordenadas deben ser valores numéricos.");
                }
            }
        });

        btnCancelar.setOnAction(event -> {
            ventana.close();
        });

        vbox.getChildren().addAll(tituloFormulario, textFieldNumeroParada, textFieldX, textFieldY, btnAgregar, btnCancelar);
        Scene scene = new Scene(vbox, 300, 300);
        ventana.setScene(scene);
        ventana.showAndWait();
    }

    public void abrirVentanaEliminarParada() {
        Stage ventana = new Stage();
        ventana.setTitle("Eliminar Parada");
        ventana.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label tituloFormulario = new Label("Seleccione una Parada para Eliminar");
        tituloFormulario.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ComboBox<Integer> comboBoxParadas = new ComboBox<>();
        for (Parada parada : paradas) {
            comboBoxParadas.getItems().add(parada.getNumero());
        }

        Button btnEliminar = new Button("Eliminar");
        Button btnCancelar = new Button("Cancelar");

        btnEliminar.setOnAction(event -> {
            Integer numeroSeleccionado = comboBoxParadas.getValue();

            if (numeroSeleccionado == null) {
                mostrarAlerta(AlertType.ERROR, "Error", "Debe seleccionar una parada para eliminar.");
            } else {
                Parada paradaAEliminar = buscarParada(numeroSeleccionado);
                if (paradaAEliminar != null) {
                    paneArea.getChildren().removeIf(node -> {
                        if (node instanceof Circle circle) {
                            return circle.getId() != null && circle.getId().equals("Parada-" + numeroSeleccionado);
                        } else if (node instanceof Label label) {
                            return label.getId() != null && label.getId().equals("TextoParada-" + numeroSeleccionado);
                        }
                        return false;
                    });

                    paradas.remove(paradaAEliminar);

                    mostrarAlerta(AlertType.INFORMATION, "Parada Eliminada", "La parada se ha eliminado correctamente.");
                    ventana.close();
                } else {
                    mostrarAlerta(AlertType.ERROR, "Error", "No se encontró la parada seleccionada.");
                }
            }
        });

        btnCancelar.setOnAction(event -> {
            ventana.close();
        });

        vbox.getChildren().addAll(tituloFormulario, comboBoxParadas, btnEliminar, btnCancelar);
        Scene scene = new Scene(vbox, 300, 250);
        ventana.setScene(scene);
        ventana.showAndWait();
    }

    public void abrirVentanaModificarParada() {
        Stage ventana = new Stage();
        ventana.setTitle("Modificar Parada");
        ventana.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titulo = new Label("Seleccione la parada y modifique los datos:");
        ComboBox<Parada> comboBoxParadas = new ComboBox<>();
        comboBoxParadas.getItems().addAll(paradas);
        comboBoxParadas.setPromptText("Seleccione una parada");

        comboBoxParadas.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Parada parada, boolean empty) {
                super.updateItem(parada, empty);
                setText(empty || parada == null ? null : "Parada #" + parada.getNumero());
            }
        });
        comboBoxParadas.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Parada parada, boolean empty) {
                super.updateItem(parada, empty);
                setText(empty || parada == null ? null : "Parada #" + parada.getNumero());
            }
        });

        TextField campoNuevoNumero = new TextField();
        campoNuevoNumero.setPromptText("Nuevo número de la parada");

        TextField campoNuevaX = new TextField();
        campoNuevaX.setPromptText("Nueva coordenada X");

        TextField campoNuevaY = new TextField();
        campoNuevaY.setPromptText("Nueva coordenada Y");

        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        btnGuardar.setOnAction(event -> {
            Parada paradaSeleccionada = comboBoxParadas.getValue();
            String nuevoNumeroTexto = campoNuevoNumero.getText();
            String nuevaXTexto = campoNuevaX.getText();
            String nuevaYTexto = campoNuevaY.getText();

            if (paradaSeleccionada == null) {
                mostrarAlerta(AlertType.ERROR, "Error", "Debe seleccionar una parada.");
                return;
            }

            if (!nuevoNumeroTexto.isEmpty() && !nuevoNumeroTexto.matches("\\d+")) {
                mostrarAlerta(AlertType.ERROR, "Error", "El número de parada debe ser un valor numérico.");
                return;
            }

            if ((!nuevaXTexto.isEmpty() && !nuevaXTexto.matches("\\d+")) || (!nuevaYTexto.isEmpty() && !nuevaYTexto.matches("\\d+"))) {
                mostrarAlerta(AlertType.ERROR, "Error", "Las coordenadas deben ser valores numéricos.");
                return;
            }

            int nuevoNumero = nuevoNumeroTexto.isEmpty() ? paradaSeleccionada.getNumero() : Integer.parseInt(nuevoNumeroTexto);
            double nuevaX = nuevaXTexto.isEmpty() ? paradaSeleccionada.getX() : Double.parseDouble(nuevaXTexto);
            double nuevaY = nuevaYTexto.isEmpty() ? paradaSeleccionada.getY() : Double.parseDouble(nuevaYTexto);

            if (nuevoNumero != paradaSeleccionada.getNumero() && paradas.stream().anyMatch(p -> p.getNumero() == nuevoNumero)) {
                mostrarAlerta(AlertType.ERROR, "Error", "Ya existe una parada con este número.");
                return;
            }

            int numeroAnterior = paradaSeleccionada.getNumero();
            paradaSeleccionada.setNumero(nuevoNumero);
            paradaSeleccionada.setX(nuevaX);
            paradaSeleccionada.setY(nuevaY);

            Label labelParada = (Label) paneArea.lookup("#TextoParada-" + numeroAnterior);
            if (labelParada != null) {
                labelParada.setText(String.valueOf(nuevoNumero));
                labelParada.setId("TextoParada-" + nuevoNumero);
                labelParada.setLayoutX(nuevaX - 4);
                labelParada.setLayoutY(nuevaY - 11);
            }

            Circle circuloParada = (Circle) paneArea.lookup("#Parada-" + numeroAnterior);
            if (circuloParada != null) {
                circuloParada.setId("Parada-" + nuevoNumero);
                circuloParada.setCenterX(nuevaX);
                circuloParada.setCenterY(nuevaY);
            }

            mostrarAlerta(AlertType.INFORMATION, "Éxito", "La parada se modificó correctamente.");
            ventana.close();
        });

        btnCancelar.setOnAction(event -> ventana.close());

        layout.getChildren().addAll(titulo, comboBoxParadas, campoNuevoNumero, campoNuevaX, campoNuevaY, btnGuardar, btnCancelar);

        Scene scene = new Scene(layout, 400, 300);
        ventana.setScene(scene);
        ventana.showAndWait();
    }

    public Parada buscarParada(int numero) {
        for (Parada parada : paradas) {
            if (parada.getNumero() == numero) {
                return parada;
            }
        }
        return null;
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public List<Parada> getParadas() {
        return paradas;
    }
}