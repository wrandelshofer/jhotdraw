/* @(#)WordListConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jhotdraw.draw.io.IdFactory;

/**
 * WordListConverter converts an ObservableList of Strings into a String.
 * <p>
 * The word list is actually a "set of space separated tokens", as specified in
 * HTML 5 and in XML Schema Part 2.
 * <p>
 * The word list converter coalesces duplicate entries if they have the same
 * Unicode NFD form. The tokens are sorted using their Unicode NFD form.
 * <p>
 * References:
 * <ul>
 * <li><a href="https://dev.w3.org/html5/spec-preview/common-microsyntaxes.html#set-of-space-separated-tokens">
 * HTML 5, Common Microsyntaxes, Space-separated tokens
 * </a></li>
 * <li><a href="https://www.w3.org/TR/xmlschema-2/#token">
 * XML Schema Part 2, Built-in datatypes, Derived datatypes, Token
 * </a></li>
 * </ul>
 *
 * @author Werner Randelshofer
 */
public class CssObservableWordListConverter implements Converter<ObservableList<String>> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,word}|[ \n\r\t]+}", new CssConverterFactory());

    public final static Comparator<String> NFD_COMPARATOR
            = (o1, o2) -> Normalizer.normalize(o1, Normalizer.Form.NFD).compareTo(
            Normalizer.normalize(o2, Normalizer.Form.NFD));

    @Override
    public void toString(Appendable out, IdFactory idFactory, ObservableList<String> value) throws IOException {
        Set<String> tokens = new TreeSet<>(NFD_COMPARATOR);
        tokens.addAll(value);
        Object[] v = new Object[tokens.size() + 1];
        v[0] = value.size();
        int i = 1;
        for (String token : tokens) {
            v[i] = token;
            i++;
        }
        formatter.toString(out, v);
    }

    @Override
    public ObservableList<String> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        Set<String> tokens = new TreeSet<>(NFD_COMPARATOR);
        for (int i = 0, n = (int) v[0]; i < n; i++) {
            tokens.add((String) v[i + 1]);
        }
        ObservableList<String> l = FXCollections.observableArrayList();
        l.addAll(tokens);
        return l;
    }

    @Override
    public ObservableList<String> getDefaultValue() {
        return FXCollections.emptyObservableList();
    }

}
