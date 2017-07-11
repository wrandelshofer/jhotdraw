/* @(#)WriteableStyleableMapAccessor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * Interface for keys which support styled values from CSS.
 *
 * @author Werner Randelshofer
 * @param <T> The value type.
 */
public interface WriteableStyleableMapAccessor<T> extends ReadOnlyStyleableMapAccessor<T> {

    final static long serialVersionUID = 1L;


}
