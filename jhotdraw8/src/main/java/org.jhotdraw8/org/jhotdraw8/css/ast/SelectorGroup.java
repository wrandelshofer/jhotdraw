/* @(#)SelectorGroup.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.SelectorModel;

import java.util.List;

/**
 * A "selector group" matches an element if one of its selectors matches the
 * element.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectorGroup extends AST {

    @Nonnull
    private final ReadOnlyList<Selector> selectors;

    public SelectorGroup(Selector selector) {
        this.selectors = ImmutableList.of(new Selector[]{selector});
    }

    public SelectorGroup(@Nonnull List<Selector> selectors) {
        this.selectors = ImmutableList.ofCollection(selectors);
    }

    @Nonnull
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("( ");
        boolean first = true;
        for (Selector s : selectors) {
            if (first) {
                first = false;
            } else {
                buf.append(" || ");
            }
            buf.append(s);
        }
        buf.append(" )");
        return buf.toString();
    }

    /**
     * Returns true if the rule matches the element.
     *
     * @param <T>     the element type
     * @param model   The helper is used to access properties of the element and
     *                parent or sibling elements in the document.
     * @param element the element
     * @return true on match
     */
    public <T> boolean matches(SelectorModel<T> model, T element) {
        return match(model, element) != null;
    }

    /**
     * Returns the selector which matches the specified element or null.
     *
     * @param <T>     the element type
     * @param model   The helper is used to access properties of the element and
     *                parent or sibling elements in the document.
     * @param element the element
     * @return the selector which matches the specified element, returns null if
     * no selector matches
     */
    @Nullable
    public <T> Selector match(SelectorModel<T> model, T element) {
        for (Selector s : selectors) {
            T result = s.match(model, element);
            if (result != null) {
                return s;
            }
        }
        return null;
    }
}
