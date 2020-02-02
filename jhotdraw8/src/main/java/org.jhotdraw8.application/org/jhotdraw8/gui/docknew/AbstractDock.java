package org.jhotdraw8.gui.docknew;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.collection.ReadOnlyListWrapper;

public abstract class AbstractDock
        extends Control
        implements Dock {
    private final ReadOnlyObjectProperty<Node> content = new ReadOnlyObjectWrapper<>((Node) this).getReadOnlyProperty();
    protected final ObjectProperty<Dock> parentComponent = new SimpleObjectProperty<>();

    {
        ChangeListener<DockComponent> changeListener = (o, oldv, newv) -> {
            System.out.println(this + " parentC " + oldv + " -> " + newv);
        };
        parentComponent.addListener(changeListener);
    }

    protected final ObservableList<DockComponent> children = FXCollections.observableArrayList();

    @Override
    public ReadOnlyList<DockComponent> getChildComponentsReadOnly() {
        return new ReadOnlyListWrapper<>(children);
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

    @Override
    public @NonNull ReadOnlyObjectProperty<Node> contentReadOnlyProperty() {
        return content;
    }

    @Override
    public @NonNull ObjectProperty<Dock> parentComponentProperty() {
        return parentComponent;
    }
}
