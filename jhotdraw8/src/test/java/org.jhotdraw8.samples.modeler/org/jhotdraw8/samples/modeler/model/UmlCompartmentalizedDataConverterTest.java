package org.jhotdraw8.samples.modeler.model;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.samples.modeler.text.CssUmlCompartmentalizedDataConverter;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class UmlCompartmentalizedDataConverterTest {

    void parseNonNull(String input, MLCompartmentalizedData expected) throws Exception {
        CssUmlCompartmentalizedDataConverter instance = new CssUmlCompartmentalizedDataConverter(false);
        MLCompartmentalizedData actual = instance.parseNonNull(new StreamCssTokenizer(input), null);
        assertEquals(expected, actual);
    }

    @TestFactory
    public @NonNull List<DynamicTest> parseNonNullFactory() {
        return Arrays.asList(
                // empty data
                dynamicTest("1", () -> parseNonNull("{}",
                        new MLCompartmentalizedData(ImmutableMaps.emptyMap()))),
                // quoted keys
                dynamicTest("2", () -> parseNonNull("{\"key\":[\"value\"]}",
                        new MLCompartmentalizedData(ImmutableMaps.of("key", ImmutableLists.of("value"))))),
                dynamicTest("3", () -> parseNonNull("{\"key\":[\"value1\",\"value2\"]}",
                        new MLCompartmentalizedData(ImmutableMaps.of("key", ImmutableLists.of("value1", "value2"))))),
                dynamicTest("4", () -> parseNonNull("{\"k1\":[\"v11\",\"v12\"],\"k2\":[\"v21\"]}",
                        new MLCompartmentalizedData(ImmutableMaps.of("k1", ImmutableLists.of("v11", "v12"), "k2", ImmutableLists.of("v21"))))),
                // unquoted keys
                dynamicTest("12", () -> parseNonNull("{key:[\"value\"]}",
                        new MLCompartmentalizedData(ImmutableMaps.of("key", ImmutableLists.of("value"))))),
                dynamicTest("13", () -> parseNonNull("{«key»:[\"value1\",\"value2\"]}",
                        new MLCompartmentalizedData(ImmutableMaps.of("«key»", ImmutableLists.of("value1", "value2"))))),
                dynamicTest("14", () -> parseNonNull("{k1:[\"v11\",\"v12\"],\"k2\":[\"v21\"]}",
                        new MLCompartmentalizedData(ImmutableMaps.of("k1", ImmutableLists.of("v11", "v12"), "k2", ImmutableLists.of("v21")))))
        );
    }
}