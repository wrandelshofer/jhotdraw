/* @(#)StylesheetsInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.text.StringConverterConverterWrapper;
import org.jhotdraw.text.UriConverter;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class StylesheetsInspector extends AbstractDrawingInspector {

    @FXML
    private ListView<URI> listView;
    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    private ListProperty<URI> stylesheetsProperty;

    /**
     * Counter for incrementing stylesheet names.
     */
    private int counter;

    public StylesheetsInspector() {
        this(StylesheetsInspector.class.getResource("StylesheetsInspector.fxml"));
    }

    public StylesheetsInspector(URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void init(URL fxmlUrl) {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        try (InputStream in = fxmlUrl.openStream()) {
            setCenter(loader.load(in));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
        listView.getItems().addListener((InvalidationListener) (o -> onListChanged()));
        // int counter = 0;
        addButton.addEventHandler(ActionEvent.ACTION, o -> listView.getItems().add(URI.create("stylesheet" + (++counter) + ".css")));
        removeButton.addEventHandler(ActionEvent.ACTION, o -> {
            ObservableList<URI> items = listView.getItems();
            ArrayList<Integer> indices = new ArrayList<>(listView.getSelectionModel().getSelectedIndices());
            Collections.sort(indices);
            for (int i = indices.size() - 1; i >= 0; i--) {
                items.remove((int) indices.get(i));
            }
        });
        removeButton.disableProperty().bind(Bindings.equal(listView.getSelectionModel().selectedIndexProperty(), -1));

        listView.setEditable(true);
        setCellFactory(listView);
        listView.setFixedCellSize(24.0);

        listView.setOnEditCommit(new EventHandler<ListView.EditEvent<URI>>() {
            @Override
            public void handle(ListView.EditEvent<URI> t) {
                listView.getItems().set(t.getIndex(), t.getNewValue());
            }

        });

        
        // XXX move this into a separate class
        
        EventHandler<? super DragEvent> dndHandler = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                EventType<DragEvent> t = event.getEventType();

                boolean isAcceptable = event.getDragboard().hasUrl()
                        || event.getDragboard().hasFiles();

                if (t == DragEvent.DRAG_DROPPED) {
                    boolean success = false;
                    if (isAcceptable) {
                        ListView<?> gestureTargetListView = null;
                        if (event.getGestureSource() instanceof ListCell) {
                            ListCell<?> gestureTargetCell = (ListCell<?>) event.getGestureSource();
                            gestureTargetListView = gestureTargetCell.getListView();
                        }
                        @SuppressWarnings("unchecked")
                        ListView<URI> sourceListView = (ListView<URI>) event.getSource();
                        TransferMode mode = (sourceListView == gestureTargetListView) ? TransferMode.MOVE : TransferMode.COPY;
                        event.acceptTransferModes(mode);

                        // XXX assume fixed cell height
                        double cellHeight = listView.getFixedCellSize();
                        int index = Math.max(0, Math.min((int) (event.getY() / cellHeight), listView.getItems().size()));

                        if (event.getDragboard()
                                .hasUrl()) {
                            URI documentHome = drawingView.getDrawing().get(Drawing.DOCUMENT_HOME);
                            URI dragboardUri = URI.create(event.getDragboard().getUrl());
                            URI stylesheetUri = documentHome.relativize(dragboardUri);
                            sourceListView.getItems().add(index, stylesheetUri);
                            if (index <= draggedCellIndex) {
                                draggedCellIndex++;
                            }
                            success = true;
                        } else if (event.getDragboard().hasFiles()) {
                            URI documentHome = drawingView.getDrawing().get(Drawing.DOCUMENT_HOME);
                            for (File f : event.getDragboard().getFiles()) {
                                URI dragboardUri = f.toURI();
                                URI stylesheetUri = documentHome.relativize(dragboardUri);
                                sourceListView.getItems().add(index++, stylesheetUri);
                                if (index <= draggedCellIndex) {
                                    draggedCellIndex++;
                                }
                            }
                            success = true;
                        }
                    }
                    event.setDropCompleted(success);
                    event.consume();
                } else if (t == DragEvent.DRAG_OVER) {
                    if (isAcceptable) {
                        ListView<?> gestureTargetListView = null;
                        if (event.getGestureSource() instanceof ListCell) {
                            ListCell<?> gestureTargetCell = (ListCell<?>) event.getGestureSource();
                            gestureTargetListView = gestureTargetCell.getListView();
                        }
                        ListView<?> sourceListView = (ListView<?>) event.getSource();
                        TransferMode mode = (sourceListView == gestureTargetListView) ? TransferMode.MOVE : TransferMode.COPY;
                        event.acceptTransferModes(mode);
                    }
                    event.consume();
                }
            }

        };
        listView.addEventHandler(DragEvent.ANY, dndHandler);
    }

    @Override
    protected void onDrawingChanged(Drawing oldValue, Drawing newValue) {
        if (oldValue != null) {
            listView.getItems().clear();
        }
        if (newValue != null) {
            List<URI> stylesheets = newValue.get(Drawing.AUTHOR_STYLESHEETS);
            if (stylesheets == null) {
                listView.getItems().clear();
            } else {
                listView.getItems().setAll(stylesheets);
            }
        }
    }

    private void onListChanged() {
        drawingView.getModel().set(drawingView.getDrawing(), Drawing.AUTHOR_STYLESHEETS, new ArrayList<>(listView.getItems()));
    }

    private int draggedCellIndex;

    private void setCellFactory(ListView<URI> listView) {
        StringConverter<URI> uriConverter = new StringConverterConverterWrapper<>(new UriConverter());
        EventHandler<? super DragEvent> dndHandler = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.isConsumed()) {
                    return;
                }
                EventType<DragEvent> t = event.getEventType();
                if (t == DragEvent.DRAG_DONE) {
                    ListCell<?> cell = (ListCell<?>) event.getSource();
                    if (event.getAcceptedTransferMode() == TransferMode.MOVE) {
                        listView.getItems().remove(draggedCellIndex);
                    }
                    event.consume();
                }
            }
        };

        EventHandler<? super MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    @SuppressWarnings("unchecked")
                    TextFieldListCell<URI> draggedCell = (TextFieldListCell<URI>) event.getSource();
                    draggedCellIndex = draggedCell.getIndex();
                    // XXX we currently only support single selection!!
                    if (!listView.getSelectionModel().isSelected(draggedCell.getIndex())) {
                        return;
                    }

                    Dragboard dragboard = draggedCell.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                    ClipboardContent content = new ClipboardContent();

                    URI stylesheetUri = draggedCell.getItem();
                    URI documentHome = drawingView.getDrawing().get(Drawing.DOCUMENT_HOME);
                    stylesheetUri = documentHome.resolve(stylesheetUri);

                    content.putUrl(stylesheetUri.toString());
                    dragboard.setDragView(draggedCell.snapshot(new SnapshotParameters(), null));
                    dragboard.setContent(content);
                    event.consume();
                }
            }

        };

        listView.setCellFactory((ListView<URI> param) -> {
            TextFieldListCell<URI> cell = new TextFieldListCell<>(uriConverter);
            cell.addEventHandler(DragEvent.ANY, dndHandler);
            cell.addEventHandler(MouseEvent.DRAG_DETECTED, mouseHandler);
            return cell;
        });
    }

}
