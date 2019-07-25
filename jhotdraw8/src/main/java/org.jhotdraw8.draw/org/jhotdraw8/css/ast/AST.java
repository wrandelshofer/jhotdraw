/* @(#)AST.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.css.CssToken;

import java.util.function.Consumer;

/**
 * Abstract syntax tree for cascading style sheets.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AST {
    public void produceTokens(Consumer<CssToken> consumer) {
    }
}
