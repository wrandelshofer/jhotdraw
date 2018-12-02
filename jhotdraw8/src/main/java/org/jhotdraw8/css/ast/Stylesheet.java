/* @(#)Stylesheet.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.collection.ImmutableArrayList;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadOnlyList;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A stylesheet is a list of rules.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Stylesheet extends AST {

    @Nonnull
    private final ImmutableList<Rule> rules;
    @Nonnull
    private final ImmutableList<StyleRule> styleRules;

    public Stylesheet(@Nonnull List<Rule> rules) {
        this.rules = ImmutableList.ofCollection(rules);
        this.styleRules = ImmutableList.ofCollection(
                rules.stream()
                        .filter(r->r instanceof StyleRule)
                        .map(r->(StyleRule)r)
                        .collect(Collectors.toList()));
    }

    /**
     * Returns only the style rules in the stylesheet.
     *
     * @return the rules
     */
    @Nonnull
    public ReadOnlyList<StyleRule> getStyleRules() {
        return styleRules;
    }
    /**
     * Returns rules in the stylesheet.
     *
     * @return the rules
     */
    @Nonnull
    public ReadOnlyList<Rule> getRules() {
        return rules;
    }

    @Nonnull
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (Rule r : rules) {
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
