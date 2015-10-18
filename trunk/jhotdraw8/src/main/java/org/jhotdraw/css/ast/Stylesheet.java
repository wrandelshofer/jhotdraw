/* @(#)Stylesheet.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css.ast;

import java.util.Collections;
import java.util.List;

/**
 * A stylesheet consists of a list of "rulesets".
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Stylesheet extends AST {

    private final List<Ruleset> rulesets;

    public Stylesheet(List<Ruleset> rulesets) {
        this.rulesets = Collections.unmodifiableList(rulesets);
    }

    public List<Ruleset> getRulesets() {
        return rulesets;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (Ruleset r : rulesets) {
            if (first) {
                first = false;
            } else {
                buf.append('\n');
            }
            buf.append(r.toString());
        }
        return buf.toString();
    }
}
