/* @(#)CssParserTest.java
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.Selector;
import org.jhotdraw8.css.ast.SelectorGroup;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author wr
 */
public class CssSelectorTest {

    private static final String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    /**
     * Test various selectors.
     */
    public static void testSelector(@NonNull String stylesheet, @NonNull String before, @NonNull String expectedValue) throws Exception {
        System.out.println(stylesheet);
        //---
        CssParser p = new CssParser();
        Stylesheet ast = p.parseStylesheet(stylesheet);
        //
        System.out.println("AST: " + ast);
        if (!p.getParseExceptions().isEmpty()) {
            System.out.println("Errors: ");
            for (ParseException e : p.getParseExceptions()) {
                System.out.println("\033[31m " + e.getMessage() + " @ " + e.getErrorOffset() + "\033[0m");
            }
        }
        //---
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        // We do not want that the reader creates a socket connection!
        builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
        Document doc = builder.parse(new InputSource(new StringReader(before)));
        //---
        DocumentSelectorModel dsd = new DocumentSelectorModel();

        NodeList elements = doc.getElementsByTagName("*");
        for (int i = 0, n = elements.getLength(); i < n; i++) {
            Element elem = (Element) elements.item(i);
            for (StyleRule r : ast.getStyleRules()) {
                SelectorGroup sg = r.getSelectorGroup();
                if (sg.matches(dsd, elem)) {
                    System.out.println("  match " + sg.toString() + " " + elem);
                    for (Declaration d : r.getDeclarations()) {
                        elem.setAttribute(d.getPropertyName(), d.getTermsAsString());
                    }
                } else {
                    System.out.println(" !match " + sg.toString() + " " + elem);
                }
            }
        }

        //---
        Transformer t = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter w = new StringWriter();
        StreamResult result = new StreamResult(w);
        t.transform(source, result);
        String actualValue = w.toString();
        actualValue = actualValue.substring(XML_PREFIX.length());

        if (!actualValue.equals(expectedValue)) {
            System.out.println(" actual  : \033[31m" + actualValue + "\033[0m");
        } else {
            System.out.println(" actual  : " + actualValue);
        }
        System.out.println(" expected: " + expectedValue);
        //---
        assertEquals(expectedValue, actualValue);
    }

    @TestFactory
    public @NonNull List<DynamicTest> testVariousSelectors() {
        return Arrays.asList(
                dynamicTest("1", () -> testSelector("#vertex4 {\n"
                        + "    -jhotdraw-fill: yellow;\n"
                        + "}\n"
                        + "\n"
                        + "[class~=\"warning\"] {\n"
                        + "  color: red;\n"
                        + "}", "<xml><a/><b class=\"warning\"/></xml>", "<xml><a/><b class=\"warning\" color=\"red\"/></xml>")),
                dynamicTest("1", () -> testSelector("[att] {a:1}", "<xml><elem att=\"bla\"/></xml>", "<xml><elem a=\"1\" att=\"bla\"/></xml>")),
                dynamicTest("2", () -> testSelector("[att=val] {a:1}", "<xml><elem att=\"bla\"/><elem att=\"val\"/></xml>", "<xml><elem att=\"bla\"/><elem a=\"1\" att=\"val\"/></xml>")),
                dynamicTest("3", () -> testSelector("[att~=val] {a:1}", "<xml><elem att=\"bla blu\"/><elem att=\"val kilmer\"/></xml>", "<xml><elem att=\"bla blu\"/><elem a=\"1\" att=\"val kilmer\"/></xml>")),
                dynamicTest("4", () -> testSelector("[att~=val] {a:1}", "<xml><elem att=\"bla val blu\"/><elem att=\"kilmer val\"/></xml>", "<xml><elem a=\"1\" att=\"bla val blu\"/><elem a=\"1\" att=\"kilmer val\"/></xml>")),
                dynamicTest("5", () -> testSelector("[att|=val] {a:1}", "<xml><elem att=\"val\"/><elem att=\"val-kilmer\"/></xml>", "<xml><elem a=\"1\" att=\"val\"/><elem a=\"1\" att=\"val-kilmer\"/></xml>")),
                dynamicTest("6", () -> testSelector("[att|=val] {a:1}", "<xml><elem att=\"valkon\"/><elem att=\"kilmer-val\"/></xml>", "<xml><elem att=\"valkon\"/><elem att=\"kilmer-val\"/></xml>")),
                dynamicTest("7", () -> testSelector("text {text:\"bla\"}", "<xml/>", "<xml/>")),
                dynamicTest("8", () -> testSelector("text {font:Lorem  Ipsum}", "<xml/>", "<xml/>")),
                dynamicTest("9", () -> testSelector("text {font:\"Lorem  Ipsum\"}", "<xml/>", "<xml/>")),
                dynamicTest("10", () -> testSelector("box {image:http://imageserver.com/myimage.png blue green}", "<xml/>", "<xml/>")),
                dynamicTest("11", () -> testSelector("box {image:http://imageserver.com/myimage.png}", "<xml/>", "<xml/>")),
                dynamicTest("12", () -> testSelector("box {fill:rgb(12,15,16)}", "<xml/>", "<xml/>")),
                dynamicTest("13", () -> testSelector("box {image:http://pics.com/myimage.png blue green}", "<xml><box/></xml>", "<xml><box image=\"http://pics.com/myimage.png blue green\"/></xml>")),
                dynamicTest("14", () -> testSelector("box {image:http://pics.com/myimage.png}", "<xml><box/></xml>", "<xml><box image=\"http://pics.com/myimage.png\"/></xml>")),
                dynamicTest("15", () -> testSelector("box {fill:rgb(12,15,16)}", "<xml><box/></xml>", "<xml><box fill=\"rgb(12,15,16)\"/></xml>")),
                dynamicTest("16", () -> testSelector("box {image:url('http://pics.com/myimage.png') blue green}", "<xml><box/></xml>", "<xml><box image=\"url(http://pics.com/myimage.png) blue green\"/></xml>")),
                dynamicTest("17", () -> testSelector("box {image:url(http://pics.com/myimage.png)}", "<xml><box/></xml>", "<xml><box image=\"url(http://pics.com/myimage.png)\"/></xml>")),
                dynamicTest("18", () -> testSelector("box {image:url(http://bad com/myimage.png)}", "<xml><box/></xml>", "<xml><box/></xml>")),
                dynamicTest("19", () -> testSelector("* {;;;}", "<xml/>", "<xml/>")),
                dynamicTest("20", () -> testSelector("* {k1:k1v1}", "<xml/>", "<xml k1=\"k1v1\"/>")),
                dynamicTest("21", () -> testSelector("* {k1:k1v1;k2:k2v1}", "<xml/>", "<xml k1=\"k1v1\" k2=\"k2v1\"/>")),
                dynamicTest("22", () -> testSelector("* {k1:k1v1 k1v2; k2 : k2v1  k2v2}", "<xml/>", "<xml k1=\"k1v1 k1v2\" k2=\"k2v1  k2v2\"/>")),
                dynamicTest("23", () -> testSelector("* {k1:k1v1 k1v2; k2 : 'k2v1  k2v2'}", "<xml/>", "<xml k1=\"k1v1 k1v2\" k2=\"&quot;k2v1  k2v2&quot;\"/>")),
                dynamicTest("24", () -> testSelector("* {k1:k1v1 k1v2; k2 : 'k2v1 } k2v2'}", "<xml/>", "<xml k1=\"k1v1 k1v2\" k2=\"&quot;k2v1 } k2v2&quot;\"/>")),
                dynamicTest("25", () -> testSelector("a~b {x:y}", "<xml><b/><a/><b/><c/><b/></xml>", "<xml><b/><a/><b x=\"y\"/><c/><b x=\"y\"/></xml>")),
                dynamicTest("26", () -> testSelector("a+b {x:y}", "<xml><a/><b/><c/><b/></xml>", "<xml><a/><b x=\"y\"/><c/><b/></xml>")),
                dynamicTest("27", () -> testSelector("a>b>c {x:y}", "<xml><a><b><c/></b></a></xml>", "<xml><a><b><c x=\"y\"/></b></a></xml>")),
                dynamicTest("28", () -> testSelector("type>childType {a:1}", "<xml><childType/><type><childType/></type></xml>", "<xml><childType/><type><childType a=\"1\"/></type></xml>")),
                dynamicTest("29", () -> testSelector("type>childType {a:1}", "<xml><descendantType/><type><subtype><childType/></subtype></type></xml>", "<xml><descendantType/><type><subtype><childType/></subtype></type></xml>")),
                dynamicTest("30", () -> testSelector("type descendantType {a:1}", "<xml><descendantType/><type><descendantType/></type></xml>", "<xml><descendantType/><type><descendantType a=\"1\"/></type></xml>")),
                dynamicTest("31", () -> testSelector("type descendantType {a:1}", "<xml><descendantType/><type><subtype><descendantType/></subtype></type></xml>", "<xml><descendantType/><type><subtype><descendantType a=\"1\"/></subtype></type></xml>")),
                dynamicTest("32", () -> testSelector(" #id , .class {a:1}", "<xml><elem/><elem id=\"id\"/><elem class=\"class\"/></xml>", "<xml><elem/><elem a=\"1\" id=\"id\"/><elem a=\"1\" class=\"class\"/></xml>")),
                dynamicTest("33", () -> testSelector("#id , .class {a:1}", "<xml><elem/><elem id=\"id\"/><elem class=\"class\"/></xml>", "<xml><elem/><elem a=\"1\" id=\"id\"/><elem a=\"1\" class=\"class\"/></xml>")),
                dynamicTest("34", () -> testSelector("#id,.class {a:1}", "<xml><elem/><elem id=\"id\"/><elem class=\"class\"/></xml>", "<xml><elem/><elem a=\"1\" id=\"id\"/><elem a=\"1\" class=\"class\"/></xml>")),
                dynamicTest("35", () -> testSelector(".class1.class2:pseudoclass1:pseudoclass2#id {}", "<xml/>", "<xml/>")),
                dynamicTest("36", () -> testSelector("* {a:1}", "<xml/>", "<xml a=\"1\"/>")),
                dynamicTest("37", () -> testSelector("type#id.class {a:1}", "<xml><type/><type class=\"class\" id=\"id\"/></xml>", "<xml><type/><type a=\"1\" class=\"class\" id=\"id\"/></xml>")),
                dynamicTest("38", () -> testSelector("#id:pseudoclass {a:1}", "<xml/>", "<xml/>")),
                dynamicTest("39", () -> testSelector("#id.class {a:1}", "<xml><type/><type class=\"class\" id=\"id\"/></xml>", "<xml><type/><type a=\"1\" class=\"class\" id=\"id\"/></xml>")),
                dynamicTest("40", () -> testSelector("type:pseudoclass {a:1}", "<xml/>", "<xml/>")),
                dynamicTest("41", () -> testSelector("type#id {a:1}", "<xml><type/><type id=\"id\"/></xml>", "<xml><type/><type a=\"1\" id=\"id\"/></xml>")),
                dynamicTest("42", () -> testSelector(":pseudoclass {}", "<xml/>", "<xml/>")),
                dynamicTest("43", () -> testSelector(".class {a:1}", "<xml><elem class=\"class\"/></xml>", "<xml><elem a=\"1\" class=\"class\"/></xml>")),
                dynamicTest("44", () -> testSelector("#id {a:1}", "<xml><elem id=\"id\"/></xml>", "<xml><elem a=\"1\" id=\"id\"/></xml>")),
                dynamicTest("45", () -> testSelector("type {a:b}", "<xml><type/></xml>", "<xml><type a=\"b\"/></xml>")),
                dynamicTest("46", () -> testSelector("", "<xml/>", "<xml/>")),
                dynamicTest("47", () -> testSelector("TubeShape,\n" +
                        "TubeShape.One,\n" +
                        "TubeShape.Two {\n" +
                        "\tstroke: black;\n" +
                        "}", "<xml><TubeShape class=\"Two\"/></xml>", "<xml><TubeShape class=\"Two\" stroke=\"black\"/></xml>"))
        );

    }

    @TestFactory
    public @NonNull List<DynamicTest> testSelectorSpecificity() {
        return Arrays.asList(
                dynamicTest("46", () -> testSelectorSpecificity("TubeShape,\n" +
                        "TubeShape.One,\n" +
                        "TubeShape.Two {\n" +
                        "\tstroke: black;\n" +
                        "}", "<xml><TubeShape/></xml>", 1)),
                dynamicTest("47", () -> testSelectorSpecificity("TubeShape,\n" +
                        "TubeShape.One,\n" +
                        "TubeShape.Two {\n" +
                        "\tstroke: black;\n" +
                        "}", "<xml><TubeShape class=\"Two\"/></xml>", 11))
        );

    }

    /**
     * Test various selectors.
     */
    public static void testSelectorSpecificity(@NonNull String stylesheet, @NonNull String xml, int expectedSpecifity) throws IOException, ParserConfigurationException, SAXException {
        System.out.println(stylesheet);
        //---
        CssParser p = new CssParser();
        Stylesheet ast = p.parseStylesheet(stylesheet);


        //---
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        // We do not want that the reader creates a socket connection!
        builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        DocumentSelectorModel dsd = new DocumentSelectorModel();

        NodeList elements = doc.getElementsByTagName("*");
        Integer actualSpecificity = null;
        for (int i = 0, n = elements.getLength(); i < n; i++) {
            Element elem = (Element) elements.item(i);
            for (StyleRule r : ast.getStyleRules()) {
                SelectorGroup sg = r.getSelectorGroup();
                Selector matchedSelector = sg.matchSelector(dsd, elem);
                if (matchedSelector != null) {
                    System.out.println("  match " + sg.toString() + " " + elem);
                    System.out.println("specificity: " + matchedSelector.getSpecificity());
                    actualSpecificity = matchedSelector.getSpecificity();
                } else {
                    System.out.println(" !match " + sg.toString() + " " + elem);
                }
            }
        }
        assertEquals(expectedSpecifity, actualSpecificity);
    }


}
