package uk.ac.cam.echo2016.multinarrative.dev;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This is a class that provides several useful methods that can help in debugging.
 * 
 * @author tr395
 *
 */
public class Debug {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final int SYSTEM_NONE = 0b00000000000000000000000000000000;
    public static final int SYSTEM_ALL = 0b11111111111111111111111111111110;

    public static final int SYSTEM_ERROR = 0b00000000000000000000000000000001;

    // The types of configurable things
    public static final int SYSTEM_GUI =       0b10000000000000000000000000000000;
    public static final int SYSTEM_IO =        0b01000000000000000000000000000000;
    public static final int SYSTEM_GRAPH =     0b00100000000000000000000000000000;
    public static final int SYSTEM_PREVIEW =   0b00010000000000000000000000000000;

    // Different console log-levels, each index into the array represents a
    // log-level.
    // eg. if consoleLogLevels[3] = SYSTEM_ALL
    // when logInfo is called and the level is 2, everything will be logged
    // on initialisation, this array is set up so that the lower indexes in the
    // array
    // are configured to log everything from the higher indexes
    private static final int[] consoleLogLevels;

    private Debug() {
    }

    /*
     * loads in configuration data from the config.json file.
     */
    static {
        // initialise the configuration an array, each integer representing
        // which systems should be logged at each level.
        consoleLogLevels = new int[5];

        try {
            // Read-in the config file and convert it to a JsonObject
            String jsonString = new String(Files.readAllBytes(Paths.get("config.json")));
            JsonObject logConfig = new JsonParser().parse(jsonString).getAsJsonObject().getAsJsonObject("log");

            // get the configuration for logging to the console
            JsonObject consoleConfig = logConfig.getAsJsonObject("console");

            // for every logging level, read in details of systems to log
            for (int logLevel = consoleLogLevels.length; logLevel > 0; logLevel--) {
                // check if systems have been configured for that particular
                // level
                if (consoleConfig.has(Integer.toString(logLevel))) {

                    // read in the array of different systems to log for this
                    // particular level
                    JsonArray logLevelConfig = consoleConfig.getAsJsonArray(Integer.toString(logLevel));
                    int systemsLogged = SYSTEM_NONE; // assume nothing is logged
                                                     // for this level

                    // for every system in this log level, add it to the
                    // configuration array
                    for (int j = 0; j < logLevelConfig.size(); j++) {
                        String systemName = logLevelConfig.get(j).getAsString();
                        try {
                            // get the binary representation of the system and
                            // OR it so it is configured to be logged.
                            int newSys = Debug.class.getField("SYSTEM_" + systemName.toUpperCase()).getInt(null);
                            systemsLogged = systemsLogged | newSys;
                        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException
                                | SecurityException e) {
                            // TODO(tr395): handle these exceptions more sanely!
                            e.printStackTrace();
                        }
                    }
                    // update the array
                    consoleLogLevels[logLevel - 1] = systemsLogged;
                }
            }

            // small optimisation, make it so that higher-priority log-events
            // are considered low-priority as well
            for (int logLevel = consoleLogLevels.length - 1; logLevel > 0; logLevel--) {
                consoleLogLevels[logLevel - 1] = consoleLogLevels[logLevel] | consoleLogLevels[logLevel - 1];
            }

        } catch (IOException | ClassCastException | IllegalStateException e) {
            // config.json doesn't exist, so log everything
            consoleLogLevels[0] = SYSTEM_NONE;
            consoleLogLevels[1] = SYSTEM_NONE;
            consoleLogLevels[2] = SYSTEM_NONE;
            consoleLogLevels[3] = SYSTEM_NONE;
            consoleLogLevels[4] = SYSTEM_NONE;
            System.err.println("Could not find config.json. Using default levels");
        }
    }

    /**
     * Prints out the provided string provided the current PRIORITY_LEVEL for the type provided is at least as great as
     * the priority level provided and the type is one of the types configured to be printed. TODO(tr395): will also
     * similarly log to a file.
     *
     * usage: printError("Fairies initialised and eating ice-cream.", 2, TYPE_PUDDING | TYPE_FAIRY)
     *
     * Should be used for printing useful debugging information, for printing errors refer to the printError method.
     * 
     * @param s
     *            The String you wish to print
     * @param priorityLevel
     *            The int describing priority of what is being printed (valued 1-5), the lower the number the higher the
     *            priority. Only strings which are less than or equal to the current priority level set will be printed
     *            <p>
     *            A guidline of what each priority should mean is given below, however it is up to the programmer to
     *            abide by this.
     *            <ul>
     *            <li>1: Really important debug info, will only be printed in for major, important events at an average
     *            rate at about 1/2 times per hour or on program startup/shutdown.
     *            <li>2: Slightly less important debug info, maybe printed a bit more often than 1 or even less often,
     *            but used for less critical steps in the program.
     *            <li>3: Important information normally critical for debugging, but not useful assuming program i
     *            running normally.
     *            <li>4: Useful information for debugging, that you really don't want to see during the normal running
     *            of a program.
     *            <li>5: Really fine-grained debugging info, useful only for tracking down especially difficult bugs.
     *            </ul>
     * @param type
     *            The integer describing the type of debug info being printed (eg, related to gui, an error (special
     *            case), backend, saving, audio). Look at all private static final int's that are prefixed with 'TYPE_'
     *            for types available. If a particular piece of information belongs to multiple types, you can bitwise
     *            or them together. eg. TYPE_PUDDING | TYPE FAIRY.
     */
    public static void logInfo(String s, int level, int system) {
        logInfo(s, 3, level, system);
    }

    public static void logInfo(String s, int calls, int level, int system) {
        int[] logSystems = consoleLogLevels; // get config info
        if ((logSystems[level - 1] & system) != 0) { // check to see if configured to log this system at this level

            // get various info about the current thread, that can be noted in the log
            StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[calls];
            int lineNumber = stackTrace.getLineNumber(); // get line number of current piece of code
            String fileName = stackTrace.getFileName(); // get the file name of the current piece of code

            // add all the info to the debug string
            String debugString = lineNumber + " " + fileName + ": " + s;

            // print out to the error stream if it's an error being logged!
            if ((system & SYSTEM_ERROR) != 0) {
                System.err.println(debugString);
            } else {
                System.out.println(debugString);
            }
        }
    }

    /**
     * Prints out the provided string as an errory provided the current PRIORITY_LEVEL for errors is at least as great
     * as the priority level provided and the type is one of the types configured to be printed. TODO(tr395): will also
     * similarly log to a file.
     *
     * usage: printError("The trifle has just exploded fairies!", 1, TYPE_PUDDING | TYPE_FAIRY)
     *
     * Should be used for printing useful debugging information, for printing errors refer to the printError method.
     * 
     * @param s
     *            The String you wish to print
     * @param priorityLevel
     *            The int describing priority of what is being printed (valued 1-9), the lower the number the higher the
     *            priority. Only strings which are less than or equal to the current priority level set will be printed
     *            <p>
     *            A guidline of what each priority should mean is given below, however it is up to the programmer to
     *            abide by this.
     *            <ul>
     *            <li>1: Critical error, the program has had to abort as a result of it!
     *            <li>2: Really bad error, the program can continue and/or recover from issue, but there is a glaring
     *            inconsistency that should be fixed and program should be stopped as soon as possible.
     *            <li>3: Bad error, program can continue but problem should really be investigated as soon as possible.
     *            <li>4: Not a major concern, but still an error that shouldn't be happening. Can be ignored
     *            <li>5: Really small error that someone should get to eventually, but shouldn't be clogging up terminal
     *            unless programmer is out to actively fix bugs.
     *            </ul>
     * @param type
     *            The integer describing the type of debug info being printed (eg, related to gui, an error (special
     *            case), backend, saving, audio) Look at all private static final int's that are prefixed with 'TYPE_'
     *            for types available. If a particular piece of information belongs to multiple types, you can bitwise
     *            or them together. eg. TYPE_PUDDING | TYPE FAIRY.
     */
    public static void logError(String s, int logLevel, int type) {
        logInfo(s, 3, logLevel, type | SYSTEM_ERROR);
    }

    public static void logError(Throwable e, int logLevel, int type) {
        logInfo(e.getClass().getName() + ": " + e.getMessage(), 3, logLevel, type | SYSTEM_ERROR);
        StackTraceElement[] stack = e.getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            logInfo(" > " + stack[i], 3, logLevel, type | SYSTEM_ERROR);
        }
        if (e.getCause() != null) {
            logInfo("Caused by: ", 3, logLevel, type | SYSTEM_ERROR);
            logError(e.getCause(), logLevel, type | SYSTEM_ERROR);
        }
    }
}
