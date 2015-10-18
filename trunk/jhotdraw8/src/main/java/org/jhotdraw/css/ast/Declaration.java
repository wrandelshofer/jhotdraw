/*
 * @(#)Declaration.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css.ast;

/**
 * A "declaration" declares a "property" with a "value".
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Declaration extends AST {

    private final String property;
    private final String terms;

    public Declaration(String property, String terms) {
        this.property = property;
        this.terms = terms;
    }

    public String getProperty() {
        return property;
    }

    public String getTerms() {
        return terms;
    }

    @Override
    public String toString() {

        return property + ":" + terms;
    }
}
