/*
 * @(#)AST.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.css.CssToken;

import java.util.function.Consumer;

/**
 * Abstract syntax tree for cascading style sheets.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSyntaxTree {
    public void produceTokens(Consumer<CssToken> consumer) {
    }
}
