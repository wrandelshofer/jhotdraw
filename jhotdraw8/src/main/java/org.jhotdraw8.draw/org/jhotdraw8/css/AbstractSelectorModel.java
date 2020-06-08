/*
 * @(#)AbstractSelectorModel.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import org.jhotdraw8.annotation.NonNull;

import java.util.Set;

public abstract class AbstractSelectorModel<E> implements SelectorModel<E> {
    private final MapProperty<String, Set<E>> additionalPseudoClassStates = new SimpleMapProperty<>(FXCollections.observableHashMap());

    @NonNull
    public MapProperty<String, Set<E>> additionalPseudoClassStatesProperty() {
        return additionalPseudoClassStates;
    }

}
