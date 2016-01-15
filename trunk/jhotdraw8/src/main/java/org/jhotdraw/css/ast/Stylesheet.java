/* @(#)Stylesheet.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import java.util.Collections;
import java.util.List;

/**
 * A stylesheet consists of a list of "rulesets".
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Stylesheet extends AST {

    private final List<StyleRule> rulesets;

    public Stylesheet(List<StyleRule> rulesets) {
        this.rulesets = Collections.unmodifiableList(rulesets);
    }

    public List<StyleRule> getRulesets() {
        return rulesets;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (StyleRule r : rulesets) {
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
