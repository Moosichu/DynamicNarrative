package uk.ac.cam.echo2016.multinarrative.preview;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uk.ac.cam.echo2016.multinarrative.NarrativeTemplate;
import uk.ac.cam.echo2016.multinarrative.io.SaveReader;

/**
 * Previewer for HTML 
 * @author jr650
 *
 */
public class HTMLPreview extends Application {

	public static String FILE_PROPERTY = "file";
	public static String DISPLAY_PROPERTY = "display";
	public static String SKIP_PROPERTY = "skip";
	public static String STYLE_PROPERTY = "style";

	/**
	 * Entry point ofr HTML previewer
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(HTMLPreview.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		String[] args = getParameters().getRaw().toArray(new String[getParameters().getRaw().size()]);
		if (args.length != 1) {
			System.out.println("Usage java uk.ac.cam.echo2016.multinarrative.preview.TextPreview <directory>");
			return;
		}
		File f = new File(args[0]);
		if (!f.exists() || !f.isDirectory()) {
			System.out.println("Usage java uk.ac.cam.echo2016.multinarrative.preview.TextPreview <directory>");
			return;
		}
		System.out.println("Loading from: " + f.getPath());
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
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml_layout.fxml"));
			Parent root = loader.load();
			FXMLController controller = loader.getController();
			controller.init(f, t.generateInstance());
			stage.setTitle("DyNaDeMo");
			stage.setScene(new Scene(root, 900, 600));
			stage.show();
		} catch (IOException ioe) {
			// Indicates that fxml files aren't set up properly...
			throw new RuntimeException("FXML files not configured correctly", ioe);
		}
	}

}
