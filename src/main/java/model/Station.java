package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;

public class Station {
    private String name;
    private int id;
    private LinkedList<Measurement> measurements;

    public Station(String name, int id) {
        this.name = name;
        this.id = id;
        measurements = new LinkedList<>();
    }
    @JsonCreator
    public Station(@JsonProperty("name") String name, @JsonProperty("id") int id, @JsonProperty("measurements") LinkedList<Measurement> measurements){
        this(name, id);
        this.measurements = measurements;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public LinkedList<Measurement> getMeasurements() {
        return measurements;
    }

    public void addMeasurement(Measurement measurement){
        measurements.add(measurement);
    }

}
