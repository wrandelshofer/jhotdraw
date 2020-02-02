package org.jhotdraw8.gui.docknew;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

import java.util.prefs.Preferences;

public class SimpleDockItem extends Tab implements DockItem {
    public SimpleDockItem(Node content) {
        this(null, null, content);
    }

    public SimpleDockItem(String text, Node content) {
        this(text, text, content);
    }

    public SimpleDockItem(String id, String text, Node content) {
        setId(id);
        setText(text);
        setContent(content);
        graphicProperty().addListener(this::onGraphicChanged);
        final Text textualIcon = new Text("❏");
        textualIcon.getStyleClass().add(DOCK_LEAF_ICON_STYLE_CLASS);
        setGraphic(textualIcon);
    }

    @Override
    public ReadOnlyList<DockComponent> getChildComponentsReadOnly() {
        return ImmutableLists.emptyList();
    }

    @Override
    public @NonNull ReadOnlyObjectProperty<Node> contentReadOnlyProperty() {
        return contentProperty();
    }

    private void onSelectionChanged(Observable o, boolean oldv, boolean newv) {
        Preferences prefs = Preferences.userNodeForPackage(SimpleDockItem.class);
        prefs.putBoolean(getId() + ".selected", newv);
    }

    private void onGraphicChanged(Observable o, @Nullable Node oldv, @Nullable Node newv) {
        if (oldv != null) {
            oldv.setOnDragDetected(null);
            oldv.setOnDragDone(null);
        }
        if (newv != null) {
            newv.setOnDragDetected(this::onDragDetected);
            newv.setOnDragDone(this::onDragDone);
        }
    }

    private void onDragDone(DragEvent e) {
        DockItem.setDraggedItem(null);
    }

    private void onDragDetected(@NonNull MouseEvent e) {
        Node graphic = getGraphic();
        DockItem.setDraggedItem(this);
        Dragboard db = graphic.startDragAndDrop(TransferMode.MOVE);

        db.setDragView(
                (graphic.getParent() == null ? graphic : graphic.getParent()).snapshot(null, null),
                e.getX(), e.getY());
        ClipboardContent content = new ClipboardContent();
        content.put(DRAGGED_LEAF_DATA_FORMAT, System.identityHashCode(this));
        db.setContent(content);

        e.consume();
    }

    protected final ObjectProperty<Dock> parentComponent = new SimpleObjectProperty<>();

    {
        ChangeListener<Object> changeListener = (o, oldv, newv) -> {
            System.out.println(this + " parentC " + oldv + " -> " + newv);
        };
        parentComponent.addListener(changeListener);
    }


    @Override
    public @NonNull ObjectProperty<Dock> parentComponentProperty() {
        return parentComponent;
    }

}
