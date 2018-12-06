/* @(#)UniversalSelector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * A "select nothing selector" matches nothing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectNothingSelector extends SimpleSelector {

    @Nonnull
    @Override
    public String toString() {
        return "SelectNothing";
    }

    @Nullable
    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        return null;
    }

    @Override
    public int getSpecificity() {
        return 0;
    }
}
