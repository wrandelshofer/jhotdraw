/* @(#)CSSSizeListConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.io.IdFactory;

/**
 * CSSSizeListConverter.
 * <p>
 * Parses a list of sizes.
 * 
 * @author Werner Randelshofer
 */
public class CSSSizeListConverter implements Converter<List<Double>> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,size}|[ ]+}", new CSSConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, List<Double> value) throws IOException {
        Object[] v = new Object[value.size()+1];
        v[0]=value.size();
        for (int i=0,n=value.size();i<n;i++) {
            v[i+1]=value.get(i);
        }
        formatter.toString(out, v);
    }

    @Override
    public List<Double> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        ArrayList<Double> l = new ArrayList<>((int)v[0]);
        for (int i=0,n=(int)v[0];i<n;i++) {
            l.add((Double)v[i+1]);
        }
        return l;
    }
}
