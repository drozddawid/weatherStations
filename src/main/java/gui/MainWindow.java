package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.DataBase;
import model.Measurement;
import model.Point;
import model.Station;
import service.DataBaseDeserializer;
import service.DataBaseSerializer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Graphic User Interface(GUI) used to present the data on the screen(tables, graphs)  and for dialoguing with user(textfields, buttons).
 */


public class MainWindow extends Application {
    File dataFile;
    GridPane gridPane;
    TableView<Measurement> measurementsTable;
    ScrollPane stationsScrollPane;
    TableView.TableViewSelectionModel<Measurement> selectionModel;
    ObservableList<Measurement> measurementObservableList;
    Station currentlyShownStation;
    TextField dateF;
    TextField tempF;
    TextField humidityF;
    TextField pressureF;
    CheckBox showPressure;
    CheckBox showHumidity;
    CheckBox showTemperature;
    CheckBox min;
    CheckBox avg;
    CheckBox max;
    int dataShownType = 2; //0-pressure, 1-humidity, 2-temperature Default:2
    int minAvgMax = 1; //0-min, 1-average, 2-max Default:1

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            dataFile = new File("dataBase.json");

            primaryStage.setTitle("Stacje pogodowe");
            //deserializing dataBase
            DataBaseDeserializer deserializer = new DataBaseDeserializer();
            DataBase dataBase = deserializer.deserialize(dataFile);
            /*adding random data for graph testing purpose
            addRandomData(dataBase.getStations(),LocalDate.of(2020,10,1),LocalDate.of(2020,10,30),40,30,1120);
            adding random data*/

            gridPane = new GridPane();
            gridPane.setPadding(new Insets(10, 10, 10, 10));
            gridPane.setVgap(8);
            gridPane.setHgap(8);


            //------------Measurements table----------------
            VBox measurementsBox = new VBox();
            GridPane.setConstraints(measurementsBox, 1, 0);
            GridPane.setHalignment(measurementsBox, HPos.RIGHT);
            measurementsBox.setSpacing(8);

            //time column
            TableColumn<Measurement, LocalDate> dateColumn = new TableColumn<>("Data");
            dateColumn.setMinWidth(125);
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            //temperature column
            TableColumn<Measurement, Integer> temperatureColumn = new TableColumn<>("Temperatura");
            temperatureColumn.setMinWidth(125);
            temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));
            //humidity column
            TableColumn<Measurement, Integer> humidityColumn = new TableColumn<>("Wilgotność");
            humidityColumn.setMinWidth(125);
            humidityColumn.setCellValueFactory(new PropertyValueFactory<>("humidity"));
            //presssure column
            TableColumn<Measurement, Integer> pressureColumn = new TableColumn<>("Ciśnienie");
            pressureColumn.setMinWidth(125);
            pressureColumn.setCellValueFactory(new PropertyValueFactory<>("pressure"));
            measurementsTable = new TableView<>();
            measurementsTable.getColumns().addAll(dateColumn, temperatureColumn, humidityColumn, pressureColumn);
            GridPane.setHalignment(measurementsTable, HPos.RIGHT);
            selectionModel = measurementsTable.getSelectionModel();
            measurementsBox.getChildren().add(measurementsTable);
            measurementsBox.setMinSize(500, 200);

            //controls for editing Measurements
            HBox editingBox = new HBox();
            dateF = new TextField();
            dateF.setPromptText("yyyy-mm-dd");
            dateF.setPrefWidth(90);
            tempF = new TextField();
            tempF.setPromptText("temperatura");
            tempF.setPrefWidth(90);
            humidityF = new TextField();
            humidityF.setPromptText("wilgotność");
            humidityF.setPrefWidth(90);
            pressureF = new TextField();
            pressureF.setPromptText("ciśnienie");
            pressureF.setPrefWidth(90);
            Button add = new Button("Dodaj");
            add.setOnAction(e -> addButtonClicked());
            Button delete = new Button("Usuń");
            delete.setOnAction(e -> deleteButtonClicked());
            editingBox.getChildren().addAll(dateF, tempF, humidityF, pressureF, add, delete);
            editingBox.setSpacing(8);
            measurementsBox.getChildren().add(editingBox);


            //------------stations list--------------
            stationsScrollPane = new ScrollPane();
            GridPane.setConstraints(stationsScrollPane, 0, 0);
            stationsScrollPane = buildStationsBox(dataBase);
            stationsScrollPane.setMinSize(200, 200);


            //-----options box----
            HBox optionsBox = new HBox();
            GridPane.setConstraints(optionsBox, 1, 1);
            GridPane.setHalignment(optionsBox, HPos.RIGHT);

            //----left side options box
            VBox leftBox = new VBox();
            Button saveAll = new Button("Zapisz zmiany");
            saveAll.setOnAction(e -> saveAllButtonClicked(dataBase, dataFile));

            Button deleteStation = new Button("Usuń stację");
            deleteStation.setOnAction(e -> {
                deleteStation(currentlyShownStation, dataBase);
                gridPane.getChildren().remove(stationsScrollPane);
                stationsScrollPane = buildStationsBox(dataBase);
                GridPane.setConstraints(stationsScrollPane, 0, 0);
                gridPane.getChildren().add(stationsScrollPane);
            });

            Button addStation = new Button("Dodaj nową stację");
            addStation.setOnAction(e -> {
                Stage stationMakerStage = stationMakerStage(dataBase);
                stationMakerStage.showAndWait();
            });
            leftBox.getChildren().addAll(saveAll, deleteStation, addStation);
            leftBox.setSpacing(8);

            //----options right side----
            VBox rightBox = new VBox();

            showPressure = new CheckBox("Wykres Ciśnienia");
            showPressure.setSelected(false);
            showPressure.setOnAction(e -> {
                dataShownType = 0;
                showHumidity.setSelected(false);
                showTemperature.setSelected(false);
            });
            showHumidity = new CheckBox("Wykres Wilgotności");
            showHumidity.setSelected(false);
            showHumidity.setOnAction(e -> {
                dataShownType = 1;
                showTemperature.setSelected(false);
                showPressure.setSelected(false);
            });
            showTemperature = new CheckBox("Wykres Temperatury");
            showTemperature.setSelected(true);
            showTemperature.setOnAction(e -> {
                dataShownType = 2;
                showHumidity.setSelected(false);
                showPressure.setSelected(false);
            });
            Label minMaxAvgLabel = new Label("Wartość dla kilku pomiarów w jednym dniu:");
            min = new CheckBox("min");
            min.setSelected(false);
            min.setOnAction(e -> {
                minAvgMax = 0;
                avg.setSelected(false);
                max.setSelected(false);
            });
            avg = new CheckBox("średnia");
            avg.setSelected(true);
            avg.setOnAction(e -> {
                minAvgMax = 1;
                min.setSelected(false);
                max.setSelected(false);
            });
            max = new CheckBox("max");
            max.setSelected(false);
            max.setOnAction(e -> {
                minAvgMax = 2;
                avg.setSelected(false);
                min.setSelected(false);
            });

            Button draw = new Button("Rysuj wykres");
            draw.setOnAction(e -> {
                //TODO: make a method which makes arraylists of localdates and datq for graph drawing
                Stage graphStage = filterDataAndGetGraphStage(dataBase.getStations());
                if (graphStage == null) {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Brak zaznaczonych stacji (zaznacz stację przy nazwie)\n lub tabele pomiarów zaznaczonych stacji są puste.", ButtonType.OK);
                    a.show();
                } else graphStage.show();

            });
            rightBox.setSpacing(8);
            rightBox.getChildren().addAll(draw, showPressure, showHumidity, showTemperature, minMaxAvgLabel, min, avg, max);
            rightBox.setAlignment(Pos.TOP_RIGHT);
            optionsBox.setSpacing(200);
            optionsBox.getChildren().addAll(leftBox, rightBox);

            gridPane.getChildren().addAll(stationsScrollPane, measurementsBox, optionsBox);

            Scene mainScene = new Scene(gridPane, 800, 600);
            primaryStage.setScene(mainScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    private ScrollPane buildStationsBox(DataBase dataBase) {
        dataBase.getStations().sort(new Comparator<Station>() {
            @Override
            public int compare(Station station, Station t1) {
                if (station.getId() > t1.getId()) {
                    return 1;
                } else return -1;
            }
        });
        ScrollPane scrollBox = new ScrollPane();
        scrollBox.setPrefViewportHeight(100);
        scrollBox.setPrefViewportWidth(201);
        VBox box = new VBox();
        box.setMinSize(200, 200);
        box.setSpacing(8);
        Border border = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1, 1, 1, 1)));
        Border rBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(1, 1, 1, 1)));
        for (Station s : dataBase.getStations()) {
            s.setShowInPlot(false);
            HBox singleStationBox = new HBox(10);
            Label id = new Label(String.valueOf(s.getId()));
            id.setPadding(new Insets(4));
            Label name = new Label(s.getName());
            name.setPadding(new Insets(4));
            CheckBox addToPlot = new CheckBox();
            addToPlot.setSelected(false);
            addToPlot.setPadding(new Insets(4));
            addToPlot.setOnAction(e -> {
                s.setShowInPlot(addToPlot.isSelected());
                //for testing  System.out.println("Stacja: " + s.getId() + ". " +s.getName()+"status: " + s.isShowInPlot());
            });
            singleStationBox.setBorder(border);
            singleStationBox.setOnMouseClicked(e -> {
                for (Node node : box.getChildren()) {
                    HBox b = (HBox) node;
                    b.setBorder(border);
                }
                singleStationBox.setBorder(rBorder);
                measurementObservableList = FXCollections.observableArrayList(s.getMeasurements());
                currentlyShownStation = s;
                measurementsTable.setItems(measurementObservableList);
            });
            singleStationBox.getChildren().addAll(id, addToPlot, name);
            box.getChildren().add(singleStationBox);
        }
        scrollBox.setContent(box);
        return scrollBox;
    }

    private void addButtonClicked() {
        LocalDate time = LocalDate.of(1970, 1, 1);
        int temp;
        int hum;
        int press;
        Alert a;
        try {
            time = LocalDate.parse(dateF.getText());
            try {
                temp = Integer.parseInt(tempF.getText());
                try {
                    hum = Integer.parseInt(humidityF.getText());
                    if (hum < 0) throw new Exception("Wartość nie może być ujemna.");
                    try {
                        press = Integer.parseInt(pressureF.getText());
                        if (press < 0) {
                            throw new Exception("Wartość nie może być ujemna.");
                        } else {
                            dateF.clear();
                            tempF.clear();
                            humidityF.clear();
                            pressureF.clear();
                        }
                    } catch (Exception e) {
                        a = new Alert(Alert.AlertType.ERROR, "Wprowadzono nieprawidłowe ciśnienie.", ButtonType.OK);
                        a.show();
                        return;
                    }
                } catch (Exception e) {
                    a = new Alert(Alert.AlertType.ERROR, "Wprowadzono nieprawidłową wilgotność.", ButtonType.OK);
                    a.show();
                    return;
                }
            } catch (NumberFormatException e) {
                a = new Alert(Alert.AlertType.ERROR, "Wprowadzono nieprawidłową temperaturę.", ButtonType.OK);
                a.show();
                return;
            }
        } catch (Exception e) {
            a = new Alert(Alert.AlertType.ERROR, "Wprowadzono nieprawidłową datę.", ButtonType.OK);
            a.show();
            return;
        }
        try {
            Measurement m = new Measurement(currentlyShownStation.getId(), press, hum, temp, time);
            currentlyShownStation.addMeasurement(m);
            measurementObservableList.add(m);
        } catch (NullPointerException e) {
            a = new Alert(Alert.AlertType.ERROR, "Wybierz stację.", ButtonType.OK);
            a.show();
            return;
        }
    }

    private void deleteButtonClicked() {
        try {
            Measurement measurementSelected = selectionModel.getSelectedItem();
            currentlyShownStation.getMeasurements().remove(measurementSelected);
            measurementObservableList.remove(measurementSelected);
        } catch (NullPointerException e) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Operacja usuwania nie powiodła się. Prawdopodobnie nie zaznaczono stacji lub pomiaru.\n Spróbuj ponownie.");
            a.show();
            return;
        }
    }

    private void saveAllButtonClicked(DataBase dataBase, File file) {
        DataBaseSerializer dataSerializer = new DataBaseSerializer();
        Alert a;
        try {
            dataSerializer.dataBaseToJson(dataBase, file);
            a = new Alert(Alert.AlertType.INFORMATION, "Zapisano.", ButtonType.OK);
            a.show();
        } catch (IOException e) {
            a = new Alert(Alert.AlertType.ERROR, "Zapisywanie nie powiodło się", ButtonType.OK);
            a.show();
        }

    }

    private void deleteStation(Station station, DataBase dataBase) {
        dataBase.getStations().remove(station);
        //stationsBox = buildStationsBox(dataBase);
    }

    private Stage filterDataAndGetGraphStage(LinkedList<Station> stations) {
        LinkedList<Measurement> selectedStationsMeasurements = new LinkedList<>();
        for (Station s : stations) {
            if (s.isShowInPlot()) {
                selectedStationsMeasurements.addAll(s.getMeasurements());
            }
        }
        if (selectedStationsMeasurements.size() == 0) {
            return null;
        }
        ArrayList<LocalDate> time = new ArrayList<>();
        ArrayList<Integer> data = new ArrayList<>();

        selectedStationsMeasurements.sort(new Comparator<>() {
            @Override
            public int compare(Measurement measurement, Measurement t1) {
                if (measurement.getTime().toEpochDay() == t1.getTime().toEpochDay()) {
                    return 0;
                } else if (measurement.getTime().toEpochDay() < t1.getTime().toEpochDay()) {
                    return -1;
                } else return 1;
            }
        });

        switch (minAvgMax) {
            case 0: {
                switch (dataShownType) {
                    case 0: {
                        LocalDate pastDate = selectedStationsMeasurements.get(0).getTime();
                        boolean isFirstOneSkipped = false;
                        int minValue = selectedStationsMeasurements.get(0).getPressure();

                        for (Measurement m : selectedStationsMeasurements) {
                            if (!isFirstOneSkipped) {
                                isFirstOneSkipped = true;
                                continue;
                            }
                            if (m.getTime().toEpochDay() == pastDate.toEpochDay()) {
                                if (m.getPressure() < minValue) {
                                    minValue = m.getPressure();
                                }
                            } else {
                                data.add(minValue);
                                time.add(pastDate);
                                minValue = m.getPressure();
                            }
                            pastDate = m.getTime();
                        }
                        data.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getPressure());
                        time.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTime());

                        break;
                    }
                    case 1: {
                        LocalDate pastDate = selectedStationsMeasurements.get(0).getTime();
                        boolean isFirstOneSkipped = false;
                        int minValue = selectedStationsMeasurements.get(0).getHumidity();
                        for (Measurement m : selectedStationsMeasurements) {
                            if (!isFirstOneSkipped) {
                                isFirstOneSkipped = true;
                                continue;
                            }
                            if (m.getTime().toEpochDay() == pastDate.toEpochDay()) {
                                if (m.getHumidity() < minValue) {
                                    minValue = m.getHumidity();
                                }
                            } else {
                                data.add(minValue);
                                time.add(pastDate);
                                minValue = m.getHumidity();
                            }
                            pastDate = m.getTime();
                        }
                        data.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getHumidity());
                        time.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTime());
                        break;
                    }
                    case 2: {
                        LocalDate pastDate = selectedStationsMeasurements.get(0).getTime();
                        boolean isFirstOneSkipped = false;
                        int minValue = selectedStationsMeasurements.get(0).getTemperature();
                        for (Measurement m : selectedStationsMeasurements) {
                            if (!isFirstOneSkipped) {
                                isFirstOneSkipped = true;
                                if(selectedStationsMeasurements.size() == 1){
                                    data.add(m.getTemperature());
                                    time.add(m.getTime());
                                }
                                continue;
                            }
                            if (m.getTime().toEpochDay() == pastDate.toEpochDay()) {
                                if (m.getTemperature() < minValue) {
                                    minValue = m.getTemperature();
                                }
                            } else {
                                data.add(minValue);
                                time.add(pastDate);
                                minValue = m.getTemperature();
                            }
                            data.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTemperature());
                            time.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTime());
                            pastDate = m.getTime();
                        }
                        break;
                    }
                }
                break;
            }
            case 1: {
                switch (dataShownType) {
                    case 0: {
                        LocalDate pastDate = selectedStationsMeasurements.get(0).getTime();
                        boolean isFirstOneSkipped = false;
                        ArrayList<Integer> buffer = new ArrayList<>();
                        buffer.add(selectedStationsMeasurements.get(0).getPressure());
                        for (Measurement m : selectedStationsMeasurements) {
                            if (!isFirstOneSkipped) {
                                isFirstOneSkipped = true;
                                continue;
                            }
                            if (m.getTime().toEpochDay() == pastDate.toEpochDay()) {
                                buffer.add(m.getPressure());
                            } else {
                                int bufferSum = 0;
                                for (Integer i : buffer) bufferSum += i;
                                int average = (int) (bufferSum / buffer.size());
                                buffer.clear();
                                buffer.add(m.getPressure());
                                data.add(average);
                                time.add(pastDate);
                            }
                            pastDate = m.getTime();
                        }
                        data.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getPressure());
                        time.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTime());

                        break;
                    }
                    case 1: {
                        LocalDate pastDate = selectedStationsMeasurements.get(0).getTime();
                        boolean isFirstOneSkipped = false;
                        ArrayList<Integer> buffer = new ArrayList<>();
                        buffer.add(selectedStationsMeasurements.get(0).getHumidity());
                        for (Measurement m : selectedStationsMeasurements) {
                            if (!isFirstOneSkipped) {
                                isFirstOneSkipped = true;
                                continue;
                            }
                            if (m.getTime().toEpochDay() == pastDate.toEpochDay()) {
                                buffer.add(m.getHumidity());
                            } else {
                                int bufferSum = 0;
                                for (Integer i : buffer) bufferSum += i;
                                int average = (int) (bufferSum / buffer.size());
                                buffer.clear();
                                buffer.add(m.getHumidity());
                                data.add(average);
                                time.add(pastDate);
                            }
                            pastDate = m.getTime();
                        }
                        data.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getHumidity());
                        time.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTime());

                        break;
                    }
                    case 2: {
                        LocalDate pastDate = selectedStationsMeasurements.get(0).getTime();
                        boolean isFirstOneSkipped = false;
                        ArrayList<Integer> buffer = new ArrayList<>();
                        buffer.add(selectedStationsMeasurements.get(0).getTemperature());
                        for (Measurement m : selectedStationsMeasurements) {
                            if (!isFirstOneSkipped) {
                                isFirstOneSkipped = true;
                                if(selectedStationsMeasurements.size() == 1){
                                    data.add(m.getTemperature());
                                    time.add(m.getTime());
                                }
                                continue;
                            }
                            if (m.getTime().toEpochDay() == pastDate.toEpochDay()) {
                                buffer.add(m.getTemperature());
                            } else {
                                int bufferSum = 0;
                                for (Integer i : buffer) bufferSum += i;
                                int average = (int) (bufferSum / buffer.size());
                                buffer.clear();
                                buffer.add(m.getTemperature());
                                data.add(average);
                                time.add(pastDate);
                            }
                            data.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTemperature());
                            time.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTime());
                            pastDate = m.getTime();
                        }

                        break;
                    }
                }
                break;
            }
            case 2: {
                switch (dataShownType) {
                    case 0: {
                        LocalDate pastDate = selectedStationsMeasurements.get(0).getTime();
                        boolean isFirstOneSkipped = false;
                        int maxValue = selectedStationsMeasurements.get(0).getPressure();
                        for (Measurement m : selectedStationsMeasurements) {
                            if (!isFirstOneSkipped) {
                                isFirstOneSkipped = true;
                                continue;
                            }
                            if (m.getTime().toEpochDay() == pastDate.toEpochDay()) {
                                if (m.getPressure() > maxValue) {
                                    maxValue = m.getPressure();
                                }
                            } else {
                                data.add(maxValue);
                                time.add(pastDate);
                                maxValue = m.getPressure();
                            }
                            pastDate = m.getTime();
                        }
                        data.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getPressure());
                        time.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTime());

                        break;
                    }
                    case 1: {
                        LocalDate pastDate = selectedStationsMeasurements.get(0).getTime();
                        boolean isFirstOneSkipped = false;
                        int maxValue = selectedStationsMeasurements.get(0).getHumidity();
                        for (Measurement m : selectedStationsMeasurements) {
                            if (!isFirstOneSkipped) {
                                isFirstOneSkipped = true;
                                continue;
                            }
                            if (m.getTime().toEpochDay() == pastDate.toEpochDay()) {
                                if (m.getHumidity() > maxValue) {
                                    maxValue = m.getHumidity();
                                }
                            } else {
                                data.add(maxValue);
                                time.add(pastDate);
                                maxValue = m.getHumidity();
                            }
                            pastDate = m.getTime();
                        }
                        data.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getHumidity());
                        time.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTime());
                        break;
                    }
                    case 2: {
                        LocalDate pastDate = selectedStationsMeasurements.get(0).getTime();
                        boolean isFirstOneSkipped = false;
                        int maxValue = selectedStationsMeasurements.get(0).getTemperature();
                        for (Measurement m : selectedStationsMeasurements) {
                            if (!isFirstOneSkipped) {
                                isFirstOneSkipped = true;
                                continue;
                            }
                            if (m.getTime().toEpochDay() == pastDate.toEpochDay()) {
                                if (m.getTemperature() > maxValue) {
                                    maxValue = m.getTemperature();
                                }
                            } else {
                                data.add(maxValue);
                                time.add(pastDate);
                                maxValue = m.getTemperature();
                            }
                            pastDate = m.getTime();
                        }
                        data.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTemperature());
                        time.add(selectedStationsMeasurements.get(selectedStationsMeasurements.size()-1).getTime());
                        break;
                    }
                }
                break;
            }
        }
        Stage graphStage = new Stage();
        switch (dataShownType) {
            case 0: {
                graphStage = graphStage(time, data, "Ciśnienie(hPa)");
                break;
            }
            case 1: {
                graphStage = graphStage(time, data, "Wilgotność(%)");
                break;
            }
            case 2: {
                graphStage = graphStage(time, data, "Temperatura(C)");
                break;
            }
        }
        return graphStage;
    }

    private Stage graphStage(ArrayList<LocalDate> time, ArrayList<Integer> data, String dataType) {
        Stage graphStage = new Stage();
        graphStage.setTitle("Wykres");
        Group root = new Group();
        int height = 600;
        if (dataShownType == 2) height = 1000;

        Canvas canvas = new Canvas(800, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        draw(gc, time, data, dataType);
        root.getChildren().add(canvas);
        graphStage.setScene(new Scene(root));
        return graphStage;
    }

    private void draw(GraphicsContext gc, ArrayList<LocalDate> time, ArrayList<Integer> data, String dataType) {
        int yLength = 550;
        if (dataShownType == 2) yLength = 990;

        gc.strokeLine(50, 550, 750, 550); //x
        gc.fillPolygon(new double[]{750, 750, 760}, new double[]{545, 555, 550}, 3);//x
        gc.strokeLine(50, yLength, 50, 50); //y
        gc.fillPolygon(new double[]{45, 55, 50}, new double[]{50, 50, 40}, 3);
        gc.fillText(dataType, 5, 35);
        gc.fillText("Czas(dni)", 720, 570);


        double xUnit;
        double yUnit;
        ArrayList<Point> points = new ArrayList<>();
        int k = 0;
        for (LocalDate singleDate : time) {
            points.add(new Point(singleDate, data.get(k)));
            k++;
        }

        points.sort(new Comparator<>() {
            @Override
            public int compare(Point a, Point b) {
                if (a.getDate().toEpochDay() < b.getDate().toEpochDay()) {
                    return -1;
                } else return 1;
            }
        });
        int dataMax = 0;
        for (Integer i : data) {
            if (i > dataMax) dataMax = i;
        }
        yUnit = 500.00 / dataMax;


        long daysQuantity = (int) (points.get(points.size() - 1).getDate().toEpochDay() - points.get(0).getDate().toEpochDay()) + 2;
        xUnit = 700.00 / daysQuantity;
        // drawing point (x,y)R example gc.fillOval(x-2,y-2,4,4); gc.fillOval(x-R,y-R,2R,2R);
        int i = 0;
        for (Point p : points) {
            int daySinceFirst = (int) (p.getDate().toEpochDay() - points.get(0).getDate().toEpochDay());
            int x = (int) (xUnit * daySinceFirst + (50 - 2) + xUnit);
            int y = (int) (550 - yUnit * p.getData() - 2);
            gc.fillOval(x, y, 4, 4);
        }
    }


    private Stage stationMakerStage(DataBase dataBase) {
        Stage stage = new Stage();
        stage.setTitle("Tworzenie nowej stacji");
        VBox sceneLayout = new VBox(8);

        HBox idBox = new HBox(8);
        Label idLabel = new Label("ID: ");
        TextField id = new TextField();
        id.setPromptText("ID ");
        idBox.getChildren().addAll(idLabel, id);

        HBox nameBox = new HBox(8);
        Label nameLabel = new Label("Nazwa stacji:");
        TextField name = new TextField();
        name.setPromptText("nazwa");
        nameBox.getChildren().addAll(nameLabel, name);

        Button done = new Button("Dodaj");
        done.setOnAction(e -> {
            int idGiven = 0;
            String nameGiven = "";
            Alert a;
            try {

                idGiven = Integer.parseInt(id.getText());
                if (idGiven < 1) {
                    a = new Alert(Alert.AlertType.ERROR, "Wartość ID nie może być mniejsza niż 1.", ButtonType.OK);
                    a.show();
                    return;
                }
                for (Station s : dataBase.getStations()) {
                    if (idGiven == s.getId()) {
                        a = new Alert(Alert.AlertType.ERROR, "Stacja o podanym ID już istnieje. Wybierz inne ID stacji lub usuń istniejącą stację o podanym ID.", ButtonType.OK);
                        a.show();
                        return;
                    }
                }
                try {
                    nameGiven = name.getText();
                    Station newStation = new Station(nameGiven, idGiven);
                    dataBase.addStation(newStation);
                    stage.close();
                    gridPane.getChildren().remove(stationsScrollPane);
                    stationsScrollPane = buildStationsBox(dataBase);
                    GridPane.setConstraints(stationsScrollPane, 0, 0);
                    gridPane.getChildren().add(stationsScrollPane);
                } catch (Exception ex) {
                    a = new Alert(Alert.AlertType.ERROR, "Wprowadzono nieprawidłową wartość w polu nazwa.", ButtonType.OK);
                    a.show();
                }
            } catch (NumberFormatException ex) {
                a = new Alert(Alert.AlertType.ERROR, "Wprowadzono nieprawidłową wartość w polu ID.", ButtonType.OK);
                a.show();
            }
        });
        sceneLayout.getChildren().addAll(idBox, nameBox, done);
        sceneLayout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(sceneLayout, 250, 200);
        stage.setScene(scene);
        return stage;
    }

    private void addRandomData(LinkedList<Station> stations, LocalDate from, LocalDate to, int amount,
                               int maxTemp, int maxPressure) {
        long minDate = from.toEpochDay();
        long maxDate = to.toEpochDay();

        for (Station s : stations) {
            for (int i = 0; i < amount; i++) {
                long randomDay = ThreadLocalRandom.current().nextLong(minDate, maxDate);
                Random rand = new Random();
                int rTemperature = (int) (rand.nextDouble() * 2 * maxTemp) - maxTemp;
                int rPressure = (int) (rand.nextDouble() * (maxPressure - 900)) + 900;
                int rHumidity = rand.nextInt(101);
                s.addMeasurement(new Measurement(s.getId(), rPressure, rHumidity, rTemperature, LocalDate.ofEpochDay(randomDay)));
            }
        }
    }
}

