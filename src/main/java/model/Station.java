package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Represents single weather station. Used to store data about it.
 */

public class Station {
    private String name;
    private int id;
    private ArrayList<Measurement> measurements;
    @JsonIgnore
    private boolean showInPlot;

    public Station(String name, int id) {
        this.name = name;
        this.id = id;
        measurements = new ArrayList<>();
        this.showInPlot = false;
    }
    @JsonCreator
    public Station(@JsonProperty("name") String name, @JsonProperty("id") int id, @JsonProperty("measurements") ArrayList<Measurement> measurements){
        this(name, id);
        this.measurements = measurements;
        this.showInPlot = false;
    }

    public String getName() {
        return name;
    }

    public void setShowInPlot(boolean showInPlot) {
        this.showInPlot = showInPlot;
    }

    public boolean isShowInPlot() {
        return showInPlot;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    public void addMeasurement(Measurement measurement){
        measurements.add(measurement);
    }

}
