import model.DataBase;
import model.Measurement;
import model.Station;
import service.DataBaseDeserializer;
import service.DataBaseSerializer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*Measurement me = new Measurement(1,600,20,24, 1,12,2020);
        Station station = new Station("jakaStacja",1);
        station.addMeasurement(me);
        DataBase dataBase = new DataBase();
        dataBase.addStation(station);
        DataBaseSerializer dataBaseSerializer = new DataBaseSerializer();
        try {
            dataBaseSerializer.dataBaseToJson(dataBase, new File("dataBase.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        DataBaseDeserializer deserializer = new DataBaseDeserializer();
        try {
            DataBase dataBase = deserializer.deserialize(new File("dataBase.json"));
            System.out.println(dataBase.getStations().get(0).getName());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
