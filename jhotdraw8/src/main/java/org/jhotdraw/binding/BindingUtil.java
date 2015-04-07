/* @(#)BindingUtil.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

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
