/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.text;

import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_CUBICTO;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;
import static java.awt.geom.PathIterator.SEG_QUADTO;
import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Locale;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author werni
 */
public class PatternConverterNGTest {

    public PatternConverterNGTest() {
    }

    /**
     * Test of toString method, of class PatternConverter.
     */
    @Test(dataProvider = "toStringData")
    public void testToString(String pattern, Object[] value, String expectedOutput) throws IOException {
        PatternConverter c = new PatternConverter(pattern, new MessageFormatConverterFactory());
        String actualOutput=c.toString(value);
        assertEquals(actualOutput, expectedOutput);
    }
    /**
     * Test of fromString method, of class PatternConverter.
     */
    @Test(dataProvider = "fromStringData")
    public void testFromString(String pattern, Object[] expectedValue, String input) throws IOException, ParseException {
        PatternConverter c = new PatternConverter(pattern, new MessageFormatConverterFactory());
        PatternConverter.AST ast = PatternConverter.parseTextFormatPattern(pattern);
        System.out.println("ast:"+ast);
        Object[] actualValue=c.fromString(input);
        assertEquals(actualValue, expectedValue);
    }

    @DataProvider
    public Object[][] toStringData() {
        return new Object[][]{
            {"{0,list,{1}}", new Object[]{0}, ""},
            {"{0,list,{1}}", new Object[]{1,"i0"}, "i0"},
            {"{0,list,{1}}", new Object[]{2,"i0","i1"}, "i0i1"},
            {"{0,list,{1}|,}", new Object[]{2,"i0","i1"}, "i0,i1"},
            {"{0,list,{1}|,}{2}", new Object[]{2,"i0","i1","x"}, "i0,i1x"},
            
            {"hello world", new Object[]{}, "hello world"},
            {"'hello world'", new Object[]{}, "hello world"},
            {"he+llo", new Object[]{}, "hello"},
            {"he*llo", new Object[]{}, "hllo"},
            {"h(e|a)llo", new Object[]{}, "hello"},
            {"h[ea]llo", new Object[]{}, "hello"},
            {"hello {0}", new Object[]{"world"}, "hello world"},
            {"left brace '{'", new Object[]{}, "left brace {"},
            {"right brace '}'", new Object[]{}, "right brace }"},
            {"quote ''", new Object[]{}, "quote '"},
            {"{0} world", new Object[]{"hello"}, "hello world"},
            {"{0} {1}", new Object[]{"hello","world"}, "hello world"},
            
            {"{0,choice,0#hello}", new Object[]{0}, "hello"},
            {"{0,choice,0#hello world|1#good morning}", new Object[]{0}, "hello world"},
            {"{0,choice,0#hello world|1#good morning}minusone", new Object[]{-1}, "hello worldminusone"},
            {"{0,choice,0#hello world|1#good morning}zero", new Object[]{0}, "hello worldzero"},
            {"{0,choice,0#hello world|1#good morning}one", new Object[]{1}, "good morningone"},
            {"{0,choice,0#hello world|1#good morning}two", new Object[]{2}, "good morningtwo"},
            
        };
    }
    @DataProvider
    public Object[][] fromStringData() {
        return new Object[][]{
            {"hello world", new Object[]{}, "hello world"},
            {"'hello world'", new Object[]{}, "hello world"},
            {"he+llo", new Object[]{}, "hello"},
            {"he*llo", new Object[]{}, "hllo"},
            {"h(e|a)llo", new Object[]{}, "hello"},
            {"h[ea]llo", new Object[]{}, "hello"},
            {"hello {0}", new Object[]{"world"}, "hello world"},
            {"left brace '{'", new Object[]{}, "left brace {"},
            {"right brace '}'", new Object[]{}, "right brace }"},
            {"quote ''", new Object[]{}, "quote '"},
            /*{"{0} world", new Object[]{"hello"}, "hello world"},
            {"{0} {1}", new Object[]{"hello","world"}, "hello world"},
            {"{1} {0}", new Object[]{"world","hello"}, "hello world"},*/
        };

    }
    
  /**
     * Tests the choice pattern.
     * /
    @Test(dataProvider = "svgpathData")
    public void testChoicePattern(Object[] svgpath, String expectedString) {
        {
            String pattern = "{0,choice," + SEG_MOVETO
                    + "#m {1} {2}|" + SEG_LINETO + "#l|"
                    + SEG_QUADTO + "#q|" + SEG_CUBICTO + "#c|" + SEG_CLOSE
                    + "#z}";

            
            String svgpathPattern = "{0,list,{1,choice,0#m{2}{3}|1#l{2},{3}|2#q{2},{3} {4},{5}|3#q{2},{3} {4},{5} {6}{7}|4#z}}";
            
            
            MessageFormat f = new MessageFormat(pattern, Locale.ENGLISH);
            String actualString = f.format(svgpath);

            System.out.println(actualString);

            ParsePosition pp = new ParsePosition(0);
            Object[] actualSvgpath = f.parse(actualString, pp);
            System.out.println("pp=" + pp);
            if (actualSvgpath != null) {
                System.out.println("actualPath:"+Arrays.asList(actualSvgpath));
            }
        }
        {
            MessageFormat mf = new MessageFormat("{0}, {1}, {2}");
            String forParsing = "x, y, z";
            ParsePosition pp = new ParsePosition(0);
            Object[] objs = mf.parse(forParsing, pp);
            // result now equals {new String("z")}
            System.out.println("pp=" + pp);
            if (objs != null) {
                System.out.println(Arrays.asList(objs));
            }
        }
        
        {
            ChoiceFormat fmt = new ChoiceFormat(
      "-1#is negative| 0#is zero or fraction | 1#is one |1.0<is 1+ |2#is two |2<is more than 2.");
 System.out.println("Formatter Pattern : " + fmt.toPattern());
 
 String actualString=fmt.format(-1);
 System.out.println("actualString:"+actualString);
 
            ParsePosition pp = new ParsePosition(0);
 Number actualValue = fmt.parse(actualString, pp);
            System.out.println("pp=" + pp+" actualValue:"+actualValue);
        }
    }

    @DataProvider
    public Object[][] svgpathData() {
        return new Object[][]{
            {new Object[]{SEG_MOVETO, 1.5, 2.7}, "m1.5,2.6"}
        };

    }   */ 
}
