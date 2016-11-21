/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.text.XmlNumberConverter;
import java.io.IOException;
import java.text.ParseException;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author werni
 */
public class XMLDoubleConverterNGTest {

    /**
     * Test of toString method, of class XmlNumberConverter.
     */
    @Test(dataProvider = "doubleData")
    public void testToString(Double inputValue, String expectedValue) {
        XmlNumberConverter c = new XmlNumberConverter();

        String actualValue = c.toString(inputValue);

        assertEquals(actualValue, expectedValue);
    }

    /**
     * Test of toString method, of class XmlNumberConverter.
     */
    @Test(dataProvider = "doubleData")
    public void testFromString(Double expectedValue, String inputValue) throws ParseException, IOException {
        XmlNumberConverter c = new XmlNumberConverter();

        Number actualValue = c.fromString(inputValue);

        assertEquals(actualValue, expectedValue);
    }

    @DataProvider
    public Object[][] doubleData() {
        return new Object[][]{
          //  {null,""},
            {-0.0, "-0"},
            {0.0, "0"},
            {1.0, "1"},
            {12.0, "12"},
            {123.0, "123"},
            {1234.0, "1234"},
            {12345.0, "12345"},
            {123456.0, "123456"},
            {1234567.0, "1234567"},
            {12345678.0, "1.2345678E7"},
            {123456789.0, "1.23456789E8"},
            {1234567890.0, "1.23456789E9"},
            {12345678901.0, "1.2345678901E10"},
            {Math.PI, "3.141592653589793"},
            {-Math.PI, "-3.141592653589793"},
            {0.1, "0.1"},
            {0.02, "0.02"},
            {0.003, "0.003"},
            {0.0004, "4.0E-4"},
            {0.00005, "5.0E-5"},
            {0.000006, "6.0E-6"},
            {0.0000007, "7.0E-7"},
            {0.00000008, "8.0E-8"},
            {0.000000009, "9.0E-9"},
            {0.00000000987654321, "9.87654321E-9"},
            {0.000000009876543210, "9.87654321E-9"},
            {0.0000000098765432109, "9.8765432109E-9"},
            {-0.0000000098765432109, "-9.8765432109E-9"},
            {1.000000009, "1.000000009"},
            {20.000000009, "20.000000009"},
            {300.000000009, "300.000000009"},
            {4000.000000009, "4000.000000009"},
            {50000.000000009, "50000.000000009"},
            {600000.000000009, "600000.000000009"},
            {7000000.000000009, "7000000.000000009"},
            {80000000.000000009, "8.000000000000001E7"},
            {900000000.000000009, "9.0E8"},
            {1.00000000001, "1.00000000001"},
            {1.000000000002, "1.000000000002"},
            {1.0000000000003, "1.0000000000003"},
            {1.00000000000004, "1.00000000000004"},
            {1.000000000000005, "1.000000000000005"},
            {1.0000000000000006, "1.0000000000000007"},
            {1.00000000000000007, "1"},
            {1.000000000000000008, "1"},
            {1.0000000000000000009, "1"},
            {Double.MAX_VALUE,"1.7976931348623157E308"},
            {Double.MIN_VALUE,"4.9E-324"},
            {-Double.MAX_VALUE,"-1.7976931348623157E308"},
            {-Double.MIN_VALUE,"-4.9E-324"},
            {Double.NEGATIVE_INFINITY,"-INF"},
            {Double.POSITIVE_INFINITY,"INF"},
            {Double.NaN,"NaN"},
        };
    }
}
