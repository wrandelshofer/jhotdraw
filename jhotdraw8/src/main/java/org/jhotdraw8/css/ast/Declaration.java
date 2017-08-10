/* @(#)Declaration.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

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
    private final List<PreservedToken> terms;
    private int startPos = -1;
    private int endPos = -1;

    public Declaration(String property, PreservedToken term) {
        this(property, Arrays.asList(new PreservedToken[]{term}));
    }

    public Declaration(String property, List<PreservedToken> terms) {
        this(property, terms, -1, -1);
    }

    public Declaration(String property, List<PreservedToken> terms, int startPos, int endPos) {
        this.property = property;
        this.terms = Collections.unmodifiableList(new ArrayList<PreservedToken>(terms));
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public String getProperty() {
        return property;
    }

    public List<PreservedToken> getTerms() {
        return terms;
    }

    public String getTermsAsString() {
        StringBuilder buf = new StringBuilder();

        for (PreservedToken t : terms) {
            buf.append(t.toString());
        }
        return buf.toString();
    }

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
