/* @(#)StyleablePropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import javafx.css.StyleableProperty;
import org.jhotdraw.beans.PropertyBean;
import org.jhotdraw.collection.Key;

/**
 * StyleablePropertyBean.
 * @author Werner Randelshofer
 */
public interface StyleablePropertyBean extends PropertyBean {
    <T> StyleableProperty<T> getStyleableProperty(Key<T> key);
}
