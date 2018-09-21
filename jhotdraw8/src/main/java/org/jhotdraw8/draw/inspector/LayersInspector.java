/* @(#)LayersInspector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.ReversedList;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.gui.ClipboardIO;
import org.jhotdraw8.gui.ListViewUtil;
import org.jhotdraw8.gui.PlatformUtil;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LayersInspector extends AbstractDrawingInspector {

    @FXML
    private ListView<Figure> listView;
    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    private ReversedList<Figure> layers;

    private Supplier<Layer> layerFactory;

    private Node node;

    @Nonnull
    private HashMap<Layer, Integer> selectionCount = new HashMap<>();

    @Nullable
    private ChangeListener<Layer> selectedLayerHandler = new ChangeListener<Layer>() {
        @Override
        public void changed(ObservableValue<? extends Layer> observable, Layer oldValue, @Nullable Layer newValue) {
            if (newValue != null) {
                listView.getSelectionModel().select(newValue);
            }
        }
    };
    @Nullable
    private InvalidationListener listInvalidationListener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            // FIXME must perform change via the model so that undo/redo will work
            if (drawingView != null) {
                drawingView.getModel().fireNodeInvalidated(drawingView.getDrawing());
            }
        }
    };
    @Nonnull
    private InvalidationListener selectionInvalidationListener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            onSelectionChanged();
        }
    };

    public LayersInspector() {
        this(LayersInspector.class.getResource("LayersInspector.fxml"));
    }

    public LayersInspector(@Nonnull URL fxmlUrl) {
        this(fxmlUrl, SimpleLayer::new);
    }

    public LayersInspector(@Nonnull URL fxmlUrl, Supplier<Layer> layerFactory) {
        this.layerFactory = layerFactory;
        init(fxmlUrl);
    }

    public LayersInspector(Supplier<Layer> layerFactory) {
        this(LayersInspector.class.getResource("LayersInspector.fxml"), layerFactory);
    }

    private boolean isUpdateSelection;

    private void onSelectionChanged() {
        if (!isUpdateSelection) {
            isUpdateSelection = true;
            Platform.runLater(this::updateSelection);
        }
    }

    private void updateSelection() {
        isUpdateSelection = false;
        Drawing d = drawingView.getDrawing();
        Set<Figure> selection = drawingView.getSelectedFigures();
        HashMap<Figure, Integer> layerToIndex = new HashMap<>();
        List<Figure> children = d.getChildren();
        int[] count = new int[children.size()];
        for (int i = 0, n = children.size(); i < n; i++) {
            layerToIndex.put(children.get(i), i);
        }
        for (Figure f : selection) {
            Layer l = f.getLayer();
            Integer index = layerToIndex.get(l);
            if (index != null) {
                count[index]++;
            }
        }
        for (int i = 0, n = children.size(); i < n; i++) {
            selectionCount.put((Layer) children.get(i), count[i]);
        }
        layers.fireUpdated(0, layers.size());
    }

    private void init(@Nonnull URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene. 
        PlatformUtil.invokeAndWait(() -> {

            FXMLLoader loader = new FXMLLoader();
            loader.setController(this);
            loader.setResources(Labels.getBundle());

            try (InputStream in = fxmlUrl.openStream()) {
                node = loader.load(in);
            } catch (IOException ex) {
                throw new InternalError(ex);
            }

            addButton.addEventHandler(ActionEvent.ACTION, o -> {
                Layer layer = layerFactory.get();
                int index = listView.getSelectionModel().getSelectedIndex();
                if (index < 0) {
                    index = 0;
                }
                Drawing drawing = drawingView.getDrawing();
                DrawingModel model = drawingView.getModel();
                int size = drawing.getChildren().size();
                model.insertChildAt(layer, drawing, size - index);
            });
            removeButton.addEventHandler(ActionEvent.ACTION, o -> {
                ArrayList<Integer> indices = new ArrayList<>(listView.getSelectionModel().getSelectedIndices());
                Drawing drawing = drawingView.getDrawing();
                DrawingModel model = drawingView.getModel();
                for (int i = indices.size() - 1; i >= 0; i--) {
                    model.removeFromParent(layers.get(indices.get(i)));
                }
            });
            removeButton.disableProperty().bind(Bindings.equal(listView.getSelectionModel().selectedIndexProperty(), -1));

            listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Figure>) c -> {
                Layer selected = (Layer) listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    drawingView.setActiveLayer(selected);
                }

            });

            ClipboardIO<Figure> io = new ClipboardIO<Figure>() {

                @Override
                public void write(@Nonnull Clipboard clipboard, List<Figure> items) {
                    if (items.size() != 1) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                    ClipboardContent content = new ClipboardContent();
                    Figure f = items.get(0);
                    String id = f.get(StyleableFigure.ID);
                    content.putString(id == null ? "" : id);
                    clipboard.setContent(content);
                }

                @Nullable
                @Override
                public List<Figure> read(Clipboard clipboard) {
                    List<Figure> list;
                    if (clipboard.hasString()) {
                        list = new ArrayList<>();
                        Layer layer = layerFactory.get();
                        layer.set(StyleableFigure.ID, clipboard.getString());
                        list.add(layer);
                    } else {
                        list = null;
                    }
                    return list;
                }

                @Override
                public boolean canRead(Clipboard clipboard) {
                    return clipboard.hasString();
                }
            };

            listView.setFixedCellSize(24.0);
            listView.setCellFactory(addSelectionLabelDndSupport(listView, this::createCell, io));
            ListViewUtil.addReorderingSupport(listView);
        });
    }

    @Nonnull
    public LayerCell createCell(ListView<Figure> listView) {
        return new LayerCell(drawingView.getModel(), this);
    }

    @Override
    protected void onDrawingViewChanged(@Nullable DrawingView oldValue, @Nullable DrawingView newValue) {
        if (oldValue != null) {
            oldValue.activeLayerProperty().removeListener(selectedLayerHandler);
            oldValue.selectedFiguresProperty().removeListener(selectionInvalidationListener);
        }
        if (newValue != null) {
            newValue.activeLayerProperty().addListener(selectedLayerHandler);
            newValue.selectedFiguresProperty().addListener(selectionInvalidationListener);
        }

    }

    @Override
    protected void onDrawingChanged(@Nullable Drawing oldValue, @Nullable Drawing newValue) {
        if (oldValue != null) {
            oldValue.getChildren().removeListener(listInvalidationListener);
        }
        if (newValue != null) {
            layers = new ReversedList<>(newValue.getChildren());
            listView.setItems(layers);
            newValue.getChildren().addListener(listInvalidationListener);
        }
    }

    @Nullable
    private Callback<ListView<Figure>, ListCell<Figure>> addSelectionLabelDndSupport(@Nonnull ListView<Figure> listView, @Nonnull Callback<ListView<Figure>, LayerCell> cellFactory, ClipboardIO<Figure> clipboardIO
    ) {
        SelectionLabelDnDSupport dndSupport = new SelectionLabelDnDSupport(listView, clipboardIO);
        Callback<ListView<Figure>, ListCell<Figure>> dndCellFactory = lv -> {
            try {
                LayerCell cell = cellFactory.call(lv);
                cell.getSelectionLabel().addEventHandler(MouseEvent.DRAG_DETECTED, dndSupport.cellMouseHandler);
                return cell;
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        };
        listView.addEventHandler(DragEvent.ANY, dndSupport.listDragHandler);
        return dndCellFactory;
    }

    int getSelectionCount(Layer item) {
        Integer value = selectionCount.get(item);
        return value == null ? 0 : value;
    }

    /**
     * Implements DnD support for the selectionLabel. Dragging the
     * selectionLabel to a layer will move the selected items to another layer.
     */
    private class SelectionLabelDnDSupport {

        private final ListView<Figure> listView;
        private int draggedCellIndex;
        private final ClipboardIO<Figure> io;

        public SelectionLabelDnDSupport(ListView<Figure> listView, ClipboardIO<Figure> io) {
            this.listView = listView;
            this.io = io;
        }

        @Nonnull
        private EventHandler<? super MouseEvent> cellMouseHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.isConsumed()) {
                    return;
                }
                if (event.getEventType() == MouseEvent.DRAG_DETECTED) {

                    draggedCellIndex = (int) Math.floor(listView.screenToLocal(0, event.getScreenY()).getY() / listView.getFixedCellSize());
                    if (0 <= draggedCellIndex && draggedCellIndex < listView.getItems().size()) {
                        Label draggedLabel = (Label) event.getSource();
                        Dragboard dragboard = draggedLabel.startDragAndDrop(new TransferMode[]{TransferMode.MOVE});
                        ArrayList<Figure> items = new ArrayList<>();
                        items.add(listView.getItems().get(draggedCellIndex));
                        io.write(dragboard, items);
                        dragboard.setDragView(draggedLabel.snapshot(new SnapshotParameters(), null));

                        // consume the event, so that it won't interfere with dnd of the underlying listview.
                        event.consume();
                    }
                } else {
                    draggedCellIndex = -1;
                }
            }

        };

        @Nonnull
        EventHandler<? super DragEvent> listDragHandler = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.isConsumed()) {
                    return;
                }
                EventType<DragEvent> t = event.getEventType();
                if (t == DragEvent.DRAG_DROPPED) {
                    onDragDropped(event);
                } else if (t == DragEvent.DRAG_OVER) {
                    onDragOver(event);
                }
            }

            private void onDragDropped(@Nonnull DragEvent event) {
                if (isAcceptable(event)) {
                    event.acceptTransferModes(TransferMode.MOVE);

                    // XXX foolishly assumes fixed cell height
                    double cellHeight = listView.getFixedCellSize();
                    List<Figure> items = listView.getItems();
                    int index = Math.max(0, Math.min((int) (event.getY() / cellHeight), items.size()));

                    Figure from = items.get(draggedCellIndex);
                    moveSelectedFiguresFromToLayer((Layer) from, (Layer) items.get(index));
                    event.setDropCompleted(true);
                    event.consume();
                }
            }

            private boolean isAcceptable(DragEvent event) {
                boolean isAcceptable = (event.getGestureSource() instanceof Label)
                        && (((Label) event.getGestureSource()).getParent().getParent() instanceof LayerCell)
                        && ((LayerCell) ((Label) event.getGestureSource()).getParent().getParent()).getListView() == listView;
                return isAcceptable;
            }

            private void onDragOver(@Nonnull DragEvent event) {
                if (isAcceptable(event)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            }

            private void moveSelectedFiguresFromToLayer(Layer from, @Nonnull Layer to) {
                DrawingModel model = drawingView.getModel();
                LinkedHashSet<Figure> selection = new LinkedHashSet<>(drawingView.getSelectedFigures());
                for (Figure f : selection) {
                    if (f instanceof Layer) {
                        continue;
                    }
                    if (f.getLayer() == from) {
                        // addChild child moves a figure, so we do not need to
                        // removeChild it explicitly
                        model.addChildTo(f, to);
                    }
                }

                // Update the selection. The selection still contains the
                // same figures but they have now a different ancestor.
                drawingView.getSelectedFigures().clear();
                drawingView.getSelectedFigures().addAll(selection);
            }

        };
    }

    @Override
    public Node getNode() {
        return node;
    }

}
