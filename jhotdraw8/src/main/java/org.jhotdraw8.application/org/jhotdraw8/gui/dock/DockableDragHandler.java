/*
 * @(#)DockableDragHandler.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import static org.jhotdraw8.gui.dock.DockRoot.DOCKABLE_DATA_FORMAT;

class DockableDragHandler {

    private final DraggableDockChild dockable;
    private final ChangeListener<Node> graphicChangedListener = this::onGraphicChanged;

    public DockableDragHandler(DraggableDockChild dockable) {
        this.dockable = dockable;
        dockable.graphicProperty().addListener(graphicChangedListener);
        onGraphicChanged(dockable.graphicProperty(), null, dockable.getGraphic());
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
        DockRoot.setDraggedDockable(null);
    }

    private void onDragDetected(@NonNull MouseEvent e) {
        if (dockable.getDockRoot() == null) {
            return;
        }

        Node graphic = dockable.getGraphic();
        DockRoot.setDraggedDockable(dockable);
        Dragboard db = graphic.startDragAndDrop(TransferMode.MOVE);

        db.setDragView(
                (graphic.getParent() == null ? graphic : graphic.getParent()).snapshot(null, null),
                e.getX(), e.getY());
        ClipboardContent content = new ClipboardContent();
        content.put(DOCKABLE_DATA_FORMAT, System.identityHashCode(this));
        db.setContent(content);

        e.consume();
    }

    public void dispose() {
        dockable.graphicProperty().removeListener(graphicChangedListener);
        Node oldv = dockable.getGraphic();
        if (oldv != null) {
            oldv.setOnDragDetected(null);
            oldv.setOnDragDone(null);
        }
    }
}

