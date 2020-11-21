package service;

import model.DataBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

/**
 * Class for serializing DataBase.class to *.json file
 */

public class DataBaseSerializer {
    public DataBaseSerializer() {
    }

    public void dataBaseToJson(DataBase dataBase, File file) throws IOException {
        ObjectMapper dataMapper = new ObjectMapper();
        dataMapper.writeValue(file,dataBase);
    }
}
