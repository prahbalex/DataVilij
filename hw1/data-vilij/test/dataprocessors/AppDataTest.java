package dataprocessors;

import org.junit.Assert;
import org.junit.Test;


import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;

public class AppDataTest {
    /**
     * This one tests the save function with a real path
     */
    @Test
    public void saveDataNormal() {
        AppData appData = new AppData(null);
        String dataDirPath = "resources/data/savedatatest.tsd";
        File file = new File(dataDirPath);
        Path path = file.toPath();
        appData.saveData("s", path);
        String dataString = "";
        try {
            Scanner scanner = new Scanner(path);
            while (scanner.hasNext()) {
                dataString += scanner.nextLine();
            }
        } catch (Exception e) {
        }
        Assert.assertEquals("s", dataString);
    }

    /**
     * This one tests the save function with a path that is not real to show that it will break
     */
    @Test(expected = Exception.class)
    public void saveDataFail() throws Exception{
        AppData appData = new AppData(null);
        String dataDirPath = null;
        File file = new File(dataDirPath);
        Path path = file.toPath();
        appData.saveData("s", path);
    }
}
