package org.jhotdraw8.css;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadOnlyList;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ExtendedCssFunctionProcessorTest extends SimpleCssFunctionProcessorTest {

    protected CssFunctionProcessor<Element> createInstance( DocumentSelectorModel model, Map<String, ImmutableList<CssToken>> customProperties) {
        return new ExtendedCssFunctionProcessor<>(model,customProperties);
    }


    @TestFactory
    public List<DynamicTest> testProcessingOfExtendedFunctionsFactory() {
        return Arrays.asList(
                dynamicTest("301", () -> doTestProcess("concat()", "\"\"")),
                dynamicTest("302", () -> doTestProcess("concat(\"a\",\"b\")", "\"ab\"")),
                //
                dynamicTest("401", () -> doTestProcess("concat(attr(id),\"x\")", "\"o1x\"")),
                //
                dynamicTest("501", () -> doTestProcess("replace(\"aabfooaabfooabfoob\")", null)),
                dynamicTest("502", () -> doTestProcess("replace(\"aabfooaabfooabfoob\",\"a*b\")", null)),
                dynamicTest("503", () -> doTestProcess("replace(\"aabfooaabfooabfoob\",\"a*b\",\"-\")", "\"-foo-foo-foo-\"")),
                //
                dynamicTest("601", () -> doTestProcess("replace(attr(id),\"\\\\d\",\"x\")", "\"ox\"")),
                //
                dynamicTest("701", () -> doTestProcess("round(0.5)", "1")),
                dynamicTest("702", () -> doTestProcess("round(-0.5)", "0")),
                dynamicTest("703", () -> doTestProcess("round(1.6)", "2")),
                dynamicTest("704", () -> doTestProcess("round(1.4)", "1")),
                dynamicTest("705", () -> doTestProcess("round(1.6m)", "2m")),
                dynamicTest("706", () -> doTestProcess("round(1.6%)", "2%"))
        );
    }
}