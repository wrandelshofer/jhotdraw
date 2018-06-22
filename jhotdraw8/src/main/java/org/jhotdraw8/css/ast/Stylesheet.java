/* @(#)Stylesheet.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * A stylesheet is a list of rules.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Stylesheet extends AST {

    @NonNull
    private final List<StyleRule> rules;

    public Stylesheet(@NonNull List<StyleRule> rules) {
        this.rules = Collections.unmodifiableList(rules);
    }

    /**
     * Returns only the style rules in the stylesheet.
     *
     * @return the rules
     */
    @NonNull
    public List<StyleRule> getStyleRules() {
        return rules;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (StyleRule r : rules) {
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
