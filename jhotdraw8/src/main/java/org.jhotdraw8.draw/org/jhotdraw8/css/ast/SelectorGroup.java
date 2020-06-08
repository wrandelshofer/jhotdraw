/*
 * @(#)SelectorGroup.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.SelectorModel;

import java.util.List;
import java.util.function.Consumer;

/**
 * A "selector group" matches an element if one of its selectors matches the
 * element.
 *
 * @author Werner Randelshofer
 */
public class SelectorGroup extends Selector {

    @NonNull
    private final ReadOnlyList<Selector> selectors;

    public SelectorGroup(Selector selector) {
        this.selectors = ImmutableLists.of(selector);
    }

    public SelectorGroup(@NonNull List<Selector> selectors) {
        this.selectors = ImmutableLists.ofCollection(selectors);
    }

    @NonNull
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

    @Override
    public int getSpecificity() {
        return selectors.stream().mapToInt(Selector::getSpecificity).sum();
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

    @Nullable
    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        for (Selector s : selectors) {
            T result = s.match(model, element);
            if (result != null) {
                return result;
            }
        }
        return null;
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
    public <T> Selector matchSelector(@NonNull SelectorModel<T> model, @NonNull T element) {
        for (Selector s : selectors) {
            T result = s.match(model, element);
            if (result != null) {
                return s;
            }
        }
        return null;
    }

    @Override
    public void produceTokens(@NonNull Consumer<CssToken> consumer) {
        boolean first = true;
        for (Selector s : selectors) {
            if (first) {
                first = false;
            } else {
                consumer.accept(new CssToken(CssTokenType.TT_COMMA));
                consumer.accept(new CssToken(CssTokenType.TT_S, "\n"));
            }
            s.produceTokens(consumer);

        }
    }
}
