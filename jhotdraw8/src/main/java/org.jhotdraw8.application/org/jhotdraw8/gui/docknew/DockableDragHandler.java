package org.jhotdraw8.gui.docknew;

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

import java.util.HashMap;
import java.util.Map;

import static org.jhotdraw8.gui.docknew.DockPane.DOCKABLE_DATA_FORMAT;

class DockableDragHandler {
    private Map<Dockable, DragHandlerImpl> map = new HashMap<>();

    public void add(Dockable d) {
        map.computeIfAbsent(d, DragHandlerImpl::new);
    }

    public void remove(Dockable d) {
        map.computeIfPresent(d, (k, h) -> h.unregister());
    }


    private static class DragHandlerImpl {
        private final Dockable dockable;
        private final ChangeListener<Node> graphicChangedListener = this::onGraphicChanged;

        private DragHandlerImpl(Dockable dockable) {
            this.dockable = dockable;
            dockable.graphicProperty().addListener(graphicChangedListener);
            onGraphicChanged(dockable.graphicProperty(), null, dockable.getGraphic());
        }

        public DragHandlerImpl unregister() {
            dockable.graphicProperty().removeListener(graphicChangedListener);
            onGraphicChanged(dockable.graphicProperty(), dockable.getGraphic(), null);
            return null;
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
            DockPane.setDraggedDockable(null);
        }

        private void onDragDetected(@NonNull MouseEvent e) {
            Node graphic = dockable.getGraphic();
            DockPane.setDraggedDockable(dockable);
            Dragboard db = graphic.startDragAndDrop(TransferMode.MOVE);

            db.setDragView(
                    (graphic.getParent() == null ? graphic : graphic.getParent()).snapshot(null, null),
                    e.getX(), e.getY());
            ClipboardContent content = new ClipboardContent();
            content.put(DOCKABLE_DATA_FORMAT, System.identityHashCode(this));
            db.setContent(content);

            e.consume();
        }

    }
}
