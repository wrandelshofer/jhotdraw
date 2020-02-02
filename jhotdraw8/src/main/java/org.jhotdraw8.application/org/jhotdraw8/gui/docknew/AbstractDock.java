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

public abstract class AbstractDock
        extends Control
        implements Dock {
    protected final ObjectProperty<Dock> parentComponent = new SimpleObjectProperty<>();
    protected final ObservableList<DockComponent> children = FXCollections.observableArrayList();
    private final ReadOnlyObjectProperty<Node> content = new ReadOnlyObjectWrapper<>((Node) this).getReadOnlyProperty();

    public AbstractDock() {
        children.addListener(this::onChildrenChanged);
        parentComponent.addListener((o, oldv, newv) -> {
            System.out.println(this + " parent " + oldv + " -> " + newv);
        });
    }

    @Override
    public @NonNull ReadOnlyObjectProperty<Node> contentReadOnlyProperty() {
        return content;
    }

    @NonNull
    @Override
    public ObservableList<DockComponent> getChildComponents() {
        return children;
    }


    @Override
    public Node getContent() {
        return content.get();
    }


    private void onChildrenChanged(ListChangeListener.Change<? extends DockComponent> change) {
        while (change.next()) {
            for (DockComponent removed : change.getRemoved()) {
                if (removed.getParentComponent() == this) {
                    removed.setParentComponent(null);
                }
            }
            for (DockComponent added : change.getAddedSubList()) {
                added.setParentComponent(this);
            }

        }
    }

    @Override
    public @NonNull ObjectProperty<Dock> parentComponentProperty() {
        return parentComponent;
    }
}
