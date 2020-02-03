package org.jhotdraw8.gui.docknew;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.gui.CustomSkin;

public abstract class AbstractDock
        extends Control
        implements Dock {
    protected final ObjectProperty<Dock> dockParent = new SimpleObjectProperty<>();
    protected final ObservableList<DockNode> dockChildren = FXCollections.observableArrayList();
    private final ReadOnlyObjectProperty<Node> node = new ReadOnlyObjectWrapper<>((Node) this).getReadOnlyProperty();
    private final DockableDragHandler dragHandler = new DockableDragHandler();

    public AbstractDock() {
        setSkin(new CustomSkin<>(this));
        dockChildren.addListener(this::onChildrenChanged);
        setMinHeight(10);
        setMinWidth(10);
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
    }

    @Override
    public @NonNull ObjectProperty<Dock> dockParentProperty() {
        return dockParent;
    }


    @Override
    public @NonNull ObservableList<DockNode> getDockChildren() {
        return dockChildren;
    }


    @Override
    public @NonNull ReadOnlyObjectProperty<Node> nodeProperty() {
        return node;
    }

    private void onChildrenChanged(ListChangeListener.Change<? extends DockNode> change) {
        while (change.next()) {
            for (DockNode removed : change.getRemoved()) {
                if (removed.getDockParent() == this) {
                    removed.setDockParent(null);
                    if (removed instanceof Dockable) {
                        dragHandler.remove((Dockable) removed);
                    }
                }
            }
            for (DockNode added : change.getAddedSubList()) {
                added.setDockParent(this);
                if (added instanceof Dockable) {
                    dragHandler.add((Dockable) added);
                }
            }

        }
    }

    @Override
    protected void layoutChildren() {
        for (Node child : getChildren()) {
            child.resizeRelocate(0, 0, getWidth(), getHeight());
        }

    }
}
