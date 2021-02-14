package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.function.AttrCssFunction;
import org.jhotdraw8.css.function.CalcCssFunction;
import org.jhotdraw8.css.function.CssFunction;
import org.jhotdraw8.css.function.VarCssFunction;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class SimpleCssFunctionProcessorTest extends AbstractCssFunctionProcessorTest {

    protected CssFunctionProcessor<Element> createInstance(DocumentSelectorModel model, Map<String, ImmutableList<CssToken>> customProperties) {
        List<CssFunction<Element>> functions = new ArrayList<>();
        functions.add(new AttrCssFunction<>());
        functions.add(new CalcCssFunction<>());
        functions.add(new VarCssFunction<>());
        return new SimpleCssFunctionProcessor<>(functions, model, customProperties);
    }


    @TestFactory
    public @NonNull List<DynamicTest> testProcessingOfStandardFunctionsFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestProcess("foo", "foo")),
                dynamicTest("2", () -> doTestProcess("attr(id)", "\"o1\"")),
                dynamicTest("3", () -> doTestProcess("attr(id", null)),
                dynamicTest("4", () -> doTestProcess("attr()", null)),
                dynamicTest("5", () -> doTestProcess("attr(,fallback)", null)),
                dynamicTest("6", () -> doTestProcess("attr(length, auto)", "\"3475mm\"")),
                dynamicTest("7", () -> doTestProcess("attr(length length, auto)", "3475mm")),
                dynamicTest("8", () -> doTestProcess("attr(length px, auto)", "3475px")),
                dynamicTest("8 unspecified fallback", () -> doTestProcess("attr(rearBrakes px)", "0px")),
                dynamicTest("8 explicit fallback", () -> doTestProcess("attr(rearBrakes px, 120px)", "120px")),
                dynamicTest("9", () -> doTestProcess("attr(doors)", "\"5\"")),
                dynamicTest("9 as string", () -> doTestProcess("attr(doors string)", "\"5\"")),
                dynamicTest("10 as length", () -> doTestProcess("attr(doors length)", "5")),
                dynamicTest("11 as percentage", () -> doTestProcess("attr(doors %)", "5%")),
                dynamicTest("12", () -> doTestProcess("attr(foo length,fallback)", "fallback")),
                dynamicTest("13", () -> doTestProcess("foo(attr(id))", "foo(\"o1\")")),
                dynamicTest("14", () -> doTestProcess("foo()", "foo()")),
                //
                dynamicTest("101", () -> doTestProcess("calc()", null)),
                dynamicTest("102", () -> doTestProcess("calc(1)", "1")),
                dynamicTest("103", () -> doTestProcess("calc(1+2)", null)),
                dynamicTest("104", () -> doTestProcess("calc(1+ 2)", "3")),// FIXME should return null
                dynamicTest("105", () -> doTestProcess("calc(1 + 2)", "3")),
                dynamicTest("106", () -> doTestProcess("calc(6*7)", "42")),
                //
                dynamicTest("201", () -> doTestProcess("calc(attr(length mm) + 5mm)", "3480mm")),
                dynamicTest("202", () -> doTestProcess("calc(attr(length mm) / 5)", "695mm")),
                dynamicTest("203", () -> doTestProcess("calc(attr(doors px)*0.5)", "2.5px")),
                dynamicTest("204", () -> doTestProcess("calc(attr(width length)*attr(length length))", "5125625mm")),
                dynamicTest("205", () -> doTestProcess("calc(2 + attr(doors number))", "7")),
                dynamicTest("206", () -> doTestProcess("calc(2% + attr(doors number))", "7%")),
                dynamicTest("207", () -> doTestProcess("calc(2% + attr(doors mm))", "1891.763779527559%")),
                dynamicTest("208", () -> doTestProcess("calc(2mm + attr(doors mm))", "7mm")),
                //
                dynamicTest("801", () -> doTestProcess("inside", "inside")),
                dynamicTest("801", () -> doTestProcess("a b", "a b")),
                //
                dynamicTest("901", () -> doTestProcess("var(--blarg)", "\"blarg\"")),
                dynamicTest("902", () -> doTestProcess("var(--blarg,fallback)", "\"blarg\"")),
                dynamicTest("902", () -> doTestProcess("var(--foo,fallback)", "fallback")),
                dynamicTest("903", () -> doTestProcess("var(x,fallback)", null)),
                dynamicTest("904", () -> doTestProcess("var(--endless-recursion,fallback)", null)),
                dynamicTest("910", () -> doTestProcess("var(--recursion-base,fallback)", "\"recursion base\"")),
                dynamicTest("911", () -> doTestProcess("var(--recursive-1,fallback)", "\"recursion base\"")),
                dynamicTest("912", () -> doTestProcess("var(--recursive-2,fallback)", "\"recursion base\"")),
                //
                dynamicTest("1001", () -> doTestProcess("linear-gradient(from 0 0 to 6 6, repeat, var(--fill-gray) 60% var(--background-color) 60%)",
                        null))
        );
    }
}