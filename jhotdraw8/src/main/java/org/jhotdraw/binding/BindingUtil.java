/* @(#)BindingUtil.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.binding;

import javafx.beans.binding.StringExpression;

/**
 * BindingUtil.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BindingUtil {

    /** Returns a string expression which uses {@code java.test.MessageFormat} to format
     * the text.
     * See {@link MessageStringFormatter} for special treatment of boolean values.
     *
     * @param format The format string.
     * @param args The arguments.
     * @return  The string expression */
    public static StringExpression formatted(
            java.lang.String format,
            java.lang.Object... args) {
        return MessageStringFormatter.format(format, args);
    }

}
