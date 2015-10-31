/* @(#)ListViewUtil.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

/**
 * ListViewUtil.
 *
 * @author Werner Randelshofer
 */
public class ListViewUtil {

    private static class DnDSupport<T> {

        private final ListView<T> listView;
        private int draggedCellIndex;
        private final ClipboardIO<T> io;
        private boolean reorderingOnly;

        public DnDSupport(ListView<T> listView, ClipboardIO<T> io, boolean reorderingOnly) {
            this.listView = listView;
            this.io = io;
            this.reorderingOnly = reorderingOnly;
        }

        private EventHandler<? super DragEvent> cellDragHandler = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                EventType<DragEvent> t = event.getEventType();
                if (t == DragEvent.DRAG_DONE) {
                    onDragDone(event);
                }
            }

            private void onDragDone(DragEvent event) {
                if (reorderingOnly) {
                    // XXX assumes that the list autodetects reordering!
                    event.consume();
                    return;
                }

                ListCell<?> cell = (ListCell<?>) event.getSource();
                if (event.getAcceptedTransferMode() == TransferMode.MOVE) {
                    listView.getItems().remove(draggedCellIndex);
                }
                event.consume();
            }

        };

        private EventHandler<? super MouseEvent> cellMouseHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    @SuppressWarnings("unchecked")
                    ListCell<T> draggedCell = (ListCell<T>) event.getSource();
                    draggedCellIndex = draggedCell.getIndex();
                    // XXX we currently only support single selection!!
                    if (!listView.getSelectionModel().isSelected(draggedCell.getIndex())) {
                        return;
                    }

                    Dragboard dragboard = draggedCell.startDragAndDrop(reorderingOnly ? new TransferMode[]{TransferMode.MOVE} : TransferMode.COPY_OR_MOVE);
                    ArrayList<T> items = new ArrayList<>();
                    items.add(draggedCell.getItem());
                    io.write(dragboard, items);
                    dragboard.setDragView(draggedCell.snapshot(new SnapshotParameters(), null));
                    event.consume();
                }
            }

        };

        EventHandler<? super DragEvent> listDragHandler = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                EventType<DragEvent> t = event.getEventType();
                if (t == DragEvent.DRAG_DROPPED) {
                    onDragDropped(event);
                } else if (t == DragEvent.DRAG_OVER) {
                    onDragOver(event);
                }
            }

            private TransferMode acceptMode(DragEvent event) {
                ListView<?> gestureTargetListView = null;
                if (event.getGestureSource() instanceof ListCell) {
                    ListCell<?> gestureTargetCell = (ListCell<?>) event.getGestureSource();
                    gestureTargetListView = gestureTargetCell.getListView();
                }
                TransferMode mode;
                if (reorderingOnly) {
                    mode = (listView == gestureTargetListView) ? TransferMode.MOVE : null;
                } else {
                    mode = (listView == gestureTargetListView) ? TransferMode.MOVE : TransferMode.COPY;
                }

                if (mode == null) {
                    event.acceptTransferModes(TransferMode.NONE);
                } else {
                    event.acceptTransferModes(mode);
                }
                return mode;
            }

            private void onDragDropped(DragEvent event) {
                boolean isAcceptable = io.canRead(event.getDragboard());
                boolean success = false;
                if (isAcceptable) {
                    TransferMode mode = acceptMode(event);

                    // XXX foolishly assumes fixed cell height
                    double cellHeight = listView.getFixedCellSize();
                    int index = Math.max(0, Math.min((int) (event.getY() / cellHeight), listView.getItems().size()));

                    if (reorderingOnly) {
                        // FIXME only supports single item drag
                        T item = listView.getItems().get(draggedCellIndex);
                        listView.getItems().add(index, item);
                        success = true;
                    } else {

                        List<T> items = io.read(event.getDragboard());
                        success = items != null;
                        if (success) {
                            for (T item : items) {
                                listView.getItems().add(index, item);
                                if (index <= draggedCellIndex) {
                                    draggedCellIndex++;
                                }
                                index++;
                            }
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }

            private void onDragOver(DragEvent event) {
                boolean isAcceptable = io.canRead(event.getDragboard());
                if (isAcceptable) {
                    acceptMode(event);
                }
                event.consume();
            }
        };
    }

    /**
     * Adds drag and drop support to the list view
     *
     * @param <T> the data type of the list view
     * @param listView the list view
     * @param clipboardIO a reader/writer for the clipboard.
     */
    public static <T> void addDragAndDropSupport(ListView<T> listView, ClipboardIO<T> clipboardIO) {
        addDragAndDropSupport(listView, listView.getCellFactory(), clipboardIO);
    }

    /**
     * Adds drag and drop support to the list view
     *
     * FIXME should also add support for cut, copy and paste keys
     *
     * @param <T> the data type of the list view
     * @param listView the list view
     * @param cellFactory the cell factory of the list view
     * @param clipboardIO a reader/writer for the clipboard.
     */
    public static <T> void addDragAndDropSupport(ListView<T> listView, Callback<ListView<T>, ListCell<T>> cellFactory, ClipboardIO<T> clipboardIO) {
        addDragAndDropSupport(listView, cellFactory, clipboardIO, false);
    }

    private static <T> void addDragAndDropSupport(ListView<T> listView, Callback<ListView<T>, ListCell<T>> cellFactory, ClipboardIO<T> clipboardIO,
            boolean reorderingOnly) {
        DnDSupport<T> dndSupport = new DnDSupport<T>(listView, clipboardIO, reorderingOnly);
        Callback<ListView<T>, ListCell<T>> dndCellFactory = lv -> {
            try {
                ListCell<T> cell = cellFactory.call(lv);
                cell.addEventHandler(DragEvent.ANY, dndSupport.cellDragHandler);
                cell.addEventHandler(MouseEvent.DRAG_DETECTED, dndSupport.cellMouseHandler);
                return cell;
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        };
        listView.setCellFactory(dndCellFactory);
        listView.addEventHandler(DragEvent.ANY, dndSupport.listDragHandler);
    }

    /**
     * Adds reordering support to the list view
     *
     * @param <T> the data type of the list view
     * @param listView the list view
     * @param clipboardIO the clipboard i/o 
     */
    public static <T> void addReorderingSupport(ListView<T> listView, ClipboardIO<T> clipboardIO) {
        addReorderingSupport(listView, listView.getCellFactory(), clipboardIO);
    }

    /**
     * Adds drag and drop support to the list view
     *
     * FIXME should also add support for cut, copy and paste keys
     *
     * @param <T> the data type of the list view
     * @param listView the list view
     * @param cellFactory the cell factory of the list view
     * @param clipboardIO a reader/writer for the clipboard.
     */
    public static <T> void addReorderingSupport(ListView<T> listView, Callback<ListView<T>, ListCell<T>> cellFactory, ClipboardIO<T> clipboardIO) {
        addDragAndDropSupport(listView, cellFactory, clipboardIO, true);
    }
}
