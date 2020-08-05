/*
 * @(#)SimpleDraggableDockChild.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

public class SimpleDockable extends Tab implements Dockable {
    protected final ObjectProperty<DockParent> dockParent = new SimpleObjectProperty<>();
    protected final BooleanProperty showing = new SimpleBooleanProperty();

    public SimpleDockable(Node content) {
        this(null, null, content);
    }

    public SimpleDockable(String text, Node content) {
        this(text, text, content);
    }

    public SimpleDockable(String id, String text, Node content) {
        super(text, content);
        setId(id);
        final Text textualIcon = new Text("❏");
        textualIcon.getStyleClass().add(DRAGGABLE_DOCK_CHILD_GRAPHIC_STYLE_CLASS);
        setGraphic(textualIcon);
    }

    @Override
    public @NonNull ObjectProperty<DockParent> dockParentProperty() {
        return dockParent;
    }


    @Override
    public @NonNull ReadOnlyList<DockChild> getDockChildrenReadOnly() {
        return ImmutableLists.emptyList();
    }

    @Override
    public @NonNull ReadOnlyObjectProperty<Node> nodeProperty() {
        return contentProperty();
    }

    @Override
    public @NonNull BooleanProperty showingProperty() {
        return showing;
    }
}
