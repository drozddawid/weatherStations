package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.DataBase;
import java.io.File;
import java.io.IOException;

public class DataBaseDeserializer {
    public DataBaseDeserializer() {
    }
    public DataBase deserialize(File file) throws IOException, ClassNotFoundException {
        ObjectMapper dataMapper = new ObjectMapper();
        DataBase dataBase = dataMapper.readValue(file, DataBase.class);
        return dataBase;
    }
}
