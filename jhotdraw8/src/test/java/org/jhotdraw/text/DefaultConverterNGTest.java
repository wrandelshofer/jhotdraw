/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.text;

import java.nio.CharBuffer;
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
public class DefaultConverterNGTest {
    
    @Test(dataProvider="textData")
    public void testFromString(String expectedOutput, String input) throws Exception {
        DefaultConverter c = new DefaultConverter();
        
        Object actualOutput = c.fromString(input);
        
        assertEquals(actualOutput, expectedOutput);
    }

    @Test(dataProvider="textData")
    public void testToString(String input, String expectedOutput) throws Exception {
        DefaultConverter c = new DefaultConverter();
        
        Object actualOutput = c.toString(input);
        
        assertEquals(actualOutput, expectedOutput);
    }
    @DataProvider
    public Object[][] textData() {
        return new Object[][]{
            {"hello world", "hello world"},
        };

    } 
}
