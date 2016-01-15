/* @(#)Declaration.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A "declaration" declares a "property" with a "value".
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Declaration extends AST {

    private final String property;
    private final List<Term> terms;

    public Declaration(String property, Term term) {
        this.property = property;
        this.terms = Arrays.asList(new Term[]{term});
    }
    public Declaration(String property, List<Term> terms) {
        this.property = property;
        this.terms = Collections.unmodifiableList(new ArrayList<Term>(terms));
    }

    public String getProperty() {
        return property;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public String getTermsAsString() {
        StringBuilder buf = new StringBuilder();

        for (Term t : terms) {
            buf.append(t.toString());
        }
        return buf.toString();
    }

    @Override
    public String toString() {

        return property + ":" + getTermsAsString();
    }
}
