package uk.ac.cam.echo2016.multinarrative.preview;

import java.io.IOException;

import org.junit.Test;

import uk.ac.cam.echo2016.multinarrative.InvalidGraphException;

public class TextPreviewTest {
    
    @Test
    public void scoobyDooTest() throws IOException, InvalidGraphException {
        TextPreview.runNarrative("../../../examples/Scooby Doo");
        //TODO: work out way to run Scooby Doo
    }
}
