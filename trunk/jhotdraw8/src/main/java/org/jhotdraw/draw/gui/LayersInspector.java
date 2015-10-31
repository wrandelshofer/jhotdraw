/* @(#)LayersInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.StringConverter;
import org.jhotdraw.collection.ReversedList;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.gui.ClipboardIO;
import org.jhotdraw.gui.ListViewUtil;
import org.jhotdraw.util.Resources;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class LayersInspector extends AbstractDrawingInspector {

    @FXML
    private ListView<Figure> listView;
    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    private ObservableList<Figure> layers;

    private Supplier<Layer> layerFactory;

    private ChangeListener<Layer> selectedLayerHandler = new ChangeListener<Layer>() {

        @Override
        public void changed(ObservableValue<? extends Layer> observable, Layer oldValue, Layer newValue) {
            if (newValue != null) {
                listView.getSelectionModel().select(newValue);
            }
        }
    };
    private InvalidationListener listInvalidationListener = new InvalidationListener() {

        @Override
        public void invalidated(Observable observable) {
            if (drawingView!=null)
            drawingView.getModel().fireNodeInvalidated(drawingView.getDrawing());
        }
        
    };

    public LayersInspector() {
        this(LayersInspector.class.getResource("LayersInspector.fxml"));
    }

    public LayersInspector(URL fxmlUrl) {
        this(fxmlUrl, SimpleLayer::new);
    }

    public LayersInspector(URL fxmlUrl, Supplier<Layer> layerFactory) {
        this.layerFactory = layerFactory;
        init(fxmlUrl);
    }

    public LayersInspector(Supplier<Layer> layerFactory) {
        this(LayersInspector.class.getResource("LayersInspector.fxml"), layerFactory);
    }

    private void init(URL fxmlUrl) {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setResources(Resources.getBundle("org.jhotdraw.draw.gui.Labels"));

        try (InputStream in = fxmlUrl.openStream()) {
            setCenter(loader.load(in));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        StringConverter<Layer> uriConverter = new StringConverter<Layer>() {

            @Override
            public String toString(Layer object) {
                return object.toString();
            }

            @Override
            public Layer fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

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
            public void write(Clipboard clipboard, List<Figure> items) {
                if (items.size() != 1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
                ClipboardContent content = new ClipboardContent();
                Figure f = items.get(0);
                String id = f.get(Figure.STYLE_ID);
                content.putString(id == null ? "" : id);
                clipboard.setContent(content);
            }

            @Override
            public List<Figure> read(Clipboard clipboard) {
                List<Figure> list;
                if (clipboard.hasString()) {
                    list = new ArrayList<>();
                    Layer layer = layerFactory.get();
                    layer.set(Figure.STYLE_ID, clipboard.getString());
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
        ListViewUtil.addReorderingSupport(listView, this::createCell, io);
    }

    public ListCell<Figure> createCell(ListView<Figure> listView) {
          StringConverter<Figure> converter = new StringConverter<Figure>() {

            @Override
            public String toString(Figure object) {
                return object.get(Figure.STYLE_ID);
            }

            @Override
            public Figure fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        return new LayerCell(drawingView);
        // return new TextFieldListCell<>(converter);
    }

    protected void onDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {
        if (oldValue != null) {
            oldValue.activeLayerProperty().removeListener(selectedLayerHandler);
        }
        if (newValue != null) {
            newValue.activeLayerProperty().addListener(selectedLayerHandler);
            //listView.setCellFactory(LayerCell.forListView(newValue));
        }

    }

    protected void onDrawingChanged(Drawing oldValue, Drawing newValue) {
        if (oldValue!=null) {
            oldValue.getChildren().removeListener(listInvalidationListener);
        }
        if (newValue != null) {
            layers = new ReversedList<>(newValue.getChildren());
            listView.setItems(layers);
            newValue.getChildren().addListener(listInvalidationListener);
        }
    }
}
