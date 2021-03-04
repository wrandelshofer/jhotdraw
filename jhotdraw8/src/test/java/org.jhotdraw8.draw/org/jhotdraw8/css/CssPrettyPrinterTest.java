package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CssPrettyPrinterTest {
    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsPrettyPrinter() {
        return Arrays.asList(
                dynamicTest("1", () -> testPrettyPrint("", "")),
                dynamicTest("2", () -> testPrettyPrint("* {}", "* {}")),
                dynamicTest("3", () -> testPrettyPrint("* {  a:  1;  }", "* { a: 1; }")),
                dynamicTest("4", () -> testPrettyPrint("* {\na:1;\n}", "* {\n\ta:1;\n}"))
        );
    }

    private void testPrettyPrint(String str, String expected) throws Exception {
        StringWriter w = new StringWriter();
        CssPrettyPrinter instance = new CssPrettyPrinter(w);
        instance.print(str);
        String actual = w.toString();
        System.out.println(actual);
        assertEquals(expected, actual);
    }
}