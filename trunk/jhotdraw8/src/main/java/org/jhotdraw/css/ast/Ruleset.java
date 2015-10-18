/* @(#)Ruleset.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css.ast;

import java.util.Collections;
import java.util.List;

/**
 * A ruleset consists of a "selector group" and a list of "declaration"s.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Ruleset extends AST {

    private final SelectorGroup selectorGroup;
    private final List<Declaration> declarations;

    public Ruleset(SelectorGroup selectorGroup, List<Declaration> declarations) {
        this.selectorGroup = selectorGroup;
        this.declarations = Collections.unmodifiableList(declarations);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("Ruleset: ");
        buf.append(selectorGroup.toString());
        buf.append("{");
        for (Declaration r : declarations) {
            buf.append(r.toString());
        }
        buf.append("}");
        return buf.toString();
    }

    public SelectorGroup getSelectorGroup() {
        return selectorGroup;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

}
