/* @(#)CssParserTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.SelectorGroup;
import org.jhotdraw8.css.ast.Stylesheet;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author wr
 */
public class CssParserTest {

    private final static String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    /**
     * Test of parseStylesheet method, of class CssParser.
     */
    public static void testParseStylesheet(String stylesheet, String before, String expectedValue) throws Exception {
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
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
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
                        elem.setAttribute(d.getProperty(), d.getTermsAsString());
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
        assertEquals(actualValue, expectedValue);
    }

    @TestFactory
    public List<DynamicTest> stylesheetData() {
        return Arrays.asList(
                dynamicTest("1", () -> testParseStylesheet("#vertex4 {\n"
                        + "    -jhotdraw-fill: yellow;\n"
                        + "}\n"
                        + "\n"
                        + "[class~=\"warning\"] {\n"
                        + "  color: red;\n"
                        + "}", "<xml><a/><b class=\"warning\"/></xml>", "<xml><a/><b class=\"warning\" color=\"red\"/></xml>")),
                dynamicTest("1", () -> testParseStylesheet("[att] {a:1}", "<xml><elem att=\"bla\"/></xml>", "<xml><elem a=\"1\" att=\"bla\"/></xml>")),
                dynamicTest("2", () -> testParseStylesheet("[att=val] {a:1}", "<xml><elem att=\"bla\"/><elem att=\"val\"/></xml>", "<xml><elem att=\"bla\"/><elem a=\"1\" att=\"val\"/></xml>")),
                dynamicTest("3", () -> testParseStylesheet("[att~=val] {a:1}", "<xml><elem att=\"bla blu\"/><elem att=\"val kilmer\"/></xml>", "<xml><elem att=\"bla blu\"/><elem a=\"1\" att=\"val kilmer\"/></xml>")),
                dynamicTest("4", () -> testParseStylesheet("[att~=val] {a:1}", "<xml><elem att=\"bla val blu\"/><elem att=\"kilmer val\"/></xml>", "<xml><elem a=\"1\" att=\"bla val blu\"/><elem a=\"1\" att=\"kilmer val\"/></xml>")),
                dynamicTest("5", () -> testParseStylesheet("[att|=val] {a:1}", "<xml><elem att=\"val\"/><elem att=\"val-kilmer\"/></xml>", "<xml><elem a=\"1\" att=\"val\"/><elem a=\"1\" att=\"val-kilmer\"/></xml>")),
                dynamicTest("6", () -> testParseStylesheet("[att|=val] {a:1}", "<xml><elem att=\"valkon\"/><elem att=\"kilmer-val\"/></xml>", "<xml><elem att=\"valkon\"/><elem att=\"kilmer-val\"/></xml>")),
                dynamicTest("7", () -> testParseStylesheet("text {text:\"bla\"}", "<xml/>", "<xml/>")),
                dynamicTest("8", () -> testParseStylesheet("text {font:Lorem  Ipsum}", "<xml/>", "<xml/>")),
                dynamicTest("9", () -> testParseStylesheet("text {font:\"Lorem  Ipsum\"}", "<xml/>", "<xml/>")),
                dynamicTest("10", () -> testParseStylesheet("box {image:http://imageserver.com/myimage.png blue green}", "<xml/>", "<xml/>")),
                dynamicTest("11", () -> testParseStylesheet("box {image:http://imageserver.com/myimage.png}", "<xml/>", "<xml/>")),
                dynamicTest("12", () -> testParseStylesheet("box {fill:rgb(12,15,16)}", "<xml/>", "<xml/>")),
                dynamicTest("13", () -> testParseStylesheet("box {image:http://pics.com/myimage.png blue green}", "<xml><box/></xml>", "<xml><box image=\"http://pics.com/myimage.png blue green\"/></xml>")),
                dynamicTest("14", () -> testParseStylesheet("box {image:http://pics.com/myimage.png}", "<xml><box/></xml>", "<xml><box image=\"http://pics.com/myimage.png\"/></xml>")),
                dynamicTest("15", () -> testParseStylesheet("box {fill:rgb(12,15,16)}", "<xml><box/></xml>", "<xml><box fill=\"rgb(12,15,16)\"/></xml>")),
                dynamicTest("16", () -> testParseStylesheet("box {image:url('http://pics.com/myimage.png') blue green}", "<xml><box/></xml>", "<xml><box image=\"url(http://pics.com/myimage.png) blue green\"/></xml>")),
                dynamicTest("17", () -> testParseStylesheet("box {image:url(http://pics.com/myimage.png)}", "<xml><box/></xml>", "<xml><box image=\"url(http://pics.com/myimage.png)\"/></xml>")),
                dynamicTest("18", () -> testParseStylesheet("box {image:url(http://bad com/myimage.png)}", "<xml><box/></xml>", "<xml><box/></xml>")),
                dynamicTest("19", () -> testParseStylesheet("* {;;;}", "<xml/>", "<xml/>")),
                dynamicTest("20", () -> testParseStylesheet("* {k1:k1v1}", "<xml/>", "<xml k1=\"k1v1\"/>")),
                dynamicTest("21", () -> testParseStylesheet("* {k1:k1v1;k2:k2v1}", "<xml/>", "<xml k1=\"k1v1\" k2=\"k2v1\"/>")),
                dynamicTest("22", () -> testParseStylesheet("* {k1:k1v1 k1v2; k2 : k2v1  k2v2}", "<xml/>", "<xml k1=\"k1v1 k1v2\" k2=\"k2v1 k2v2\"/>")),
                dynamicTest("23", () -> testParseStylesheet("* {k1:k1v1 k1v2; k2 : 'k2v1  k2v2'}", "<xml/>", "<xml k1=\"k1v1 k1v2\" k2=\"'k2v1  k2v2'\"/>")),
                dynamicTest("24", () -> testParseStylesheet("* {k1:k1v1 k1v2; k2 : 'k2v1 } k2v2'}", "<xml/>", "<xml k1=\"k1v1 k1v2\" k2=\"'k2v1 } k2v2'\"/>")),
                dynamicTest("25", () -> testParseStylesheet("a~b {x:y}", "<xml><b/><a/><b/><c/><b/></xml>", "<xml><b/><a/><b x=\"y\"/><c/><b x=\"y\"/></xml>")),
                dynamicTest("26", () -> testParseStylesheet("a+b {x:y}", "<xml><a/><b/><c/><b/></xml>", "<xml><a/><b x=\"y\"/><c/><b/></xml>")),
                dynamicTest("27", () -> testParseStylesheet("a>b>c {x:y}", "<xml><a><b><c/></b></a></xml>", "<xml><a><b><c x=\"y\"/></b></a></xml>")),
                dynamicTest("28", () -> testParseStylesheet("type>childType {a:1}", "<xml><childType/><type><childType/></type></xml>", "<xml><childType/><type><childType a=\"1\"/></type></xml>")),
                dynamicTest("29", () -> testParseStylesheet("type>childType {a:1}", "<xml><descendantType/><type><subtype><childType/></subtype></type></xml>", "<xml><descendantType/><type><subtype><childType/></subtype></type></xml>")),
                dynamicTest("30", () -> testParseStylesheet("type descendantType {a:1}", "<xml><descendantType/><type><descendantType/></type></xml>", "<xml><descendantType/><type><descendantType a=\"1\"/></type></xml>")),
                dynamicTest("31", () -> testParseStylesheet("type descendantType {a:1}", "<xml><descendantType/><type><subtype><descendantType/></subtype></type></xml>", "<xml><descendantType/><type><subtype><descendantType a=\"1\"/></subtype></type></xml>")),
                dynamicTest("32", () -> testParseStylesheet(" #id , .class {a:1}", "<xml><elem/><elem id=\"id\"/><elem class=\"class\"/></xml>", "<xml><elem/><elem a=\"1\" id=\"id\"/><elem a=\"1\" class=\"class\"/></xml>")),
                dynamicTest("33", () -> testParseStylesheet("#id , .class {a:1}", "<xml><elem/><elem id=\"id\"/><elem class=\"class\"/></xml>", "<xml><elem/><elem a=\"1\" id=\"id\"/><elem a=\"1\" class=\"class\"/></xml>")),
                dynamicTest("34", () -> testParseStylesheet("#id,.class {a:1}", "<xml><elem/><elem id=\"id\"/><elem class=\"class\"/></xml>", "<xml><elem/><elem a=\"1\" id=\"id\"/><elem a=\"1\" class=\"class\"/></xml>")),
                dynamicTest("35", () -> testParseStylesheet(".class1.class2:pseudoclass1:pseudoclass2#id {}", "<xml/>", "<xml/>")),
                dynamicTest("36", () -> testParseStylesheet("* {a:1}", "<xml/>", "<xml a=\"1\"/>")),
                dynamicTest("37", () -> testParseStylesheet("type#id.class {a:1}", "<xml><type/><type class=\"class\" id=\"id\"/></xml>", "<xml><type/><type a=\"1\" class=\"class\" id=\"id\"/></xml>")),
                dynamicTest("38", () -> testParseStylesheet("#id:pseudoclass {a:1}", "<xml/>", "<xml/>")),
                dynamicTest("39", () -> testParseStylesheet("#id.class {a:1}", "<xml><type/><type class=\"class\" id=\"id\"/></xml>", "<xml><type/><type a=\"1\" class=\"class\" id=\"id\"/></xml>")),
                dynamicTest("40", () -> testParseStylesheet("type:pseudoclass {a:1}", "<xml/>", "<xml/>")),
                dynamicTest("41", () -> testParseStylesheet("type#id {a:1}", "<xml><type/><type id=\"id\"/></xml>", "<xml><type/><type a=\"1\" id=\"id\"/></xml>")),
                dynamicTest("42", () -> testParseStylesheet(":pseudoclass {}", "<xml/>", "<xml/>")),
                dynamicTest("43", () -> testParseStylesheet(".class {a:1}", "<xml><elem class=\"class\"/></xml>", "<xml><elem a=\"1\" class=\"class\"/></xml>")),
                dynamicTest("44", () -> testParseStylesheet("#id {a:1}", "<xml><elem id=\"id\"/></xml>", "<xml><elem a=\"1\" id=\"id\"/></xml>")),
                dynamicTest("45", () -> testParseStylesheet("type {a:b}", "<xml><type/></xml>", "<xml><type a=\"b\"/></xml>")),
                dynamicTest("46", () -> testParseStylesheet("", "<xml/>", "<xml/>"))//
        );

    }

    /**
     * Test of parseStylesheet method, of class CssParser.
     */
    public static void testCssSyntax(boolean valid, String stylesheet, String before, String expectedValue) throws Exception {
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
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
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
                        elem.setAttribute(d.getProperty(), d.getTermsAsString());
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
        assertEquals(actualValue, expectedValue);
        assertEquals(p.getParseExceptions().isEmpty(), valid);
    }

    /**
     * Examples from
     * <a href="http://www.w3.org/TR/2014/CR-css-syntax-3-20140220/">
     * CSS Syntax 3</a>.
     *
     * @return examples
     */
    @TestFactory
    public List<DynamicTest> testCssSyntaxFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testCssSyntax(true, ":nth-child(3n + 1) {}", //
                        "<xml/>",//
                        "<xml/>")), //
                // example 1
                dynamicTest("2", () -> testCssSyntax(true, "p > a {\n  color: blue;\n  text-decoration: underline;\n}", //
                        "<xml><p/><a/><p><a/></p><a><p/></a></xml>", //
                        "<xml><p/><a/><p><a color=\"blue\" text-decoration=\"underline\"/></p><a><p/></a></xml>")),//
                // example 2
                dynamicTest("3", () -> testCssSyntax(true, "@import \"my-styles.css\";", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("4", () -> testCssSyntax(true, "@page :left {\n  margin-left: 4cm;\n  margin-right: 3cm;\n}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("5", () -> testCssSyntax(true, "@media print {\n  body { font-size: 10pt }\n}", //
                        "<xml/>",//
                        "<xml/>")),
                // example 3 (Changed \26 to \41 so that we can match an element)
                dynamicTest("6", () -> testCssSyntax(true, "\\41 B {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),
                dynamicTest("7", () -> testCssSyntax(true, "\\000041B {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),
                // example 4
                dynamicTest("8", () -> testCssSyntax(true, ":nth-child(2n+0) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("9", () -> testCssSyntax(true, ":nth-child(even) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("10", () -> testCssSyntax(true, ":nth-child(4n+1) {}", //
                        "<xml/>",//
                        "<xml/>")),
                // example 5
                dynamicTest("11", () -> testCssSyntax(true, ":nth-child(-5n+6) {}", //
                        "<xml/>",//
                        "<xml/>")),
                // example 6
                dynamicTest("12", () -> testCssSyntax(true, ":nth-child(0n+5) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("13", () -> testCssSyntax(true, ":nth-child(5) {}", //
                        "<xml/>",//
                        "<xml/>")),
                // example 7
                dynamicTest("14", () -> testCssSyntax(true, ":nth-child(1n+0) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("15", () -> testCssSyntax(true, ":nth-child(n+0) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("16", () -> testCssSyntax(true, ":nth-child(n) {}", //
                        "<xml/>",//
                        "<xml/>")),
                // example 8
                dynamicTest("17", () -> testCssSyntax(true, ":nth-child(2n+0) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("18", () -> testCssSyntax(true, ":nth-child(2n) {}", //
                        "<xml/>",//
                        "<xml/>")),
                // example 9
                dynamicTest("19", () -> testCssSyntax(true, ":nth-child(+3n - 2) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("20", () -> testCssSyntax(true, ":nth-child(-n+ 6) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("21", () -> testCssSyntax(true, ":nth-child(+6) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("22", () -> testCssSyntax(true, ":nth-child(3 n) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("23", () -> testCssSyntax(true, ":nth-child(+ 2n) {}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("24", () -> testCssSyntax(true, ":nth-child(+ 2) {}", //
                        "<xml/>",//
                        "<xml/>")),
                // example 10
                dynamicTest("25", () -> testCssSyntax(false, ".foo { transform: translate(50px", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("26", () -> testCssSyntax(true, "", //
                        "<xml/>",//
                        "<xml/>"))//
        )/**/;
    }
}
