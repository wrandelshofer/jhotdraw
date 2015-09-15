/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhotdraw.text;

/**
 * XMLConverterFactory.
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
            default:
                throw new IllegalArgumentException("illegal type:"+type);
        }
    }

}
