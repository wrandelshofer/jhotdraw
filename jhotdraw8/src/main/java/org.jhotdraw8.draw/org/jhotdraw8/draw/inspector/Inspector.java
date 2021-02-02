/*
 * @(#)Inspector.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.Nullable;

/**
 * API for inspectors.
 *
 * @author Werner Randelshofer
 */
public interface Inspector<S> {
    /**
     * The name of the {@link #subjectProperty}.
     */
    String SUBJECT_PROPERTY = "subject";

    /**
     * The name of the {@link #showingProperty}.
     */
    String SHOWING_PROPERTY = "showing";

    ObjectProperty<S> subjectProperty();

    default void setSubject(@Nullable S s) {
        subjectProperty().set(s);
    }

    @Nullable
    default S getSubject() {
        return subjectProperty().get();
    }

    Node getNode();

    /**
     * Whether this inspector is showing.
     * <p>
     * An inspector that is not showing should not consume CPU resources.
     * <p>
     * This property is set by parent nodes in the scene graph,, for example
     * depending on whether this inspector is in a collapsed pane.
     *
     * @return true if this inspector is showing.
     */
    BooleanProperty showingProperty();

    default boolean isShowing() {
        return showingProperty().get();
    }

    default void setShowing(boolean newValue) {
        showingProperty().set(newValue);
    }
}
