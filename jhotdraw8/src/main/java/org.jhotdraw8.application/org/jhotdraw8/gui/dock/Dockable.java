/*
 * @(#)Dockable.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;

public interface Dockable extends DockNode {
    /**
     * The style class for the dockable icon.
     * <p>
     * Value: {@value #DOCKABLE_ICON_STYLE_CLASS}.
     */
    String DOCKABLE_ICON_STYLE_CLASS = "dockable-icon";

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
