/* @(#)XMLConverterFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

/**
 * XMLConverterFactory.
 * <p>
 * Supports the following types:
 * <ul>
 * <li>number</li>
 * <li>word</li>
 * </ul>
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XMLConverterFactory implements ConverterFactory {

    @Override
    public Converter<?> apply(String type, String style) {
        if (type==null) {
            return new DefaultConverter();
        }
        switch (type) {
            case "number":
                return new XMLDoubleConverter();
            case "word":
                return new WordConverter();
            default:
                throw new IllegalArgumentException("illegal type:"+type);
        }
    }

}
