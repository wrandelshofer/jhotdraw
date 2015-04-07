/* @(#)CDataConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Converts a String to XML CData and vice versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CDataConverter implements Converter<String> {

    @Override
    public String toString(String value) {
        return value.replace("&","&amp;").replace("\"","&quot;");
    }

    @Override
    public String toValue(String string)  {
        ParsePosition pp = new ParsePosition(0);
        String value = toValue(string, pp);
        return value;
    }

    @Override
    public String toValue(String string, ParsePosition pp) {
        String sub = string.substring(pp.getIndex());
        String value= sub.replace("&quot;","\"").replace("&amp;","&");
        pp.setIndex(string.length());
        return value;
    }


}
