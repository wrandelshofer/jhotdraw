/* @(#)BlendModeStyleableFigureKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.effect.BlendMode;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * BlendModeStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BlendModeStyleableFigureKey extends AbstractStyleableFigureKey<BlendMode> implements WriteableStyleableMapAccessor<BlendMode> {

    final static long serialVersionUID = 1L;

    private final CssMetaData<? extends Styleable, BlendMode> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public BlendModeStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public BlendModeStyleableFigureKey(String name, BlendMode defaultValue) {
        super(name, BlendMode.class, DirtyMask.of(DirtyBits.NODE), defaultValue);

        StyleablePropertyFactory<? extends Styleable> factory = new StyleablePropertyFactory<>(null);
        cssMetaData = factory.createEnumCssMetaData(BlendMode.class,
                Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData<? extends Styleable, BlendMode> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<BlendMode> converter;

    @Override
    public Converter<BlendMode> getConverter() {
        if (converter == null) {
            converter = new CssEnumConverter<>(BlendMode.class, false);
        }
        return converter;
    }

}
