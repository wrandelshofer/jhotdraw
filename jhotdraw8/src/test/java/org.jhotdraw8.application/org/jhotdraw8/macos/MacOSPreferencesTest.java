package org.jhotdraw8.macos;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableMaps;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import javax.xml.datatype.DatatypeFactory;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class MacOSPreferencesTest {
    @NonNull
    @TestFactory
    List<DynamicTest> test() {
        List<DynamicTest> list = new ArrayList<>();
        for (String file : Arrays.asList("XML Property List.plist",
                "Binary Property List.plist")) {
            list.addAll(Arrays.asList(
                    dynamicTest("nonexistent key", () -> doTest(file, "key", null)),
                    dynamicTest("array", () -> doTest(file, "a array", Arrays.asList("the item 0 value", "the item 1 value"))),
                    dynamicTest("dict", () -> doTest(file, "a dict", ImmutableMaps.of("a child 1", "the child 1 value", "a child 2", "the child 2 value").asMap())),
                    dynamicTest("sub-dict access", () -> doTest(file, "a dict\ta child 2", "the child 2 value")),

                    dynamicTest("boolean false", () -> doTest(file, "a boolean false", false)),
                    dynamicTest("boolean true", () -> doTest(file, "a boolean true", true)),
                    dynamicTest("data", () -> doTest(file, "a data", new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe})),
                    dynamicTest("date", () -> doTest(file, "a date", DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-11-09T11:39:03Z"))),
                    dynamicTest("float", () -> doTest(file, "a float", 0.42)),
                    dynamicTest("integer", () -> doTest(file, "a integer", 42L)),
                    dynamicTest("long", () -> doTest(file, "a long", 4294967296L)),
                    dynamicTest("string", () -> doTest(file, "a string", "The String Value"))
            ));
        }
        return list;

    }

    private void doTest(String filename, @NonNull String key, Object expectedValue) throws URISyntaxException {
        File file = new File(getClass().getResource(filename).toURI());
        System.out.println(filename + ", " + key.replaceAll("\t", "→") + " = " + expectedValue);
        final Object actualValue = MacOSPreferences.get(file, key);
        if (expectedValue instanceof byte[]) {
            assertArrayEquals((byte[]) expectedValue, (byte[]) actualValue, "key=" + key);
        } else {
            assertEquals(expectedValue, actualValue, "key=" + key);
        }
    }
}