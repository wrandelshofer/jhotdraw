/*
 * @(#)Dockable.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;

/**
 * Represents a dock child that the user can drag from one {@link Track} to
 * another {@link Track}.
 */
public interface Dockable extends DockChild {
    /**
     * The name of the {@link #graphicProperty()} ()}.
     */
    String GRAPHIC_PROPERTY = "graphic";
    /**
     * The name of the {@link #textProperty()} ()}.
     */
    String TEXT_PROPERTY = "text";

    /**
     * The graphic of this dockable.
     * <p>
     * The user uses the graphic to drag and drop the dockable.
     *
     * @return the graphic
     */
    @NonNull
    ObjectProperty<Node> graphicProperty();

    default Node getGraphic() {
        return graphicProperty().get();
    }

    default void setGraphic(Node value) {
        graphicProperty().set(value);
    }

    /**
     * The text of this dockable.
     * <p>
     * The user uses the text to identify the dockable.
     *
     * @return the graphic
     */
    @NonNull
    StringProperty textProperty();

    default String getText() {
        return textProperty().get();
    }

    default void setText(String value) {
        textProperty().set(value);
    }
}
