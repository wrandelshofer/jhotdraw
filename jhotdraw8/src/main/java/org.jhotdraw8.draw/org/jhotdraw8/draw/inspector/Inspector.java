/*
 * @(#)Inspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.Nullable;

/**
 * API for inspectors.
 *
 * @author Werner Randelshofer
 */
public interface Inspector<S> {
    ObjectProperty<S> subjectProperty();

    default void setSubject(@Nullable S s) {
        subjectProperty().set(s);
    }

    @Nullable
    default S getSubject() {
        return subjectProperty().get();
    }

    Node getNode();
}
