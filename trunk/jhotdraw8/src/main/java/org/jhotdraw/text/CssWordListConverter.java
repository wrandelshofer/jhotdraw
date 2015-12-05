/* @(#)CssWordListConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jhotdraw.draw.io.IdFactory;

/**
 * CssWordListConverter.
 *
 * @author Werner Randelshofer
 */
public class CssWordListConverter implements Converter<List<String>> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,word}|[ ]+}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, List<String> value) throws IOException {
        Object[] v = new Object[value.size()+1];
        v[0]=value.size();
        for (int i=0,n=value.size();i<n;i++) {
            v[i+1]=value.get(i);
        }
        formatter.toString(out, v);
    }

    @Override
    public List<String> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        ArrayList<String> l = new ArrayList<>((int)v[0]);
        for (int i=0,n=(int)v[0];i<n;i++) {
            l.add((String)v[i+1]);
        }
        return l;
    }
    @Override
    public List<String> getDefaultValue() {
        return Collections.emptyList();
    }
}
