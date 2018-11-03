package org.jhotdraw8.css;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadableList;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CssFunctionProcessorTest {



    void doTestProcess(String expression, String expected) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        doc.getDocumentElement();
        Element elem = doc.createElement("Car");
        elem.setAttribute("id","o1");
        elem.setAttribute("doors","5");
        elem.setAttribute("length","3475mm");
        elem.setAttribute("width","1475mm");
        elem.setAttribute("height","1608mm");
        elem.setAttribute("rearBrakes","Drum");
        doc.appendChild(elem);

        StreamCssTokenizer tt = new StreamCssTokenizer(expression);
        StringBuilder buf=new StringBuilder();
        Consumer<CssToken> consumer=t->buf.append(t.fromToken());

        DocumentSelectorModel model = new DocumentSelectorModel();
        Map<String, ReadableList<CssToken>> customProperties=new LinkedHashMap<>();
        customProperties.put("--blarg", ImmutableList.of(new CssToken(CssTokenType.TT_STRING,"blarg")));
        customProperties.put("--endless-recursion",ImmutableList.of(new CssToken(CssTokenType.TT_FUNCTION,"var"),
                new CssToken(CssTokenType.TT_IDENT,"--endless-recursion"),
                new CssToken(CssTokenType.TT_RIGHT_BRACKET)));
        CssFunctionProcessor<Element> instance = new CssFunctionProcessor<>(model,customProperties);

        try {
            instance.process(elem, tt, consumer);
            if (expected==null) fail("must throw ParseException");
            assertEquals(expected,buf.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            if (expected!=null) fail("must not throw ParseException");
        }



    }

    @TestFactory
    public List<DynamicTest> testProcessFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestProcess("foo", "foo")),
                dynamicTest("2", () -> doTestProcess("attr(id)", "\"o1\"")),
                dynamicTest("3", () -> doTestProcess("attr(id", null)),
                dynamicTest("4", () -> doTestProcess("attr()", null)),
                dynamicTest("5", () -> doTestProcess("attr(,fallback)", null)),
                dynamicTest("6", () -> doTestProcess("attr(length, auto)", "\"3475mm\"")),
                dynamicTest("7", () -> doTestProcess("attr(length length, auto)", "3475mm")),
                dynamicTest("8", () -> doTestProcess("attr(length px, auto)", "3475px")),
                dynamicTest("9", () -> doTestProcess("attr(doors)", "\"5\"")),
                dynamicTest("10", () -> doTestProcess("attr(doors length)", "5")),
                dynamicTest("11", () -> doTestProcess("attr(doors % length)", "5%")),
                dynamicTest("12", () -> doTestProcess("attr(foo,fallback)", "fallback")),
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
                dynamicTest("801", () -> doTestProcess("inside", "inside")),
                dynamicTest("801", () -> doTestProcess("a b", "a b")),
                //
                dynamicTest("901", () -> doTestProcess("var(--blarg)", "\"blarg\"")),
                dynamicTest("902", () -> doTestProcess("var(--blarg,fallback)", "\"blarg\"")),
                dynamicTest("902", () -> doTestProcess("var(--foo,fallback)", "fallback")),
                dynamicTest("903", () -> doTestProcess("var(x,fallback)", null)),
                dynamicTest("904", () -> doTestProcess("var(--endless-recursion,fallback)", null))
        );
    }
}