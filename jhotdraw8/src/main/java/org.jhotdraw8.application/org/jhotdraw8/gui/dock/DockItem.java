/*
 * @(#)DockItem.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.prefs.Preferences;

/**
 * Represents an item which can be added to a {@code Dock}.
 * <p>
 * Shows a textual icon on the tab.
 * The textual icon is represented by a {@link Text} object
 * with the style class {@value #DOCK_ITEM_ICON_STYLE_CLASS}.
 *
 * @author Werner Randelshofer
 */
public class DockItem extends Tab {
    public final static String DOCK_ITEM_ICON_STYLE_CLASS = "dock-item-icon";
    @Nullable
    static DockItem draggedTab;
    private final ObjectProperty<Dock> dock = new SimpleObjectProperty<>();
    public final static DataFormat DOCKABLE_TAB_FORMAT = new DataFormat("application/x-java-dockabletab");

    public DockItem() {
        this(null, null, null);
    }

    public DockItem(Node content) {
        this(null, null, content);
    }

    public DockItem(String text, Node content) {
        this(text, text, content);
    }

    public DockItem(String id, String text, Node content) {
        super(text, content);
        setId(id);
        graphicProperty().addListener(this::graphicChanged);
        final Text textualIcon = new Text("❏");
        textualIcon.getStyleClass().add(DOCK_ITEM_ICON_STYLE_CLASS);
        setGraphic(textualIcon);
        setClosable(false);
        getStyleClass().add("dockItem");
        selectedProperty().addListener(this::selectionChanged);
    }

    @NonNull
    public ObjectProperty<Dock> dockProperty() {
        return dock;
    }

    public Dock getDock() {
        return dock.get();
    }

    public void setDock(Dock value) {
        dock.set(value);
    }

    private void selectionChanged(Observable o, boolean oldv, boolean newv) {
        Preferences prefs = Preferences.userNodeForPackage(DockItem.class);
        prefs.putBoolean(getId() + ".selected", newv);
    }

    private void graphicChanged(Observable o, @Nullable Node oldv, @Nullable Node newv) {
        if (oldv != null) {
            oldv.setOnDragDetected(null);
            oldv.setOnDragDone(null);
        }
        if (newv != null) {
            newv.setOnDragDetected(this::onDragDetected);
            newv.setOnDragDone(this::onDragDone);
        }
    }

    public void onDragDone(DragEvent e) {
        draggedTab = null;
    }

    public void onDragDetected(@NonNull MouseEvent e) {
        Node graphic = getGraphic();
        draggedTab = this;
        Dragboard db = graphic.startDragAndDrop(TransferMode.MOVE);

        db.setDragView(
                (graphic.getParent() == null ? graphic : graphic.getParent()).snapshot(null, null),
                e.getX(), e.getY());
        ClipboardContent content = new ClipboardContent();
        content.put(DOCKABLE_TAB_FORMAT, System.identityHashCode(this));
        db.setContent(content);

        e.consume();
    }
}
