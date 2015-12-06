/* @(#)QualifiedRule.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css.ast;

import java.util.Collections;
import java.util.List;

/**
 * A "At rule" consists of an "At keyword", a "selector list" and a list of
 * "declaration"s.
 * 
 * FIXME - An At Rule is actually quite more complex
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class AtRule extends AST {

    private final String atKeyword;
    private final SelectorGroup selectorList;
    private final List<Declaration> declarations;

    public AtRule(String atKeyword,
            SelectorGroup selectorGroup, List<Declaration> declarations) {
        this.atKeyword=atKeyword;
        this.selectorList = selectorGroup;
        this.declarations = declarations==null?Collections.emptyList(): Collections.unmodifiableList(declarations);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("AtRule: ");
        buf.append(selectorList.toString());
        buf.append("{");
        for (Declaration r : declarations) {
            buf.append(r.toString());
        }
        buf.append("}");
        return buf.toString();
    }

    public SelectorGroup getSelectorGroup() {
        return selectorList;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

}
