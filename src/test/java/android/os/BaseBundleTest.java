package android.os;

import android.os.BaseBundle;
import org.junit.Test;
import static org.junit.Assert.*;

public class BaseBundleTest {
    @Test
    public void testSetAndGetBoolean() {
        BaseBundle baseBundle = new BaseBundle();

        final String testKey1 = "Test";
        final boolean testVal1 = true;

        final String testKey2 = "Test2";
        final boolean testVal2 = false;

        baseBundle.putBoolean(testKey1, testVal1);
        baseBundle.putBoolean(testKey2, testVal2);

        assertEquals(baseBundle.getBoolean(testKey1), testVal1);
        assertEquals(baseBundle.getBoolean(testKey2), testVal2);
    }
}
