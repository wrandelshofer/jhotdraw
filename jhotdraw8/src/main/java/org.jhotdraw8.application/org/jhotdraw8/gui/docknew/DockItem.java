package org.jhotdraw8.gui.docknew;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;

public interface DockItem extends DockComponent {
    /**
     * The style class for the dock leaf icon.
     * <p>
     * Value: {@value #DOCK_LEAF_ICON_STYLE_CLASS}.
     */
    String DOCK_LEAF_ICON_STYLE_CLASS = "dock-leaf-icon";

    /**
     * Data format used for dragging a DockItem with the drag board.
     * The value of this data format is the {@link System#identityHashCode(Object)}
     * of the dragged leaf.
     */
    DataFormat DRAGGED_LEAF_DATA_FORMAT = new DataFormat("application/x-jhotdraw8-dragged-dock-leaf");
    /**
     * We store the dragged item here, because we move the <i>reference</i>
     * of a DockItem with the drag board rather than a value of the DockItem.
     */
    ObjectProperty<DockItem> draggedItem = new SimpleObjectProperty<>();

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


    static DockItem getDraggedItem() {
        return draggedItem.get();
    }

    static void setDraggedItem(DockItem value) {
        draggedItem.set(value);
    }

    @NonNull
    ObjectProperty<Node> contentProperty();

    default Node getContent() {
        return contentProperty().get();
    }

    default void setContent(Node value) {
        contentProperty().set(value);
    }

}
