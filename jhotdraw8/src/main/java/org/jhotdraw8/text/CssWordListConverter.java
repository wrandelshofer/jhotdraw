/* @(#)WordListConverter.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.Comparator;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.io.IdFactory;

/**
 * WordListConverter converts an ImmutableObservableList of Strings into a
 * String.
 * <p>
 The word list is actually a "set ofCollection space separated tokens", as specified in
 HTML 5 and in XML Schema Part 2.
 <p>
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
 * @version $Id$
 */
public class CssWordListConverter implements Converter<ImmutableList<String>> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,word}|[ \n\r\t]+}", new CssConverterFactory());

    public final static Comparator<String> NFD_COMPARATOR
            = (o1, o2) -> Normalizer.normalize(o1, Normalizer.Form.NFD).compareTo(
                    Normalizer.normalize(o2, Normalizer.Form.NFD));

    @Override
    public void toString(Appendable out, IdFactory idFactory, ImmutableList<String> value) throws IOException {
        if (value == null) {
            return;
        }
        Object[] v = new Object[value.size() + 1];
        v[0] = value.size();
        value.copyInto(v, 1);
        formatter.toString(out, v);
    }

    @Override
    public ImmutableList<String> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        ImmutableList<String> l =  ImmutableList.ofArray(v, 1, (int) v[0]);
        return l;
    }

    @Override
    public ImmutableList<String> getDefaultValue() {
        return ImmutableList.emptyList();
    }
}
