/*
 * @(#)Declaration.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
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
 * @version $$Id$$
 */
public class Declaration extends AST {

    private final String property;
    private final List<String> terms;

    public Declaration(String property, String term) {
        this.property = property;
        this.terms = Arrays.asList(new String[]{term});
    }
    public Declaration(String property, List<String> terms) {
        this.property = property;
        this.terms = Collections.unmodifiableList(new ArrayList<String>(terms));
    }

    public String getProperty() {
        return property;
    }

    public List<String> getTerms() {
        return terms;
    }

    public String getTermsAsString() {
        StringBuilder buf = new StringBuilder();

        for (String t : terms) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            if (t.isEmpty()) {
                buf.append("''");
            } else if (t.matches(".*\\s.*")) {// FIXME implement escaping
                buf.append('\'');
                buf.append(t);
                buf.append('\'');
            }else{
                buf.append(t);
            }
        }
        return buf.toString();
    }

    @Override
    public String toString() {

        return property + ":" + terms;
    }
}
