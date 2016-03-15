package uk.ac.cam.echo2016.multinarrative;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import uk.ac.cam.echo2016.multinarrative.io.SaveReader;
import uk.ac.cam.echo2016.multinarrative.io.SaveWriter;

import java.io.IOException;

public class SaveTest {

    GUINarrative gNarr;

    // Taken from GUINarrativeTest
    @Before
    public void setup() throws IOException {
        gNarr = new GUINarrative();

        SynchronizationNode s1 = new SynchronizationNode("start");
        ChoiceNode c1 = new ChoiceNode("choice1");
        SynchronizationNode s2 = new SynchronizationNode("left");
        SynchronizationNode s3 = new SynchronizationNode("right");
        SynchronizationNode s4 = new SynchronizationNode("end");
        Route l1 = new Route("link1", s1, c1);
        l1.setup();
        Route l2 = new Route("link2", c1, s2);
        l2.setup();
        Route l3 = new Route("link3", c1, s3);
        l3.setup();
        Route l4 = new Route("link4", s2, s4);
        l4.setup();
        Route l5 = new Route("link5", s3, s4);
        l5.setup();
        gNarr.addNode(s1);
        gNarr.addNode(c1);
        gNarr.addNode(s2);
        gNarr.addNode(s3);
        gNarr.addNode(s4);
        gNarr.addRoute(l1);
        gNarr.addRoute(l2);
        gNarr.addRoute(l3);
        gNarr.addRoute(l4);
        gNarr.addRoute(l5);
        gNarr.start = s1;

        SaveWriter.saveObject("test.gui", gNarr);
        gNarr = null;
        gNarr = SaveReader.loadGUINarrative("test.gui");
    }

    @Test
    public void structureTest() {
        assertEquals("Check root is correct", "start", gNarr.start.getId());
        assertEquals("Check root has correct exit route", "link1", gNarr.start.getExiting().get(0).getId());
        Node c1 = gNarr.start.getExiting().get(0).getEnd();
        assertEquals("Check child of link1", "choice1", c1.getId());
        assertEquals("Check choice1 has correct number of children", 2, c1.getExiting().size());
        assertEquals("Check choice1 has correct left route", "link2", c1.getExiting().get(0).getId());
        assertEquals("Check choice1 has correct right route", "link3", c1.getExiting().get(1).getId());
        assertEquals("Check choice1 has correct number of parents", 1, c1.getEntering().size());
        assertEquals("Check choice1 has correct parent", "link1", c1.getEntering().get(0).getId());
        Node left = c1.getExiting().get(0).getEnd();
        Node right = c1.getExiting().get(1).getEnd();
        assertEquals("Check choice1 has correct left child", "left", left.getId());
        assertEquals("Check choice1 has correct right child", "right", right.getId());
        assertEquals("Check left has correct child route", "link4", left.getExiting().get(0).getId());
        assertEquals("Check right has correct child route", "link5", right.getExiting().get(0).getId());
        assertEquals("Check left leads into end node", "end", left.getExiting().get(0).getEnd().getId());
        assertEquals("Check right leads into end node", "end", right.getExiting().get(0).getEnd().getId());
        assertEquals("Check left has correct parent", "link2", left.getEntering().get(0).getId());
        assertEquals("Check right has correct parent", "link3", right.getEntering().get(0).getId());
        Node end = left.getExiting().get(0).getEnd();
        assertEquals("Check end has correct number of parents", 2, end.getEntering().size());
        assertEquals("Check end has left as parent node", left, end.getEntering().get(0).getStart());
        assertEquals("Check end has right as parent node", right, end.getEntering().get(1).getStart());
    }

    @Test
    public void loadCastTest() throws IOException {
        NarrativeInstance inst = new NarrativeInstance();
        SaveWriter.saveObject("test.inst", inst);
        inst = null;
        inst = SaveReader.loadNarrativeInstance("test.inst");
        assertEquals("Check narrativeinstance loaded correctly", NarrativeInstance.class, inst.getClass());

        NarrativeTemplate template = new NarrativeTemplate();
        SaveWriter.saveObject("test.template", template);
        template = null;
        template = SaveReader.loadNarrativeTemplate("test.template");
        assertEquals("Check NarrativeTemplate loaded correctly", NarrativeTemplate.class, template.getClass());
    }
}
