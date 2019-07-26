/*
 * @(#)NulllableBooleanStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.css.text.CssBooleanConverter;

/**
 * Nullable BooleanStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NulllableBooleanStyleableKey extends SimpleStyleableKey<Boolean> implements WriteableStyleableMapAccessor<Boolean> {

    private final static long serialVersionUID = 1L;

    public NulllableBooleanStyleableKey(String key) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), null);
    }

    public NulllableBooleanStyleableKey(String key, Boolean defaultValue) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), defaultValue);
    }

    public NulllableBooleanStyleableKey(String key, String cssName) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), null);
    }

    public NulllableBooleanStyleableKey(String key, @Nonnull String cssName, Boolean defaultValue) {
        super(key, Boolean.class, null, new CssBooleanConverter(true), defaultValue);
        setCssMetaData(
                new StyleablePropertyFactory<>(null).createBooleanCssMetaData(
                        cssName, s -> {
                            StyleablePropertyBean spb = (StyleablePropertyBean) s;
                            return spb.getStyleableProperty(this);
                        })
        );
    }

}
