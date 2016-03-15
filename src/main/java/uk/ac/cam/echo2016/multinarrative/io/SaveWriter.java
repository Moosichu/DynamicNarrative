package uk.ac.cam.echo2016.multinarrative.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SaveWriter {
    public static void saveObject(String filename, Object toSave) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(toSave);
        } finally {
            oos.close();
        }
    }
}
