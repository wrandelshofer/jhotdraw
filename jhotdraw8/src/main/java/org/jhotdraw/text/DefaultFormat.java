/* @(#)DefaultFormat.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * DefaultFormat.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultFormat extends Format {

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        toAppendTo.append(obj == null ? "null" : obj.toString());
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        pos.setIndex(source.length());
        return source;
    }

}
