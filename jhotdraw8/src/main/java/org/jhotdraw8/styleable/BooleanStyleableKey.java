/* @(#)BooleanStyleableKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.StyleablePropertyFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.text.CssBooleanConverter;

/**
 * BooleanStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BooleanStyleableKey extends SimpleStyleableKey<Boolean> {

    private final static long serialVersionUID = 1L;

    public BooleanStyleableKey(String key) {
        this(key,  ReadOnlyStyleableMapAccessor.toCssName(key),null);
    }
    public BooleanStyleableKey(String key, Boolean defaultValue) {
        this(key,  ReadOnlyStyleableMapAccessor.toCssName(key),defaultValue);
    }

    public BooleanStyleableKey(String key, String cssName) {
        this(key,  ReadOnlyStyleableMapAccessor.toCssName(key),null);
    }

    public BooleanStyleableKey(String key, @NonNull String cssName, Boolean defaultValue) {
        super(key, Boolean.class, null, new CssBooleanConverter(),defaultValue);
        setCssMetaData(
                new StyleablePropertyFactory<>(null).createBooleanCssMetaData(
                        cssName, s -> {
                            StyleablePropertyBean spb = (StyleablePropertyBean) s;
                            return spb.getStyleableProperty(this);
                        })
        );
    }

}
