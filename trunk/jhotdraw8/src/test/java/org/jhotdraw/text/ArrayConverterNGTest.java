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
public class ArrayConverterNGTest {

    public ArrayConverterNGTest() {
    }

    /**
     * Test of parseTextFormatPattern method, of class PatternConverter.
     */
    @Test(dataProvider = "patternData")
    public void testParsePattern(String pattern, Object[] value, String expectedOutput) throws IOException {
        PatternConverter.AST ast = PatternConverter.parseTextFormatPattern(pattern);
        System.out.println(ast);
        
        PatternConverter c = new PatternConverter(pattern, new MessageFormatConverterFactory());
        String actualOutput=c.toString(value);
        
        System.out.println(actualOutput);
        
        
    }

    @DataProvider
    public Object[][] patternData() {
        return new Object[][]{
            {"hello world", new Object[]{}, "hello world"},
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
            {"{0,choice,0#hello world|1#good morning}bla", new Object[]{1}, "good morningbla"},
            {"{0,list,{1}|separator}", new Object[]{2,1,2}, "1separator2"},
            {"{0,list,{1}|separator}bla", new Object[]{2,1,2}, "1separator2bla"},
            {"{0,list,{1}{2}|}'+'{3,list,{4}|-}={7}", new Object[]{2,'h','e','l','o',5,'w','o','r','l','d','!'}, "1.1x1.2,2.1x2.2,11.1a11.2b11.3;12.1a12.2b12.3;13.1a13.2b13.3=4"},
            
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
