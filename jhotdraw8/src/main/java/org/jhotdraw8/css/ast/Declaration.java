/* @(#)Declaration.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.css.CssToken;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A "declaration" associates a "property" with a list of preserved tokens. If
 * the list of preserved tokens is empty, the declaration must be ignored.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Declaration extends AST {

    private final String property;
    @Nonnull
    private final List<CssToken> terms;
    private int startPos = -1;
    private int endPos = -1;

    public Declaration(String property, CssToken term) {
        this(property, Arrays.asList(new CssToken[]{term}));
    }

    public Declaration(String property, @Nonnull List<CssToken> terms) {
        this(property, terms, -1, -1);
    }

    public Declaration(String property, @Nonnull List<CssToken> terms, int startPos, int endPos) {
        this.property = property;
        this.terms = Collections.unmodifiableList(new ArrayList<>(terms));
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public String getProperty() {
        return property;
    }

    @Nonnull
    public List<CssToken> getTerms() {
        return terms;
    }

    @Nonnull
    public String getTermsAsString() {
        StringBuilder buf = new StringBuilder();

        for (CssToken t : terms) {
            buf.append(t.toString());
        }
        return buf.toString();
    }

    @Nonnull
    @Override
    public String toString() {

        return property + ":" + getTermsAsString();
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

}
