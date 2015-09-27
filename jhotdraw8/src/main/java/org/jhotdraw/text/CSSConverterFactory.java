/* @(#)CSSConverterFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

/**
 * CSSConverterFactory.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CSSConverterFactory implements ConverterFactory {

    @Override
    public Converter<?> apply(String type, String style) {
        if (type==null) {
            return new DefaultConverter();
        }
        switch (type) {
            case "number":
                return new XMLDoubleConverter();
            case "size":
                return new CSSSizeConverter();
            case "word":
                return new WordConverter();
            case "paint":
                return new CSSPaintConverter();
            case "font":
                return new CSSFontConverter();
            default:
                throw new IllegalArgumentException("illegal type:"+type);
        }
    }

}
