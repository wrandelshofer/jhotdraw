/* @(#)DefaultConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.text;

import java.text.ParseException;
import java.text.ParsePosition;

/**
 * DefaultConverter.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultConverter implements Converter<String> {

    @Override
    public String toString(String value) {
        return value;
    }

    @Override
    public String toValue(String string, ParsePosition pp) {
        String value= string.substring(pp.getIndex());
        pp.setIndex(string.length());
        return value;
    }

}
