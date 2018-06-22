/* @(#)Declaration.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

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
    @NonNull
    private final List<PreservedToken> terms;
    private int startPos = -1;
    private int endPos = -1;

    public Declaration(String property, PreservedToken term) {
        this(property, Arrays.asList(new PreservedToken[]{term}));
    }

    public Declaration(String property, @NonNull List<PreservedToken> terms) {
        this(property, terms, -1, -1);
    }

    public Declaration(String property, @NonNull List<PreservedToken> terms, int startPos, int endPos) {
        this.property = property;
        this.terms = Collections.unmodifiableList(new ArrayList<PreservedToken>(terms));
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public String getProperty() {
        return property;
    }

    @NonNull
    public List<PreservedToken> getTerms() {
        return terms;
    }

    @NonNull
    public String getTermsAsString() {
        StringBuilder buf = new StringBuilder();

        for (PreservedToken t : terms) {
            buf.append(t.toString());
        }
        return buf.toString();
    }

    @NonNull
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
