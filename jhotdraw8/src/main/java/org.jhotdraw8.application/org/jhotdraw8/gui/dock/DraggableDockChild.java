/*
 * @(#)DraggableDockChild.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;

/**
 * Represents a dock child that the user can drag from one {@code Dock} to
 * another {@code Dock}.
 */
public interface DraggableDockChild extends DockChild {
    /**
     * The style class for the graphic object.
     * <p>
     * Value: {@value #DRAGGABLE_DOCK_CHILD_GRAPHIC_STYLE_CLASS}.
     */
    String DRAGGABLE_DOCK_CHILD_GRAPHIC_STYLE_CLASS = "draggable-dock-child-graphic";

    @NonNull
    ObjectProperty<Node> graphicProperty();

    default Node getGraphic() {
        return graphicProperty().get();
    }

    default void setGraphic(Node value) {
        graphicProperty().set(value);
    }

    @NonNull
    StringProperty textProperty();

    default String getText() {
        return textProperty().get();
    }

    default void setText(String value) {
        textProperty().set(value);
    }

    @NonNull
    StringProperty idProperty();

    default String getId() {
        return idProperty().get();
    }

    default void setId(String value) {
        idProperty().set(value);
    }


}
