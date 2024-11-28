package com.example.sistemadetrafico;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class RutaManager {
    private List<Ruta> rutas;
    private List<Parada> paradas;

    public RutaManager(List<Parada> paradas) {
        this.rutas = new ArrayList<>();
        this.paradas = paradas;
    }

    public void abrirVentanaAgregarRuta(Pane paneArea) {
        Stage ventana = new Stage();
        ventana.setTitle("Agregar Ruta");

        ventana.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label tituloFormulario = new Label("Agregar Ruta");
        tituloFormulario.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField textFieldOrigen = new TextField();
        textFieldOrigen.setPromptText("Número de la parada origen");
        textFieldOrigen.setMaxWidth(200);

        TextField textFieldDestino = new TextField();
        textFieldDestino.setPromptText("Número de la parada destino");
        textFieldDestino.setMaxWidth(200);

        TextField textFieldDistancia = new TextField();
        textFieldDistancia.setPromptText("Distancia (en km)");
        textFieldDistancia.setMaxWidth(200);

        TextField textFieldTiempo = new TextField();
        textFieldTiempo.setPromptText("Tiempo (en minutos)");
        textFieldTiempo.setMaxWidth(200);

        TextField textFieldCosto = new TextField();
        textFieldCosto.setPromptText("Costo (en $)");
        textFieldCosto.setMaxWidth(200);

        TextField textFieldTransbordos = new TextField();
        textFieldTransbordos.setPromptText("Cantidad de transbordos");
        textFieldTransbordos.setMaxWidth(200);

        Button btnAgregar = new Button("Agregar");
        Button btnCancelar = new Button("Cancelar");

        btnAgregar.setOnAction(event -> {
            String origen = textFieldOrigen.getText().trim();
            String destino = textFieldDestino.getText().trim();
            String distancia = textFieldDistancia.getText().trim();
            String tiempo = textFieldTiempo.getText().trim();
            String costo = textFieldCosto.getText().trim();
            String transbordos = textFieldTransbordos.getText().trim();

            if (origen.isEmpty() || destino.isEmpty() || distancia.isEmpty() || tiempo.isEmpty() || costo.isEmpty() || transbordos.isEmpty()) {
                mostrarAlerta(AlertType.ERROR, "Error", "Todos los campos deben ser completados.");
            } else {
                try {
                    int origenNum = Integer.parseInt(origen);
                    int destinoNum = Integer.parseInt(destino);
                    int distanciaVal = Integer.parseInt(distancia);
                    int tiempoVal = Integer.parseInt(tiempo);
                    float costoVal = Float.parseFloat(costo);
                    int transbordosVal = Integer.parseInt(transbordos);

                    Parada paradaOrigen = buscarParada(origenNum);
                    Parada paradaDestino = buscarParada(destinoNum);

                    if (paradaOrigen == null || paradaDestino == null || (paradaOrigen == null && paradaDestino == null)) {
                        mostrarAlerta(AlertType.ERROR, "Error", "Parada origen o destino no encontrada.");
                    } else {
                        Ruta nuevaRuta = new Ruta(paradaOrigen, paradaDestino, tiempoVal, distanciaVal, costoVal, transbordosVal);
                        rutas.add(nuevaRuta);

                        Circle origenCircle = buscarCirculo(paradaOrigen.getNumero(), paneArea);
                        Circle destinoCircle = buscarCirculo(paradaDestino.getNumero(), paneArea);

                        if (origenCircle != null && destinoCircle != null) {
                            Line linea = crearLineaAjustada(
                                    origenCircle.getCenterX(),
                                    origenCircle.getCenterY(),
                                    destinoCircle.getCenterX(),
                                    destinoCircle.getCenterY(),
                                    15 // Tamaño del radio del círculo para ajustar la línea
                            );
                            String idLinea = "Ruta-" + paradaOrigen.getNumero() + "-" + paradaDestino.getNumero();
                            linea.setId(idLinea); // Asignar ID único
                            linea.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 4;");
                            paneArea.getChildren().add(0, linea);
                        } else {
                            mostrarAlerta(AlertType.ERROR, "Error", "No se encontraron las coordenadas de origen o destino.");
                        }

                        mostrarAlerta(AlertType.INFORMATION, "Ruta Agregada", "La ruta se ha agregado correctamente.");
                        ventana.close();
                    }

                } catch (NumberFormatException e) {
                    mostrarAlerta(AlertType.ERROR, "Error", "Todos los valores deben ser numéricos.");
                }
            }
        });

        btnCancelar.setOnAction(event -> {
            ventana.close();
        });

        vbox.getChildren().addAll(tituloFormulario, textFieldOrigen, textFieldDestino, textFieldDistancia, textFieldTiempo, textFieldCosto, textFieldTransbordos, btnAgregar, btnCancelar);
        Scene scene = new Scene(vbox, 300, 450); // Tamaño de la ventana
        ventana.setScene(scene);
        ventana.showAndWait();
    }


    public void abrirVentanaEliminarRuta(Pane paneArea) {
        Stage ventana = new Stage();
        ventana.setTitle("Eliminar Ruta");

        // Bloquear la ventana principal mientras está abierta
        ventana.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label tituloFormulario = new Label("Seleccione una Ruta para Eliminar");
        tituloFormulario.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ComboBox<String> comboBoxRutas = new ComboBox<>();
        for (Ruta ruta : rutas) {
            String rutaTexto = "Origen: " + ruta.getOrigen().getNumero() +
                    " - Destino: " + ruta.getDestino().getNumero();
            comboBoxRutas.getItems().add(rutaTexto);
        }

        Button btnEliminar = new Button("Eliminar");
        Button btnCancelar = new Button("Cancelar");

        btnEliminar.setOnAction(event -> {
            String seleccion = comboBoxRutas.getValue();

            if (seleccion == null) {
                mostrarAlerta(AlertType.ERROR, "Error", "Debe seleccionar una ruta para eliminar.");
            } else {
                // Identificar la ruta a eliminar
                String[] partes = seleccion.split(" - ");
                int origenNum = Integer.parseInt(partes[0].replace("Origen: ", "").trim());
                int destinoNum = Integer.parseInt(partes[1].replace("Destino: ", "").trim());

                Ruta rutaAEliminar = buscarRuta(origenNum, destinoNum);

                if (rutaAEliminar != null) {
                    rutas.remove(rutaAEliminar);

                    // Eliminar la línea de la ruta en el área de dibujo
                    paneArea.getChildren().removeIf(node ->
                            node instanceof Line &&
                                    node.getId() != null &&
                                    node.getId().equals("Ruta-" + origenNum + "-" + destinoNum)
                    );

                    mostrarAlerta(AlertType.INFORMATION, "Ruta Eliminada", "La ruta se ha eliminado correctamente.");
                    ventana.close(); // Cerrar la ventana
                } else {
                    mostrarAlerta(AlertType.ERROR, "Error", "No se encontró la ruta seleccionada.");
                }
            }
        });

        btnCancelar.setOnAction(event -> {
            ventana.close();
        });

        vbox.getChildren().addAll(tituloFormulario, comboBoxRutas, btnEliminar, btnCancelar);
        Scene scene = new Scene(vbox, 300, 250);
        ventana.setScene(scene);
        ventana.showAndWait();
    }


    public void abrirVentanaModificarRuta(Pane paneArea) {
        // Verificar que existan rutas para modificar
        if (rutas.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Sin rutas", "No hay rutas disponibles para modificar.");
            return;
        }

        Stage ventana = new Stage();
        ventana.setTitle("Modificar Ruta");

        // Bloquear la ventana principal mientras está abierta
        ventana.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;"); // Margen y centrado

        Label tituloFormulario = new Label("Modificar Ruta");
        tituloFormulario.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ComboBox<String> comboBoxRutas = new ComboBox<>();
        comboBoxRutas.setPromptText("Seleccione una ruta");
        comboBoxRutas.setMaxWidth(300);

        for (Ruta ruta : rutas) {
            String rutaTexto = "Origen: " + ruta.getOrigen().getNumero() +
                    " - Destino: " + ruta.getDestino().getNumero();
            comboBoxRutas.getItems().add(rutaTexto);
        }


        ComboBox<Parada> comboBoxOrigen = new ComboBox<>();
        comboBoxOrigen.getItems().addAll(paradas);
        comboBoxOrigen.setPromptText("Seleccione el origen");
        comboBoxOrigen.setMaxWidth(300);

        ComboBox<Parada> comboBoxDestino = new ComboBox<>();
        comboBoxDestino.getItems().addAll(paradas);
        comboBoxDestino.setPromptText("Seleccione el destino");
        comboBoxDestino.setMaxWidth(300);

        TextField textFieldTiempo = new TextField();
        textFieldTiempo.setPromptText("Ingrese el tiempo en minutos");
        textFieldTiempo.setMaxWidth(300);

        TextField textFieldDistancia = new TextField();
        textFieldDistancia.setPromptText("Ingrese la distancia");
        textFieldDistancia.setMaxWidth(300);

        TextField textFieldCosto = new TextField();
        textFieldCosto.setPromptText("Ingrese el costo");
        textFieldCosto.setMaxWidth(300);

        TextField textFieldTransbordos = new TextField();
        textFieldTransbordos.setPromptText("Ingrese los transbordos");
        textFieldTransbordos.setMaxWidth(300);

        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        Parada[] origenAnterior = new Parada[1];
        Parada[] destinoAnterior = new Parada[1];
        // Actualizar campos al seleccionar una ruta
        comboBoxRutas.setOnAction(event -> {
            int selectedIndex = comboBoxRutas.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0) {
                Ruta rutaSeleccionada = rutas.get(selectedIndex);
                origenAnterior[0] = rutaSeleccionada.getOrigen();
                destinoAnterior[0] = rutaSeleccionada.getDestino();
                comboBoxOrigen.setValue(rutaSeleccionada.getOrigen());
                comboBoxDestino.setValue(rutaSeleccionada.getDestino());
                textFieldTiempo.setText(String.valueOf(rutaSeleccionada.getTiempo()));
                textFieldDistancia.setText(String.valueOf(rutaSeleccionada.getDistancia()));
                textFieldCosto.setText(String.valueOf(rutaSeleccionada.getCosto()));
                textFieldTransbordos.setText(String.valueOf(rutaSeleccionada.getTransbordos()));
            }
        });

        btnGuardar.setOnAction(event -> {
            try {
                int selectedIndex = comboBoxRutas.getSelectionModel().getSelectedIndex();
                if (selectedIndex < 0) {
                    mostrarAlerta(AlertType.ERROR, "Error", "Debe seleccionar una ruta.");
                    return;
                }

                Ruta rutaSeleccionada = rutas.get(selectedIndex);
                Parada nuevoOrigen = comboBoxOrigen.getValue();
                Parada nuevoDestino = comboBoxDestino.getValue();
                int nuevoTiempo = Integer.parseInt(textFieldTiempo.getText().trim());
                int nuevaDistancia = Integer.parseInt(textFieldDistancia.getText().trim());
                float nuevoCosto = Float.parseFloat(textFieldCosto.getText().trim());
                int nuevosTransbordos = Integer.parseInt(textFieldTransbordos.getText().trim());

                if (nuevoOrigen == null || nuevoDestino == null) {
                    mostrarAlerta(AlertType.ERROR, "Error", "Debe seleccionar un origen y un destino.");
                } else if (nuevoOrigen.equals(nuevoDestino)) {
                    mostrarAlerta(AlertType.ERROR, "Error", "El origen y el destino no pueden ser la misma parada.");
                } else if (existeRutaEntre(nuevoOrigen, nuevoDestino, rutaSeleccionada)) {
                    mostrarAlerta(AlertType.ERROR, "Error", "Ya existe una ruta entre estas paradas.");
                } else {

                    if (origenAnterior[0] != null && destinoAnterior[0] != null) {
                        String idLineaAnterior = "#Ruta-" + origenAnterior[0].getNumero() + "-" + destinoAnterior[0].getNumero();
                        Line lineaAnterior = (Line) paneArea.lookup(idLineaAnterior);
                        if (lineaAnterior != null) {
                            paneArea.getChildren().remove(lineaAnterior);
                        }
                    }

                    // Actualizar datos de la ruta
                    rutaSeleccionada.setOrigen(nuevoOrigen);
                    rutaSeleccionada.setDestino(nuevoDestino);
                    rutaSeleccionada.setTiempo(nuevoTiempo);
                    rutaSeleccionada.setDistancia(nuevaDistancia);
                    rutaSeleccionada.setCosto(nuevoCosto);
                    rutaSeleccionada.setTransbordos(nuevosTransbordos);

                    // Dibujar la nueva línea
                    Line nuevaLinea = crearLineaAjustada(
                            nuevoOrigen.getX(),
                            nuevoOrigen.getY(),
                            nuevoDestino.getX(),
                            nuevoDestino.getY(),
                            15
                    );
                    nuevaLinea.setStroke(Color.RED);
                    nuevaLinea.setStrokeWidth(4);
                    nuevaLinea.setId("Ruta-" + rutaSeleccionada.getOrigen().getNumero() +
                            "-" + rutaSeleccionada.getDestino().getNumero());
                    paneArea.getChildren().add(nuevaLinea);

                    mostrarAlerta(AlertType.INFORMATION, "Ruta Modificada", "La ruta se ha modificado correctamente.");
                    ventana.close();
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(AlertType.ERROR, "Error", "Verifique que todos los valores sean numéricos y válidos.");
            }
        });

        btnCancelar.setOnAction(event -> {
            ventana.close();
        });

        vbox.getChildren().addAll(
                tituloFormulario,
                comboBoxRutas,
                comboBoxOrigen,
                comboBoxDestino,
                textFieldTiempo,
                textFieldDistancia,
                textFieldCosto,
                textFieldTransbordos,
                btnGuardar,
                btnCancelar
        );

        Scene scene = new Scene(vbox, 400, 600);
        ventana.setScene(scene);
        ventana.showAndWait();
    }


    private Parada buscarParada(int numero) {
        for (Parada parada : paradas) {
            if (parada.getNumero() == numero) {
                return parada;
            }
        }
        return null;
    }


    private Ruta buscarRuta(int origenNum, int destinoNum) {
        for (Ruta ruta : rutas) {
            if (ruta.getOrigen().getNumero() == origenNum && ruta.getDestino().getNumero() == destinoNum) {
                return ruta;
            }
        }
        return null;
    }


    private Circle buscarCirculo(int numeroParada, Pane paneArea) {
        for (javafx.scene.Node node : paneArea.getChildren()) {
            if (node instanceof Circle) {
                Circle circulo = (Circle) node;
                if (circulo.getId() != null && circulo.getId().equals("Parada-" + numeroParada)) {
                    return circulo;
                }
            }
        }
        return null;
    }


    private boolean existeRutaEntre(Parada origen, Parada destino, Ruta rutaActual) {
        for (Ruta ruta : rutas) {
            if (ruta != rutaActual &&
                    ((ruta.getOrigen().equals(origen) && ruta.getDestino().equals(destino)) ||
                            (ruta.getOrigen().equals(destino) && ruta.getDestino().equals(origen)))) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private Line crearLineaAjustada(double x1, double y1, double x2, double y2, double radioCirculo) {
        // Calcular el vector de dirección
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);

        // Calcular los puntos ajustados directamente con el radio
        double ajusteX = radioCirculo * dx / length;
        double ajusteY = radioCirculo * dy / length;

        // Retornar una línea que no invade los círculos
        return new Line(
                x1 + ajusteX, y1 + ajusteY, // Punto ajustado en el círculo de origen
                x2 - ajusteX, y2 - ajusteY  // Punto ajustado en el círculo de destino
        );
    }

}