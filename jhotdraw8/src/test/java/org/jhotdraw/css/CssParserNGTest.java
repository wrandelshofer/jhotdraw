/* @(#)CssParserNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import org.jhotdraw.css.DocumentSelectorModel;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw.css.ast.Declaration;
import org.jhotdraw.css.ast.Ruleset;
import org.jhotdraw.css.ast.SelectorGroup;
import org.jhotdraw.css.ast.Stylesheet;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author wr
 */
public class CssParserNGTest {

    private final static String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    public CssParserNGTest() {
    }

    /**
     * Test of parseStylesheet method, of class CssParser.
     */
    @Test(dataProvider = "stylesheetData")
    public void testParseStylesheet(String stylesheet, String before, String expectedValue) throws Exception {
        System.out.println(stylesheet);
        //---
        CssParser p = new CssParser();
        Stylesheet ast = p.parseStylesheet(stylesheet);
        // 
        System.out.println("AST: "+ast);
        if (!p.getParseExceptions().isEmpty()){
        System.out.println("Errors: ");
        for (ParseException e:p.getParseExceptions()) {
            System.out.println("\033[31m "+e.getMessage()+" @ "+e.getErrorOffset()+"\033[0m");
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
            for (Ruleset r : ast.getRulesets()) {
                SelectorGroup sg = r.getSelectorGroup();
                if (sg.matches(dsd, elem)) {
                    System.out.println("  match "+sg.toString()+" "+elem);
                    for (Declaration d : r.getDeclarations()) {
                        elem.setAttribute(d.getProperty(), d.getTermsAsString());
                    }
                }else{
                    System.out.println(" !match "+sg.toString()+" "+elem);
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
        System.out.println(" actual  : \033[31m"+actualValue+"\033[0m");
        }else{
        System.out.println(" actual  : "+actualValue);
        }
        System.out.println(" expected: "+expectedValue);
        //---
        assertEquals(actualValue, expectedValue);
    }

    @DataProvider
    public Object[][] stylesheetData() {
        return new Object[][]{
            {"[att] {a:1}", "<xml><elem att=\"bla\"/></xml>", "<xml><elem a=\"1\" att=\"bla\"/></xml>"},
            {"[att=val] {a:1}", "<xml><elem att=\"bla\"/><elem att=\"val\"/></xml>", "<xml><elem att=\"bla\"/><elem a=\"1\" att=\"val\"/></xml>"},
            {"[att~=val] {a:1}", "<xml><elem att=\"bla blu\"/><elem att=\"val kilmer\"/></xml>", "<xml><elem att=\"bla blu\"/><elem a=\"1\" att=\"val kilmer\"/></xml>"},
            {"[att~=val] {a:1}", "<xml><elem att=\"bla val blu\"/><elem att=\"kilmer val\"/></xml>", "<xml><elem a=\"1\" att=\"bla val blu\"/><elem a=\"1\" att=\"kilmer val\"/></xml>"},
            {"[att|=val] {a:1}", "<xml><elem att=\"val\"/><elem att=\"val-kilmer\"/></xml>", "<xml><elem a=\"1\" att=\"val\"/><elem a=\"1\" att=\"val-kilmer\"/></xml>"},
            {"[att|=val] {a:1}", "<xml><elem att=\"valkon\"/><elem att=\"kilmer-val\"/></xml>", "<xml><elem att=\"valkon\"/><elem att=\"kilmer-val\"/></xml>"},
            {"text {text:\"bla\"}", "<xml/>", "<xml/>"},
            {"text {font:Lorem  Ipsum}", "<xml/>", "<xml/>"},
            {"text {font:\"Lorem  Ipsum\"}", "<xml/>", "<xml/>"},
            {"box {image:http://imageserver.com/myimage.png blue green}", "<xml/>", "<xml/>"},
            {"box {image:http://imageserver.com/myimage.png}", "<xml/>", "<xml/>"},
            {"box {fill:rgb(12,15,16)}", "<xml/>", "<xml/>"},
            {"box {image:http://pics.com/myimage.png blue green}", "<xml><box/></xml>", "<xml><box image=\"http://pics.com/myimage.png blue green\"/></xml>"},
            {"box {image:http://pics.com/myimage.png}", "<xml><box/></xml>", "<xml><box image=\"http://pics.com/myimage.png\"/></xml>"},
            {"box {fill:rgb(12,15,16)}", "<xml><box/></xml>", "<xml><box fill=\"rgb(12,15,16)\"/></xml>"},
            {"box {image:url('http://pics.com/myimage.png') blue green}", "<xml><box/></xml>", "<xml><box image=\"http://pics.com/myimage.png blue green\"/></xml>"},
            {"box {image:url(http://pics.com/myimage.png)}", "<xml><box/></xml>", "<xml><box image=\"http://pics.com/myimage.png\"/></xml>"},
            {"box {image:url(http://bad com/myimage.png)}", "<xml><box/></xml>", "<xml><box image=\"http://bad com/myimage.png\"/></xml>"},
            {"* {;;;}", "<xml/>", "<xml/>"},
            {"* {k1:k1v1}", "<xml/>", "<xml k1=\"k1v1\"/>"},
            {"* {k1:k1v1;k2:k2v1}", "<xml/>", "<xml k1=\"k1v1\" k2=\"k2v1\"/>"},
            {"* {k1:k1v1 k1v2; k2 : k2v1  k2v2}", "<xml/>", "<xml k1=\"k1v1 k1v2\" k2=\"k2v1 k2v2\"/>"},
            {"* {k1:k1v1 k1v2; k2 : 'k2v1  k2v2'}", "<xml/>", "<xml k1=\"k1v1 k1v2\" k2=\"'k2v1  k2v2'\"/>"},
            {"* {k1:k1v1 k1v2; k2 : 'k2v1 } k2v2'}", "<xml/>", "<xml k1=\"k1v1 k1v2\" k2=\"'k2v1 } k2v2'\"/>"},
            {"a~b {x:y}", "<xml><b/><a/><b/><c/><b/></xml>", "<xml><b/><a/><b x=\"y\"/><c/><b x=\"y\"/></xml>"},
            {"a+b {x:y}", "<xml><a/><b/><c/><b/></xml>", "<xml><a/><b x=\"y\"/><c/><b/></xml>"},
            {"a>b>c {x:y}", "<xml><a><b><c/></b></a></xml>", "<xml><a><b><c x=\"y\"/></b></a></xml>"},
            {"type>childType {a:1}", "<xml><childType/><type><childType/></type></xml>", "<xml><childType/><type><childType a=\"1\"/></type></xml>"},
            {"type>childType {a:1}", "<xml><descendantType/><type><subtype><childType/></subtype></type></xml>", "<xml><descendantType/><type><subtype><childType/></subtype></type></xml>"},
            {"type descendantType {a:1}", "<xml><descendantType/><type><descendantType/></type></xml>", "<xml><descendantType/><type><descendantType a=\"1\"/></type></xml>"},
            {"type descendantType {a:1}", "<xml><descendantType/><type><subtype><descendantType/></subtype></type></xml>", "<xml><descendantType/><type><subtype><descendantType a=\"1\"/></subtype></type></xml>"},
            {" #id , .class {a:1}", "<xml><elem/><elem id=\"id\"/><elem class=\"class\"/></xml>",  "<xml><elem/><elem a=\"1\" id=\"id\"/><elem a=\"1\" class=\"class\"/></xml>"},
            {"#id , .class {a:1}", "<xml><elem/><elem id=\"id\"/><elem class=\"class\"/></xml>",  "<xml><elem/><elem a=\"1\" id=\"id\"/><elem a=\"1\" class=\"class\"/></xml>"},
            {"#id,.class {a:1}", "<xml><elem/><elem id=\"id\"/><elem class=\"class\"/></xml>",  "<xml><elem/><elem a=\"1\" id=\"id\"/><elem a=\"1\" class=\"class\"/></xml>"},
            {".class1.class2:pseudoclass1:pseudoclass2#id {}", "<xml/>", "<xml/>"},
            {"* {a:1}", "<xml/>", "<xml a=\"1\"/>"},
            {"type#id.class {a:1}", "<xml><type/><type class=\"class\" id=\"id\"/></xml>", "<xml><type/><type a=\"1\" class=\"class\" id=\"id\"/></xml>"},
            {"#id:pseudoclass {a:1}", "<xml/>", "<xml/>"},
            {"#id.class {a:1}", "<xml><type/><type class=\"class\" id=\"id\"/></xml>", "<xml><type/><type a=\"1\" class=\"class\" id=\"id\"/></xml>"},
            {"type:pseudoclass {a:1}", "<xml/>", "<xml/>"},
            {"type#id {a:1}", "<xml><type/><type id=\"id\"/></xml>", "<xml><type/><type a=\"1\" id=\"id\"/></xml>"},
            {":pseudoclass {}", "<xml/>", "<xml/>"},
            {".class {a:1}", "<xml><elem class=\"class\"/></xml>", "<xml><elem a=\"1\" class=\"class\"/></xml>"},
            {"#id {a:1}", "<xml><elem id=\"id\"/></xml>", "<xml><elem a=\"1\" id=\"id\"/></xml>"},
            {"type {a:b}", "<xml><type/></xml>", "<xml><type a=\"b\"/></xml>"},
            {"", "<xml/>", "<xml/>"},};

    }
/*
    public static void main(String... args) throws Exception {
        CssParserNGTest test = new CssParserNGTest();
        Object[][] data = test.stylesheetData();
        for (Object[] row : data) {
            String stylesheet = (String) row[0];
            String before = (String) row[1];
            String expectedValue = (String) row[2];
            test.testParseStylesheet(stylesheet, before, expectedValue);
        }

    }*/

}
