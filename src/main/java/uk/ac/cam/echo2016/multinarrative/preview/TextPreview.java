package uk.ac.cam.echo2016.multinarrative.preview;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import uk.ac.cam.echo2016.multinarrative.GameChoice;
import uk.ac.cam.echo2016.multinarrative.GraphElementNotFoundException;
import uk.ac.cam.echo2016.multinarrative.InvalidGraphException;
import uk.ac.cam.echo2016.multinarrative.NarrativeInstance;
import uk.ac.cam.echo2016.multinarrative.NarrativeTemplate;
import uk.ac.cam.echo2016.multinarrative.Node;
import uk.ac.cam.echo2016.multinarrative.Route;
import uk.ac.cam.echo2016.multinarrative.io.SaveReader;
import uk.ac.cam.echo2016.multinarrative.preview.preprocessor.FileProcessor;

/**
 * Text Previewer class
 * @author jr650
 *
 */
public class TextPreview {

    private File directory;
    private NarrativeInstance inst;
    private FileProcessor proc;

    /**
     * Creates from a directory
     * @param f
     * @throws IOException
     * @throws InvalidGraphException
     */
    public TextPreview(File f) throws IOException, InvalidGraphException {
        File[] children = f.listFiles((File dir, String name) -> {
            return name.endsWith(".dnm");
        });
        if (children.length != 1) {
            throw new IOException("Could not find unique template file (.dnm)");
        }
        NarrativeTemplate t = SaveReader.loadNarrativeTemplate(children[0].getPath());
        if (t == null) {
            throw new IOException("Could not load graph from file");
        }
        inst = t.generateInstance();
        directory = f;
        proc = new FileProcessor(FileProcessor.getDefaultProcessor(inst));
    }

    /**
     * Previews to given set of input & output streams.
     * @param console console to display stuff to.
     * @param files where to output contents of files.
     * @param input where to read input from.
     */
    public void preview(PrintStream console, PrintStream files, InputStream input) {
        ArrayList<Route> items = inst.getPlayableRoutes();
        outputFile(inst.getStart().getId(), console, files, input, "Triggered");
        inst.getStart().createProperties();
        inst.getStart().getProperties().putBoolean("System.isCompleted", true);
        while (!items.isEmpty()) {
            GameChoice choice;
            String s = choose(items, console, input);
            do {
                choice = doRoute(s, console, files, input);

                if (choice.hasEvent()) {
                    for (Node n : choice.getEvents()) {
                        outputFile(n.getId(), console, files, input, "Triggered");
                    }
                    choice.completeEvents();
                }
                items = choice.getOptions();
                if (items.size() > 0) {
                    switch (choice.getAction()) {
                    case GameChoice.ACTION_CHOOSE_ROUTE:
                        console.println("Choose another narrative to play!");
                        break;
                    case GameChoice.ACTION_MAJOR_DECISION:
                        console.println("You must make a decision!");
                        break;
                    case GameChoice.ACTION_CONTINUE:
                        s = items.get(0).getId();
                        choose(null, console, input);
                        break;
                    }
                }
            } while (choice.getAction() == GameChoice.ACTION_CONTINUE);
        }
    }

    /**
     * Does a route
     * @param name name of route
     * @param output output stream
     * @param files stream to output files
     * @param input stream to output files to
     * @return the result of doing route
     */
    public GameChoice doRoute(String name, PrintStream output, PrintStream files, InputStream input) {
        try {
            inst.startRoute(name);
        } catch (GraphElementNotFoundException e) {
            System.err.println("IMPOSSIBLE_ERROR");
            e.printStackTrace();
            System.exit(1);
        }
        outputFile(name, output, files, input, "Playing");
        try {
            GameChoice choice = inst.endRoute(name);
            return choice;

        } catch (GraphElementNotFoundException e) {
            System.err.println("IMPOSSIBLE_ERROR");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * Choose a option
     * @param items choices
     * @param output output stream
     * @param input input stream
     * @return
     */
    @SuppressWarnings("resource")
    public static String choose(ArrayList<Route> items, PrintStream output, InputStream input) {
        if (items == null) {
            // Read a line
            return (new Scanner(input)).nextLine();
            // Don't close! since this closes input
        }
        if (items.size() == 0) {
            return null;
        }
        Scanner sc = new Scanner(input);
        // Don't close! since this closes input
        String r = null;
        do {
            output.println("Choose one of:");
            output.println(items);
            String choice = sc.nextLine();
            for (Route route : items) {
                if (route.getId().equals(choice)) {
                    r = route.getId();
                    break;
                }
            }
        } while (r == null);
        return r;
    }

    /**
     * Parses file to a stream
     * @param route name of route
     * @param output console to output to 
     * @param files here to output files
     * @param input input from here
     * @param prefix what to prefix message with if can't find file
     */
    public void outputFile(String route, PrintStream output, PrintStream files, InputStream input, String prefix) {
        File[] c = directory.listFiles((File dir, String name) -> {
            return name.equals(route + ".txt");
        });
        if (c.length > 0) {
            proc.process(c[0], files);
        } else {
            output.println(prefix + " " + route);
        }
    }

    /**
     * Entry point for Text Preview
     * @param args
     */
    public static void main(String[] args){
        if (args.length != 1) {
            System.out.println("Usage java uk.ac.cam.echo2016.multinarrative.preview.TextPreview <directory>");
            return;
        }
        try {
            runNarrative(args[0]);
        } catch(IOException ioe){
            System.out.println(ioe.getMessage());
        } catch (InvalidGraphException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Runs a narrative
     * @param narrativeDirectory directory to load from
     * @throws IOException if io goes badly
     * @throws InvalidGraphException if it's a bad file
     */
    public static void runNarrative(String narrativeDirectory) throws IOException, InvalidGraphException {
        File f = new File(narrativeDirectory);
        if (!f.exists()) {
            System.out.println("The directory " + narrativeDirectory + " doesn't seem to exist!");
            return;
        } else if(!f.isDirectory()) {
            System.out.println(narrativeDirectory + " is not a directory!");
            return;
        }
        System.out.println("Loading from: " + f.getPath());

        TextPreview preview = new TextPreview(f);
        preview.preview(System.out, System.out, System.in);
    }
}
