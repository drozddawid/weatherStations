package model;

import java.util.LinkedList;

public class DataBase {
    private LinkedList<Station> stations;

    public DataBase() {
        this.stations = new LinkedList<>();
    }

    public LinkedList<Station> getStations() {
        return stations;
    }

    public void addStation(Station station){
        this.stations.add(station);
    }

}
