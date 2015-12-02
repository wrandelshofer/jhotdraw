/* @(#)CssConverterFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

/**
 * CssConverterFactory.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssConverterFactory implements ConverterFactory {

    @Override
    public Converter<?> apply(String type, String style) {
        if (type==null) {
            return new DefaultConverter();
        }
        switch (type) {
            case "number":
                return new XmlNumberConverter();
            case "size":
                return new CssSizeConverter();
            case "word":
                return new XmlWordConverter();
            case "paint":
                return new CssPaintConverter();
            case "font":
                return new CssFontConverter();
            default:
                throw new IllegalArgumentException("illegal type:"+type);
        }
    }

}
