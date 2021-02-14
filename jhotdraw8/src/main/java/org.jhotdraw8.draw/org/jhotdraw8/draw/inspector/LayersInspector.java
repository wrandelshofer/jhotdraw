/*
 * @(#)LayersInspector.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ReversedObservableList;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LayerFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.DrawingModelFigureChildrenObservableList;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.gui.ClipboardIO;
import org.jhotdraw8.gui.ListViewUtil;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.tree.TreeModelEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 */
public class LayersInspector extends AbstractDrawingInspector {

    @FXML
    private ListView<Figure> listView;
    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    private @Nullable ReversedObservableList<Figure> layers;

    private Supplier<Layer> layerFactory;

    private Node node;

    private @NonNull HashMap<Layer, Integer> selectionCount = new HashMap<>();

    private @Nullable ChangeListener<Figure> selectedLayerHandler = new ChangeListener<Figure>() {
        int changedRecursion = 0;

        @Override
        public void changed(ObservableValue<? extends Figure> observable, Figure oldValue, @Nullable Figure newValue) {
            if (changedRecursion++ == 0) {
                if (newValue != null) {
                    listView.getSelectionModel().select(newValue);
                }
            }
            changedRecursion--;
        }
    };
    private @Nullable Listener<TreeModelEvent<Figure>> listInvalidationListener = new Listener<TreeModelEvent<Figure>>() {
        @Override
        public void handle(@NonNull TreeModelEvent<Figure> event) {
            boolean fire = false;
            Figure root = event.getSource().getRoot();
            switch (event.getEventType()) {

            case ROOT_CHANGED:
                fire = true;
                break;
            case SUBTREE_NODES_CHANGED:
                    if (event.getNode() == root) {
                        fire = true;
                    }
                    break;
                case NODE_ADDED_TO_PARENT:
                case NODE_REMOVED_FROM_PARENT:
                    if (event.getParent() == root) {
                        fire = true;
                    }
                    break;
                case NODE_ADDED_TO_TREE:
                case NODE_REMOVED_FROM_TREE:
                case NODE_CHANGED:
                    break;
            }

            // FIXME why do we call fireNodeInvalidated? The model should do this for us???
            // FIXME must perform change via the model so that undo/redo will work
            if (fire && subject != null) {
                getModel().fireNodeInvalidated(getDrawing());
            }
        }
    };


    private @NonNull InvalidationListener selectionInvalidationListener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            onSelectionChanged();
        }
    };

    public LayersInspector() {
        this(LayersInspector.class.getResource("LayersInspector.fxml"));
    }

    public LayersInspector(@NonNull URL fxmlUrl) {
        this(fxmlUrl, LayerFigure::new);
    }

    public LayersInspector(@NonNull URL fxmlUrl, Supplier<Layer> layerFactory) {
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
        Drawing d = getDrawing();
        Set<Figure> selection = getSubject().getSelectedFigures();
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
        if (layers != null)
            layers.fireUpdated(0, layers.size());
    }

    private void init(@NonNull URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene.
        PlatformUtil.invokeAndWait(() -> {

            FXMLLoader loader = new FXMLLoader();
            loader.setController(this);
            loader.setResources(InspectorLabels.getResources().asResourceBundle());

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
                Drawing drawing = getDrawing();
                DrawingModel model = getModel();
                int size = drawing.getChildren().size();
                model.insertChildAt(layer, drawing, size - index);
            });
            removeButton.addEventHandler(ActionEvent.ACTION, o -> {
                ArrayList<Integer> indices = new ArrayList<>(listView.getSelectionModel().getSelectedIndices());
                Drawing drawing = getDrawing();
                DrawingModel model = getModel();
                for (int i = indices.size() - 1; i >= 0; i--) {
                    model.removeFromParent(layers.get(indices.get(i)));
                }
            });
            removeButton.disableProperty().bind(Bindings.equal(listView.getSelectionModel().selectedIndexProperty(), -1));

            listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Figure>) c -> {
                Layer selected = (Layer) listView.getSelectionModel().getSelectedItem();
                DrawingView subject = getSubject();
                if (selected != null && subject != null) {
                    subject.setActiveParent(selected);
                }

            });

            ClipboardIO<Figure> io = new ClipboardIO<Figure>() {

                @Override
                public void write(@NonNull Clipboard clipboard, @NonNull List<Figure> items) {
                    if (items.size() != 1) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                    ClipboardContent content = new ClipboardContent();
                    Figure f = items.get(0);
                    String id = f.get(StyleableFigure.ID);
                    content.putString(id == null ? "" : id);
                    clipboard.setContent(content);
                }

                @Override
                public @Nullable List<Figure> read(@NonNull Clipboard clipboard) {
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
                public boolean canRead(@NonNull Clipboard clipboard) {
                    return clipboard.hasString();
                }
            };

            listView.setFixedCellSize(24.0);
            listView.setCellFactory(addSelectionLabelDndSupport(listView, this::createCell, io));
            ListViewUtil.addReorderingSupport(listView);
        });
    }

    public @NonNull LayerCell createCell(ListView<Figure> listView) {
        return new LayerCell(getModel(), this);
    }

    protected void onDrawingViewChanged(ObservableValue<? extends DrawingView> observable, @Nullable DrawingView oldValue, @Nullable DrawingView newValue) {
        super.onDrawingViewChanged(observable, oldValue, newValue);
        if (oldValue != null) {
            oldValue.activeParentProperty().removeListener(selectedLayerHandler);
            oldValue.selectedFiguresProperty().removeListener(selectionInvalidationListener);
        }
        if (newValue != null) {
            newValue.activeParentProperty().addListener(selectedLayerHandler);
            newValue.selectedFiguresProperty().addListener(selectionInvalidationListener);
        }

    }

    @Override
    protected void onDrawingChanged(ObservableValue<? extends Drawing> observable, @Nullable Drawing oldValue, @Nullable Drawing newValue) {
        if (oldValue != null) {
            listView.setItems(FXCollections.observableArrayList());
            if (layers != null) {
                layers = null;
            }
        }
        if (newValue != null && newValue.getRoot() != null && drawingModel != null) {
            layers = new ReversedObservableList<Figure>(
                    new DrawingModelFigureChildrenObservableList(drawingModel, newValue));
            listView.setItems(layers);
        }
    }

    @Override
    protected void onDrawingModelChanged(ObservableValue<? extends DrawingModel> observable, @Nullable DrawingModel oldValue, @Nullable DrawingModel newValue) {
        if (oldValue != null) {
            oldValue.removeTreeModelListener(listInvalidationListener);
        }
        if (newValue != null) {
            newValue.addTreeModelListener(listInvalidationListener);
        }
    }

    private @NonNull Callback<ListView<Figure>, ListCell<Figure>> addSelectionLabelDndSupport(
            @NonNull ListView<Figure> listView, @NonNull Callback<ListView<Figure>, LayerCell> cellFactory, ClipboardIO<Figure> clipboardIO
    ) {
        SelectionLabelDnDSupport dndSupport = new SelectionLabelDnDSupport(listView, clipboardIO);
        Callback<ListView<Figure>, ListCell<Figure>> dndCellFactory = lv -> {
            try {
                LayerCell cell = cellFactory.call(lv);
                cell.getSelectionLabel().addEventHandler(MouseEvent.DRAG_DETECTED, dndSupport.cellMouseHandler);
                cell.addEventHandler(DragEvent.ANY, dndSupport.cellDragHandler);
                return cell;
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        };
        //listView.addEventHandler(DragEvent.ANY, dndSupport.listDragHandler);
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

        private @NonNull EventHandler<? super MouseEvent> cellMouseHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(@NonNull MouseEvent event) {
                if (event.isConsumed()) {
                    return;
                }
                if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    Label draggedLabel = (Label) event.getSource();
                    Node parent = draggedLabel;
                    while (parent != null && !(parent instanceof LayerCell)) {
                        parent = parent.getParent();
                    }
                    if (parent == null) {
                        return;
                    }
                    LayerCell cell = (LayerCell) parent;
                    draggedCellIndex = cell.getIndex();
                    if (0 <= draggedCellIndex && draggedCellIndex < listView.getItems().size()) {
                        Dragboard dragboard = draggedLabel.startDragAndDrop(TransferMode.MOVE);
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

        @NonNull
        EventHandler<? super DragEvent> cellDragHandler = new EventHandler<DragEvent>() {

            @Override
            public void handle(@NonNull DragEvent event) {
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

            private void onDragDropped(@NonNull DragEvent event) {
                if (isAcceptable(event)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    List<Figure> items = listView.getItems();

                    LayerCell source = (LayerCell) event.getSource();
                    int droppedCellIndex = source.getIndex();
                    Figure to = droppedCellIndex >= 0 && droppedCellIndex < items.size() ? items.get(droppedCellIndex) : null;
                    Figure from = draggedCellIndex >= 0 && draggedCellIndex < items.size() ? items.get(draggedCellIndex) : null;
                    if (to != null && from != null) {
                        moveSelectedFiguresFromToLayer((Layer) from, (Layer) to);
                        event.setDropCompleted(true);
                    } else {
                        event.setDropCompleted(false);
                    }
                    event.consume();

                }
            }

            private boolean isAcceptable(@NonNull DragEvent event) {
                boolean isAcceptable = (event.getGestureSource() instanceof Label)
                        && (((Label) event.getGestureSource()).getParent().getParent() instanceof LayerCell)
                        && ((LayerCell) ((Label) event.getGestureSource()).getParent().getParent()).getListView() == listView;
                return isAcceptable;
            }

            private void onDragOver(@NonNull DragEvent event) {
                if (isAcceptable(event)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            }


        };
    }

    protected void moveSelectedFiguresFromToLayer(Layer from, @NonNull Layer to) {
        DrawingModel model = getModel();
        DrawingView view = getSubject();
        LinkedHashSet<Figure> selection = new LinkedHashSet<>(view.getSelectedFigures());
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
        view.getSelectedFigures().clear();
        view.getSelectedFigures().addAll(selection);
    }

    @Override
    public Node getNode() {
        return node;
    }

}
