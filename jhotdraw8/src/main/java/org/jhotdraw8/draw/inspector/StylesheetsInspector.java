/* @(#)StylesheetsInspector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.StringConverter;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.gui.ClipboardIO;
import org.jhotdraw8.gui.ListViewUtil;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.text.StringConverterAdapter;
import org.jhotdraw8.text.XmlUriConverter;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StylesheetsInspector extends AbstractDrawingInspector {

    @FXML
    private ListView<URI> listView;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button refreshButton;

    private ListProperty<URI> stylesheetsProperty;
    private Node node;
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
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene. 
        PlatformUtil.invokeAndWait(() -> {

            FXMLLoader loader = new FXMLLoader();
            loader.setResources(Labels.getBundle());
            loader.setController(this);
            try (InputStream in = fxmlUrl.openStream()) {
                node = loader.load(in);
            } catch (IOException ex) {
                throw new InternalError(ex);
            }
            listView.getItems().addListener((InvalidationListener) (o -> onListChanged()));
            // int counter = 0;
            addButton.addEventHandler(ActionEvent.ACTION, this::onAddAction);
            removeButton.addEventHandler(ActionEvent.ACTION, this::onRemoveAction);
            removeButton.disableProperty().bind(Bindings.equal(listView.getSelectionModel().selectedIndexProperty(), -1));
            refreshButton.addEventHandler(ActionEvent.ACTION, o -> getDrawingModel().fireStyleInvalidated(getDrawing()));

            listView.setEditable(true);
            listView.setFixedCellSize(24.0);

            listView.setOnEditCommit(new EventHandler<ListView.EditEvent<URI>>() {
                @Override
                public void handle(ListView.EditEvent<URI> t) {
                    listView.getItems().set(t.getIndex(), t.getNewValue());
                }

            });

            ClipboardIO<URI> io = new ClipboardIO<URI>() {

                @Override
                public void write(Clipboard clipboard, List<URI> items) {
                    if (items.size() != 1) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                    ClipboardContent content = new ClipboardContent();
                    URI stylesheetUri = items.get(0);
                    URI documentHome = drawingView.getDrawing().get(Drawing.DOCUMENT_HOME);
                    stylesheetUri = documentHome.resolve(stylesheetUri);

                    content.putUrl(stylesheetUri.toString());
                    clipboard.setContent(content);
                }

                @Override
                public List<URI> read(Clipboard clipboard) {
                    List<URI> list;
                    if (clipboard.hasUrl()) {
                        list = new ArrayList<>();
                        URI documentHome = drawingView.getDrawing().get(Drawing.DOCUMENT_HOME);
                        URI dragboardUri = URI.create(clipboard.getUrl());
                        URI stylesheetUri = documentHome.relativize(dragboardUri);
                        list.add(stylesheetUri);
                    } else if (clipboard.hasFiles()) {
                        list = new ArrayList<>();
                        URI documentHome = drawingView.getDrawing().get(Drawing.DOCUMENT_HOME);
                        for (File f : clipboard.getFiles()) {
                            URI dragboardUri = f.toURI();
                            URI stylesheetUri = documentHome.relativize(dragboardUri);
                            list.add(stylesheetUri);
                        }
                    } else {
                        list = null;
                    }
                    return list;
                }

                @Override
                public boolean canRead(Clipboard clipboard) {
                    return clipboard.hasFiles() || clipboard.hasUrl();
                }

            };
            StringConverter<URI> uriConverter = new StringConverterAdapter<>(new XmlUriConverter());
            ListViewUtil.addDragAndDropSupport(listView, (ListView<URI> param)
                    -> new TextFieldListCell<>(uriConverter), io);
        });
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

    @Override
    public Node getNode() {
        return node;
    }

    private void onRemoveAction(ActionEvent event) {
        ObservableList<URI> items = listView.getItems();
        ArrayList<Integer> indices = new ArrayList<>(listView.getSelectionModel().getSelectedIndices());
        Collections.sort(indices);
        for (int i = indices.size() - 1; i >= 0; i--) {
            items.remove((int) indices.get(i));
        }
    }

    private void onAddAction(ActionEvent event) {
        listView.getItems().add(URI.create("stylesheet" + (++counter) + ".css"));
    }

}
