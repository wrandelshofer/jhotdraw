/*
 * @(#)Stylesheet.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A stylesheet is a list of rules.
 *
 * @author Werner Randelshofer
 */
public class Stylesheet extends AST {

    @NonNull
    private final ImmutableList<Rule> rules;
    @NonNull
    private final ImmutableList<StyleRule> styleRules;

    public Stylesheet(@NonNull List<Rule> rules) {
        this.rules = ImmutableLists.ofCollection(rules);
        this.styleRules = ImmutableLists.ofCollection(
                rules.stream()
                        .filter(r -> r instanceof StyleRule)
                        .map(r -> (StyleRule) r)
                        .collect(Collectors.toList()));
    }

    /**
     * Returns only the style rules in the stylesheet.
     *
     * @return the rules
     */
    @NonNull
    public ReadOnlyList<StyleRule> getStyleRules() {
        return styleRules;
    }

    /**
     * Returns rules in the stylesheet.
     *
     * @return the rules
     */
    @NonNull
    public ReadOnlyList<Rule> getRules() {
        return rules;
    }

    @NonNull
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
