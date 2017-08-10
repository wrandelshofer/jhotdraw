/* @(#)WordListConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.css.PseudoClass;
import org.jhotdraw8.collection.ImmutableObservableSet;
import org.jhotdraw8.io.IdFactory;

/**
 * WordSetConverter converts an ImmutableObservableSet of Strings into a
 * String.
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
 * @version $Id$
 */
public class CssPseudoClassConverter implements Converter<ImmutableObservableSet<PseudoClass>> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,word}|[ \n\r\t]+}", new CssConverterFactory());

    public final static Comparator<PseudoClass> NFD_COMPARATOR
            = (o1, o2) -> Normalizer.normalize(o1.getPseudoClassName(), Normalizer.Form.NFD).compareTo(
                    Normalizer.normalize(o2.getPseudoClassName(), Normalizer.Form.NFD));

    @Override
    public void toString(Appendable out, IdFactory idFactory, ImmutableObservableSet<PseudoClass> value) throws IOException {
        Set<PseudoClass> tokens = new LinkedHashSet<>();
        tokens.addAll(value);
        Object[] v = new Object[tokens.size() + 1];
        v[0] = value.size();
        value.copyInto(v, 1);
        formatter.toString(out, v);
    }

    @Override
    public ImmutableObservableSet<PseudoClass> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        ImmutableObservableSet<PseudoClass> l =  new ImmutableObservableSet<>(v,1,(int)v[0]);
        return l;
    }

    @Override
    public ImmutableObservableSet<PseudoClass> getDefaultValue() {
        return ImmutableObservableSet.emptySet();
    }

}
