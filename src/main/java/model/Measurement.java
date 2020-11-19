package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import service.LocalDateDeserializer;
import service.LocalDateSerializer;

import java.time.LocalDate;

public class Measurement {
    private int stationID;
    private int pressure;
    private int humidity;
    private int temperature;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate time;

    public Measurement(int stationID, int pressure, int humidity, int temperature, int day, int month, int year) {
        this.stationID = stationID;
        this.pressure = pressure;
        this.humidity = humidity;
        this.temperature = temperature;
        this.time = LocalDate.of(year,month,day);
    }
    @JsonCreator
    public Measurement(@JsonProperty("stationID") int stationID,@JsonProperty("pressure") int pressure,@JsonProperty("humidity") int humidity, @JsonProperty("temperature") int temperature, @JsonProperty("time") LocalDate time){
        this.stationID = stationID;
        this.pressure = pressure;
        this.humidity = humidity;
        this.temperature = temperature;
        this.time = time;
    }
    

    public int getStationID() {
        return stationID;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public LocalDate getTime() {
        return time;
    }

}
