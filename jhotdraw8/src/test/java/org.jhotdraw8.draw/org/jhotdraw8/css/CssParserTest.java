/* @(#)CssParserTest.java
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.Rule;
import org.jhotdraw8.css.ast.SelectorGroup;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
public class CssParserTest {

    private static final String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    /**
     * Test of CSS syntax.
     * <p>
     * Takes a stylesheet and applies it to the given XML document.
     */
    public static void testCssSyntax(boolean valid, @NonNull String stylesheet, @NonNull String xml, String expectedValue) throws Exception {
        System.out.println(stylesheet);
        //---
        CssParser p = new CssParser();
        Stylesheet ast = p.parseStylesheet(stylesheet, null);
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
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
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
        assertEquals(valid, p.getParseExceptions().isEmpty());
    }

    /**
     * Examples from
     * <a href="http://www.w3.org/TR/2014/CR-css-syntax-3-20140220/">
     * CSS Syntax 3</a>.
     *
     * @return examples
     */
    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsCssSyntax() {
        return Arrays.asList(
                dynamicTest("1", () -> testCssSyntax(true, ":nth-child(3n + 1) {}", //
                        "<xml/>",//
                        "<xml/>")), //
                // example 1
                dynamicTest("2", () -> testCssSyntax(true, "p > a {\n  color: blue;\n  text-decoration: underline;\n}", //
                        "<xml><p/><a/><p><a/></p><a><p/></a></xml>", //
                        "<xml><p/><a/><p><a color=\"blue\" text-decoration=\"underline\"/></p><a><p/></a></xml>")),//
                // example 2
                dynamicTest("3", () -> testCssSyntax(false, "@import \"my-styles.css\";", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("4", () -> testCssSyntax(false, "@page :left {\n  margin-left: 4cm;\n  margin-right: 3cm;\n}", //
                        "<xml/>",//
                        "<xml/>")),
                dynamicTest("5", () -> testCssSyntax(false, "@media print {\n  body { font-size: 10pt }\n}", //
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
                        "<xml/>")),//
                // with comments
                dynamicTest("31", () -> testCssSyntax(true, "/*comment*/AB {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),

                // selector missing after comma
                dynamicTest("32", () -> testCssSyntax(false, "AB, {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),

                dynamicTest("33", () -> testCssSyntax(false, "AB,, {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),

                // attribute value has curly braces
                dynamicTest("41", () -> testCssSyntax(false, "AB {x:{y}}}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"{y}\"/></xml>")),
                dynamicTest("42", () -> testCssSyntax(false, "AB {x:{class:[Object]};y:4}}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"{class:[Object]}\" y=\"4\"/></xml>"))

        )/**/;
    }


    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsAtRule() {
        return Arrays.asList(
                // at rules
                dynamicTest("1", () -> testAtRule(false, "@charset \"UTF-8\"; AB {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),
                dynamicTest("2", () -> testAtRule(false, "@import url('landscape.css') screen and (orientation:landscape); AB {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),
                dynamicTest("3", () -> testAtRule(true, "@namespace url(http://www.w3.org/1999/xhtml); AB {x:y}", //
                        "<xml xmlns=\"http://www.w3.org/1999/xhtml\"><AB/></xml>",//
                        "<xml xmlns=\"http://www.w3.org/1999/xhtml\"><AB x=\"y\"/></xml>")),
                dynamicTest("3 with any NS", () -> testAtRule(true, "@namespace url(http://www.w3.org/1999/xhtml); *|AB {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),
                dynamicTest("4", () -> testAtRule(true, "@namespace svg url(http://www.w3.org/2000/svg); AB {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),
                dynamicTest("5", () -> testAtRule(false, "@media print {\n" +
                                "  body { font-size: 10pt }\n" +
                                "} AB {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>")),
                dynamicTest("6", () -> testAtRule(false, "@document url(http://www.w3.org/),\n" +
                                "               url-prefix(http://www.w3.org/Style/),\n" +
                                "               domain(mozilla.org),\n" +
                                "               regexp(\"https:.*\")\n" +
                                "{\n" +
                                "  body {\n" +
                                "    color: purple;\n" +
                                "    background: yellow;\n" +
                                "  }\n" +
                                "} AB {x:y}", //
                        "<xml><AB/></xml>",//
                        "<xml><AB x=\"y\"/></xml>"))
        )/**/;
    }

    /**
     * Tests parsing of at rules.
     */
    public static void testAtRule(boolean valid, @NonNull String stylesheetStr, @NonNull String before, String expectedValue) throws Exception {
        testCssSyntax(valid, stylesheetStr, before, expectedValue);

        CssParser p = new CssParser();
        Stylesheet stylesheet = p.parseStylesheet(stylesheetStr, null);
        ReadOnlyList<Rule> rules = stylesheet.getRules();
        System.out.println(rules);
    }

    /**
     * Test of CSS selector syntax.
     * <p>
     * Takes a stylesheet and applies it to the given XML document.
     */
    public static void testSelectorSyntax(boolean valid, @NonNull String stylesheet, @NonNull String xml, String expectedValue) throws Exception {
        System.out.println(stylesheet);
        //---
        CssParser p = new CssParser();
        Stylesheet ast = p.parseStylesheet(stylesheet, null);
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
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
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
        assertEquals(valid, p.getParseExceptions().isEmpty());
    }


    /**
     * Tests selectors.
     */
    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsSelectorSyntax() {
        return Arrays.asList(
                dynamicTest("type selector", () -> testSelectorSyntax(true, "a {x:1;}", //
                        "<xml><a/><b/></xml>",//
                        "<xml><a x=\"1\"/><b/></xml>")), //
                dynamicTest("empty not selector", () -> testSelectorSyntax(false, ":not() {x:1;}", //
                        "<xml><a/><b/></xml>",//
                        "<xml><a/><b/></xml>")), //
                dynamicTest("not selector 1", () -> testSelectorSyntax(true, ":not(a) {x:1;}", //
                        "<xml><a/><b/></xml>",//
                        "<xml x=\"1\"><a/><b x=\"1\"/></xml>")), //
                dynamicTest("not selector 2", () -> testSelectorSyntax(false, ":not(xml,a) {x:1;}", //
                        "<xml><a/><b/></xml>",//
                        "<xml><a/><b/></xml>")), //
                dynamicTest("nested not selector", () -> testSelectorSyntax(true, ":not(:not(a)) {x:1;}", //
                        "<xml><a/><b/></xml>",//
                        "<xml><a x=\"1\"/><b/></xml>")), //
                dynamicTest("universal selector", () -> testSelectorSyntax(true, "* {x:1;}", //
                        "<xml><a/><b/></xml>",//
                        "<xml x=\"1\"><a x=\"1\"/><b x=\"1\"/></xml>")) //
        );
    }

    /**
     * Test of selectors.
     */
    public static void testSelector(boolean valid, @NonNull String stylesheet, @NonNull String before, String expectedValue) throws Exception {
        testSelectorSyntax(valid, stylesheet, before, expectedValue);
    }

    /**
     * Test selectors with name space.
     */
    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsSelectorSyntaxNS() {
        return Arrays.asList(
                dynamicTest("type selector not ns aware", () -> testSelectorSyntaxNS(true, "a {x:1;}", //
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a/><b/></xml>",//
                        "<xml xmlns:n1=\"http://n1.com\"><a x=\"1\"/><n1:a x=\"1\"/><b/></xml>")),
                dynamicTest("type selector in all ns", () -> testSelectorSyntaxNS(true, "*|a {x:1;}", //
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a/><b/></xml>",//
                        "<xml xmlns:n1=\"http://n1.com\"><a x=\"1\"/><n1:a x=\"1\"/><b/></xml>")),
                dynamicTest("type selector with ns prefix as string", () -> testSelectorSyntaxNS(true, "@namespace ns \"http://n1.com\"; ns|a {x:1;}", //
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a/><b/></xml>",//
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a x=\"1\"/><b/></xml>")),
                dynamicTest("type selector with ns prefix as url", () -> testSelectorSyntaxNS(true, "@namespace ns url(http://n1.com); ns|a {x:1;}", //
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a/><b/></xml>",//
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a x=\"1\"/><b/></xml>")),
                dynamicTest("type selector with default ns as string", () -> testSelectorSyntaxNS(true, "@namespace \"http://n1.com\"; a {x:1;}", //
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a/><b/></xml>",//
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a x=\"1\"/><b/></xml>")),
                dynamicTest("type selector with default ns as url", () -> testSelectorSyntaxNS(true, "@namespace url(http://n1.com); a {x:1;}", //
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a/><b/></xml>",//
                        "<xml xmlns:n1=\"http://n1.com\"><a/><n1:a x=\"1\"/><b/></xml>"))
        );
    }


    /**
     * Test of selectors with namespace.
     */
    public static void testSelectorSyntaxNS(boolean valid, @NonNull String stylesheet, @NonNull String before, String expectedValue) throws Exception {
        testSelectorSyntax(valid, stylesheet, before, expectedValue);
    }
}
