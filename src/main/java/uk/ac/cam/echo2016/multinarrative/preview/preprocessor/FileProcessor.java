package uk.ac.cam.echo2016.multinarrative.preview.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import uk.ac.cam.echo2016.multinarrative.NarrativeInstance;
import uk.ac.cam.echo2016.multinarrative.dev.Debug;

/**
 * Preprocessor for files
 * @author jr650
 */
public class FileProcessor {

    Map<String, Function<String[], Function<String, String>>> commands;

    public FileProcessor(Map<String, Function<String[], Function<String, String>>> commands) {
        this.commands = commands;
    }

    /**
     * Processes a file
     * @param f file
     * @param files where to put the output
     */
    public void process(File f, PrintStream files) {
        try {
            BufferedReader r = new BufferedReader(new FileReader(f));
            Debug.logInfo("Found File " + f, 4, Debug.SYSTEM_PREVIEW);
            String s;
            ArrayList<Function<String, String>> applies = new ArrayList<Function<String, String>>();
            try {
                while ((s = r.readLine()) != null) {
                    Debug.logInfo("Read: " + s, 5, Debug.SYSTEM_PREVIEW);
                    if (s.startsWith("%")) {// Comment
                    } else if (s.startsWith("#")) {// Command
                        for (Function<String, String> func : applies) {
                            s = func.apply(s);
                            Debug.logInfo("> " + s, 5, Debug.SYSTEM_PREVIEW);
                            if (s == null) {
                                break;
                            }
                        }
                        if (s != null) {
                            String[] comm = s.substring(1).split(" ");
                            if (comm.length > 0) {
                                Function<String[], Function<String, String>> command = commands.get(comm[0]);
                                if (command != null) {
                                    Function<String, String> apply = command.apply(comm);
                                    if (apply != null) {
                                        applies.add(0, apply);
                                    }
                                } else {
                                    System.err.println("Invalid Command:\n" + s);
                                }
                            } else {
                                System.err.println("Invalid Command:\n" + s);
                            }
                        }
                    } else {
                        for (Function<String, String> func : applies) {
                            s = func.apply(s);
                            Debug.logInfo("> " + s, 5, Debug.SYSTEM_PREVIEW);
                            if (s == null) {
                                break;
                            }
                        }
                        if (s != null) {
                            files.println(s);
                        }
                    }
                }
            } catch (IOException e) {
                Debug.logError("Error Reading File " + f, 5, Debug.SYSTEM_PREVIEW);
            } finally {
                if (r != null) {
                    try {
                        r.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Debug.logError("File not found " + f, 5, Debug.SYSTEM_PREVIEW);
        }
    }

    /**
     * Gets standard preprocess options
     * @param inst the Narrative instance to base from
     * @return list of standard options
     */
    public static Map<String, Function<String[], Function<String, String>>> getDefaultProcessor(
            NarrativeInstance inst) {
        Map<String, Function<String[], Function<String, String>>> r = new HashMap<String, Function<String[], Function<String, String>>>();
        r.put("ifx", s -> {
            if (s.length < 2) {
                Debug.logError("Invalid ifx directive", 2, Debug.SYSTEM_PREVIEW);
                return null;
            } else {
                String str = "";
                for (int i = 1; i < s.length; i++) {
                    str += s[i];
                }
                boolean b = inst.getNode(str) != null || inst.getRoute(str) != null;
                Debug.logInfo(inst.getNodes().keySet() + "//" + inst.getRoutes().keySet(), 5,
                        Debug.SYSTEM_PREVIEW | Debug.SYSTEM_GRAPH);
                Debug.logInfo("ifx " + str + " = " + b, 4, Debug.SYSTEM_PREVIEW);
                return new IfProcessor(b);
            }
        });
        r.put("ifnx", s -> {
            if (s.length < 2) {
                Debug.logError("Invalid ifnx directive", 2, Debug.SYSTEM_PREVIEW);
                return null;
            } else {
                String str = "";
                for (int i = 1; i < s.length; i++) {
                    str += s[i];
                }
                boolean b = inst.getNode(str) != null || inst.getRoute(str) != null;
                Debug.logInfo(inst.getNodes().keySet() + "//" + inst.getRoutes().keySet(), 5,
                        Debug.SYSTEM_PREVIEW | Debug.SYSTEM_GRAPH);
                Debug.logInfo("ifnx " + str + " = " + !b, 4, Debug.SYSTEM_PREVIEW);
                return new IfProcessor(!b);
            }
        });
        return r;
    }

}
