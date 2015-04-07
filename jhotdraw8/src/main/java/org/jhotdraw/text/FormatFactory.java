/* @(#)FormatFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.text;

import java.text.Format;
import java.util.function.BiFunction;

/**
 * FormatFactory returns a Format object given a format type and a format style.
 * <p>
 * See {@code PatternFormat} for details..
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FormatFactory extends BiFunction<String,String,Format> {
    /** The type and the style can be null! */
    @Override
    public Format apply(String type, String style);

}
