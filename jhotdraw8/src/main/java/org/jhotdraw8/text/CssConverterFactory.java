/* @(#)CssConverterFactory.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * CssConverterFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssConverterFactory implements ConverterFactory {

    @Nonnull
    @Override
    public Converter<?> apply(@Nullable String type, String style) {
        if (type == null) {
            return new DefaultConverter();
        }
        switch (type) {
            case "number":
                return new CssNumberConverter();
            case "size":
                return new CssSizeConverter();
            case "word":
                return new XmlWordConverter();
            case "paint":
                return new CssPaintableConverter();
            case "font":
                return new CssFontConverter();
            default:
                throw new IllegalArgumentException("illegal type:" + type);
        }
    }

}
