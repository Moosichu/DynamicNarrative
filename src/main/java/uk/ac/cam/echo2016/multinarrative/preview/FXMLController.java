package uk.ac.cam.echo2016.multinarrative.preview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import uk.ac.cam.echo2016.multinarrative.GameChoice;
import uk.ac.cam.echo2016.multinarrative.GraphElementNotFoundException;
import uk.ac.cam.echo2016.multinarrative.NarrativeInstance;
import uk.ac.cam.echo2016.multinarrative.Node;
import uk.ac.cam.echo2016.multinarrative.Route;
import uk.ac.cam.echo2016.multinarrative.dev.Debug;
import uk.ac.cam.echo2016.multinarrative.preview.preprocessor.FileProcessor;

/**
 * Controller for HTML previewer
 * @author jr650
 *
 */
public class FXMLController implements Initializable {

	@FXML
	private HBox choices;

	@FXML
	private Button next;

	@FXML
	private WebView web;

	@FXML
	private ScrollPane scroll;

	private WebEngine engine;

	private File dir;
	private File index;

	private NarrativeInstance instance;

	private ArrayList<Node> pages = new ArrayList<>();
	private ArrayList<Route> options = new ArrayList<>();
	private Route routePlaying;

	private FileProcessor proc;

	/**
	 * Initialises, called from GUI bootstrap
	 * @param file File source directory
	 * @param inst
	 */
	public void init(File file, NarrativeInstance inst) {
		dir = file;
		index = new File(dir.getAbsolutePath() + File.separator + "index.html");
		instance = inst;
		proc = new FileProcessor(FileProcessor.getDefaultProcessor(inst));
		options = inst.getPlayableRoutes();
		
		choices.minWidthProperty().bind(scroll.widthProperty().subtract(10));
		choices.minHeightProperty().bind(scroll.heightProperty().subtract(10));

		display(inst.getStart());
		inst.getStart().createProperties();
		inst.getStart().getProperties().putBoolean(NarrativeInstance.SYSTEM_ISCOMPLETED, true);
		initChoices(options);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		engine = web.getEngine();
		next.disableProperty().bind(engine.getLoadWorker().runningProperty());
	}

	/**
	 * FXML hook. When next button pressed
	 */
	@FXML
	public void onNext(ActionEvent event) {
		Debug.logInfo("Next Pressed, pages=" + pages, 2, Debug.SYSTEM_PREVIEW);
		if (!pages.isEmpty()) {
			nextPage();
		} else if (routePlaying != null) {
			try {
				GameChoice c = instance.endRoute(routePlaying.getId());
				options = c.getOptions();
				if (c.hasEvent()) {
					for (Node n : c.getEvents()) {
						if (n.getProperties() != null
								&& n.getProperties().getBoolean(HTMLPreview.SKIP_PROPERTY, false)) {
							Debug.logInfo("Skipping: " + n.getId(), 2, Debug.SYSTEM_PREVIEW);
						} else {
							pages.add(n);
						}
					}
					c.completeEvents();
					if (!pages.isEmpty()) {
						nextPage();
					} else {
						initChoices(options);
					}
				} else {
					initChoices(options);
				}
			} catch (GraphElementNotFoundException e) {
				Debug.logError(e, 2, Debug.SYSTEM_PREVIEW);
				throw new RuntimeException(e);
			}
			routePlaying = null;
		} else

		{
			initChoices(options);
		}

	}

	/**
	 * Change to next page
	 */
	public void nextPage() {
		Node s = pages.remove(0);
		while (!pages.isEmpty() && s.getProperties() != null
				&& s.getProperties().getBoolean(NarrativeInstance.SYSTEM_ISCOMPLETED, false))
			s = pages.remove(0);
		Debug.logInfo("Starting " + s, 2, Debug.SYSTEM_PREVIEW);
		display(s);
		if (pages.isEmpty()) {
			initChoices(options);
		}
	}

	/**
	 * Create options
	 * @param items options to add
	 */
	public void initChoices(ArrayList<Route> items) {
		choices.getChildren().clear();
		for (Route s : items) {
			Button b = createOption(s);
			choices.getChildren().add(b);
		}
	}

	/**
	 * Set to only have next button
	 */
	public void initNext() {
		choices.getChildren().clear();
		choices.getChildren().add(next);
	}

	/**
	 * Choose an item
	 * @param item the choice
	 */
	public void choose(Route item) {
		Debug.logInfo("Choosen: " + item, 2, Debug.SYSTEM_PREVIEW);
		try {
			instance.startRoute(item.getId());
			routePlaying = item;
			display(item);
			initNext();
		} catch (GraphElementNotFoundException e) {
			Debug.logError(e, 2, Debug.SYSTEM_PREVIEW);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Display the given node
	 * @param node node to display
	 */
	public void display(Node node) {
		String item = node.getId();
		if (node.getProperties() != null && node.getProperties().getString(HTMLPreview.FILE_PROPERTY, null) != null) {
			item = node.getProperties().getString(HTMLPreview.FILE_PROPERTY);
		}
		parseFrom(item);
		try {
			setContents(index);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Display a route
	 * @param route route to display
	 */
	public void display(Route route) {
		String item = route.getId();
		if (route.getProperties() != null && route.getProperties().getString(HTMLPreview.FILE_PROPERTY, null) != null) {
			item = route.getProperties().getString(HTMLPreview.FILE_PROPERTY);
		}
		parseFrom(item);
		try {
			setContents(index);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reloads contents
	 */
	public void setContents() {
		try {

			setContents(index);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets contents to given file
	 * @param file file to load
	 * @throws MalformedURLException if you've mucked up
	 */
	public void setContents(File file) throws MalformedURLException {
		Debug.logInfo("Loading: " + file.getName(), 5, Debug.SYSTEM_PREVIEW);
		if (!file.exists()) {
			Debug.logError(file.getName() + " does not exist", 1, Debug.SYSTEM_PREVIEW);
		}
		String s = file.toURI().toURL().toString();
		engine.load(s);
	}

	/**
	 * Parses a file into index
	 * @param file file to parse
	 */
	public void parseFrom(String file) {
		PrintStream s = null;
		try {
			s = new PrintStream(index);
			File[] c = dir.listFiles((File direc, String name) -> {
				return name.equals(file + ".html");
			});
			if (c.length > 0) {
				proc.process(c[0], s);
			} else {
				Debug.logError("File Not Found: " + file, 2, Debug.SYSTEM_PREVIEW);
				s.println("<!DOCTYPE html>");
				s.println("<html>");
				s.println("<body>");
				s.println("<h1>" + file + "</h1>");
				s.println("</body>");
				s.println("</html>");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}

	/**
	 * Creates an option
	 * @param route route choice if pressed
	 * @return button for this option
	 */
	public Button createOption(Route route) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("button.fxml"));
			Button b = loader.load();
			String text = route.getId();
			if (route.getProperties() != null
					&& route.getProperties().getString(HTMLPreview.DISPLAY_PROPERTY, null) != null) {
				text = route.getProperties().getString(HTMLPreview.DISPLAY_PROPERTY);
			}
			b.setText(text);
			if(route.getProperties() != null
					&& route.getProperties().getString(HTMLPreview.STYLE_PROPERTY, null) != null) {
						b.setStyle(route.getProperties().getString(HTMLPreview.STYLE_PROPERTY));
					}
			b.disableProperty().bind(engine.getLoadWorker().runningProperty());
			b.setOnAction(e -> choose(route));
			return b;
		} catch (IOException ioe) {
			// Indicates that fxml files aren't set up properly...
			throw new RuntimeException("FXML files not configured correctly", ioe);
		}
	}
}
