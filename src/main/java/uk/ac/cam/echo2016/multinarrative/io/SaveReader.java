package uk.ac.cam.echo2016.multinarrative.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import uk.ac.cam.echo2016.multinarrative.GUINarrative;
import uk.ac.cam.echo2016.multinarrative.NarrativeInstance;
import uk.ac.cam.echo2016.multinarrative.NarrativeTemplate;

public class SaveReader {
    public static GUINarrative loadGUINarrative(InputStream is) throws IOException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(is);
            return (GUINarrative) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException();
        } finally {
            ois.close();
        }
    }
    
    public static GUINarrative loadGUINarrative(String filename) throws IOException {
        return loadGUINarrative(new FileInputStream(filename));
    }
    
    public static NarrativeInstance loadNarrativeInstance(InputStream is) throws IOException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(is);
            return (NarrativeInstance) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException();
        } finally {
            ois.close();
        }
    }
    
    public static NarrativeInstance loadNarrativeInstance(String filename) throws IOException {
        return loadNarrativeInstance(new FileInputStream(filename));
    }

    public static NarrativeTemplate loadNarrativeTemplate(InputStream is) throws IOException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(is);
            return (NarrativeTemplate) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException();
        } finally {
            ois.close();
        }
    }
    
    public static NarrativeTemplate loadNarrativeTemplate(String filename) throws IOException {
        return loadNarrativeTemplate(new FileInputStream(filename));
    }
}
