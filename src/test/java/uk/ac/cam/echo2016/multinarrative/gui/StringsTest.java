package uk.ac.cam.echo2016.multinarrative.gui;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <h1>HTML in Javadoc:</h1>
 * <p>
 * paragraph break:
 * </p>
 * line break: <br>
 * <blockquote> indented </blockquote>
 * <code> Font used for classes/methods/etc. </code>= {@code javadoc version}
 * 
 * <pre>
* Preformatted        (as typed 
                            in source)
 * </pre>
 * 
 * <i>(&lt;em&gt;) italic </i> <b>(&lt;strong&gt;) bold </b>
 * <q>quote</q> <a
 * href=http://www.oracle.com/technetwork/articles/java/index-137868.html>
 * hyperlink(javadoc guidelines)</a>
 **/

public class StringsTest {

    @Test
    public void testPopulateString() {
        assertEquals("A B C", Strings.populateString("%1 %2 %3", "A", "B", "C", "D"));
    }

}
