/* @(#)ClassSelector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * A "class selector" matches an element if the element has a style class with
 * the specified value.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ClassSelector extends SimpleSelector {

    private final String clazz;

    public ClassSelector(String clazz) {
        this.clazz = clazz;
    }

    @NonNull
    @Override
    public String toString() {
        return "Class:" + clazz;
    }

    @Nullable
    @Override
    public <T> T match(@NonNull SelectorModel<T> model, @Nullable T element) {
        return (element != null && model.hasStyleClass(element, clazz)) //
                ? element : null;
    }

    @Override
    public int getSpecificity() {
        return 10;
    }

}
