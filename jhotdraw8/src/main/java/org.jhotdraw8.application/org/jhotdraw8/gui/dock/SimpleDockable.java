/*
 * @(#)SimpleDraggableDockChild.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

public class SimpleDockable extends AbstractDockable {
    private final Node node;

    public SimpleDockable(Node content) {
        this(null, content);
    }

    public SimpleDockable(String text, Node content) {
        this.node = content;
        setText(text);
        final Text textualIcon = new Text("❏");
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
    public @NonNull Node getNode() {
        return node;
    }


    @Override
    public @NonNull BooleanProperty showingProperty() {
        return showing;
    }
}
