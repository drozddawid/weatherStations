package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.DataBase;
import java.io.File;
import java.io.IOException;

/**
 * Class for deserializing DataBase.class from *.json file
 */

public class DataBaseDeserializer {
    public DataBaseDeserializer() {
    }
    public DataBase deserialize(File file) throws IOException, ClassNotFoundException {
        ObjectMapper dataMapper = new ObjectMapper();
        return dataMapper.readValue(file, DataBase.class);
    }
}
