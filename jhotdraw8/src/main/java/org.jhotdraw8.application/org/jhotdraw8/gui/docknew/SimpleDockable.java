package org.jhotdraw8.gui.docknew;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

public class SimpleDockable extends Tab implements Dockable {
    protected final ObjectProperty<Dock> dockParent = new SimpleObjectProperty<>();

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
        textualIcon.getStyleClass().add(DOCKABLE_ICON_STYLE_CLASS);
        setGraphic(textualIcon);
        new DockableDragHandler(this);
    }

    @Override
    public @NonNull ObjectProperty<Dock> dockParentProperty() {
        return dockParent;
    }


    @Override
    public @NonNull ReadOnlyList<DockNode> getDockChildrenReadOnly() {
        return ImmutableLists.emptyList();
    }

    @Override
    public @NonNull ReadOnlyObjectProperty<Node> nodeProperty() {
        return contentProperty();
    }
}
