/* @(#)BooleanStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.css.text.CssBooleanConverter;

/**
 * BooleanStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BooleanStyleableKey extends SimpleStyleableKey<Boolean> implements WriteableStyleableMapAccessor<Boolean> {

    private final static long serialVersionUID = 1L;

    public BooleanStyleableKey(String key) {
        this(key, ReadableStyleableMapAccessor.toCssName(key), null);
    }

    public BooleanStyleableKey(String key, Boolean defaultValue) {
        this(key, ReadableStyleableMapAccessor.toCssName(key), defaultValue);
    }

    public BooleanStyleableKey(String key, String cssName) {
        this(key, ReadableStyleableMapAccessor.toCssName(key), null);
    }

    public BooleanStyleableKey(String key, @Nonnull String cssName, Boolean defaultValue) {
        super(key, Boolean.class, null, new CssBooleanConverter(false), defaultValue);
        setCssMetaData(
                new StyleablePropertyFactory<>(null).createBooleanCssMetaData(
                        cssName, s -> {
                            StyleablePropertyBean spb = (StyleablePropertyBean) s;
                            return spb.getStyleableProperty(this);
                        })
        );
    }

}
