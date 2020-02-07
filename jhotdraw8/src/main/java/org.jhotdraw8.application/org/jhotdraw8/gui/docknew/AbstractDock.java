package org.jhotdraw8.gui.docknew;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.gui.CustomSkin;

public abstract class AbstractDock
        extends Control
        implements Dock {
    protected final ObjectProperty<Dock> dockParent = new SimpleObjectProperty<>();
    protected final ObservableList<DockNode> dockChildren = FXCollections.observableArrayList();
    private final ReadOnlyObjectProperty<Node> node = new ReadOnlyObjectWrapper<>((Node) this).getReadOnlyProperty();

    public AbstractDock() {
        setSkin(new CustomSkin<>(this));
        //CustomBinding.bindElements(dockChildren,DockNode::setDockParent,this);
        dockChildren.addListener((ListChangeListener.Change<? extends DockNode> change) -> {
            while (change.next()) {
                for (DockNode removed : change.getRemoved()) {
                    removed.setDockParent(null);
                }
                for (DockNode added : change.getAddedSubList()) {
                    if (added.getDockParent() != null) {
                        throw new IllegalStateException("Added still has parent " + added);
                    }
                    if (added instanceof DockPane) {
                        throw new IllegalStateException("Added DockPane cannot have parent " + added);
                    }
                    added.setDockParent(this);
                }
            }
        });
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


    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        for (Node child : getChildren()) {
            if (child.isManaged()) {
                child.resizeRelocate(0, 0, getWidth(), getHeight());
            }
        }
    }


    @NonNull
    @Override
    public Parent getNode() {
        return this;
    }

}
