/* @(#)WordListConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jhotdraw.draw.io.IdFactory;

/**
 * WordListConverter.
 *
 * @author Werner Randelshofer
 */
public class CssObservableWordListConverter implements Converter<ObservableList<String>> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,word}|[ ]+}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, ObservableList<String> value) throws IOException {
        Object[] v = new Object[value.size()+1];
        v[0]=value.size();
        for (int i=0,n=value.size();i<n;i++) {
            v[i+1]=value.get(i);
        }
        formatter.toString(out, v);
    }

    @Override
    public ObservableList<String> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        ObservableList<String> l = FXCollections.observableArrayList();
        for (int i=0,n=(int)v[0];i<n;i++) {
            l.add((String)v[i+1]);
        }
        return l;
    }
    @Override
    public ObservableList<String> getDefaultValue() {
        return FXCollections.emptyObservableList();
    }

}
