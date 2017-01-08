/* @(#)StyleClassesInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.inspector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.DrawingModelEvent;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.text.CssObservableWordListConverter;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class StyleClassesInspector extends AbstractSelectionInspector {

    @FXML
    private Button addButton;
    private final Listener<DrawingModelEvent> drawingModelEventListener = change -> {
        if (change.getEventType() == DrawingModelEvent.EventType.PROPERTY_VALUE_CHANGED) {
            if ((Key<?>) change.getKey() == StyleableFigure.STYLE_CLASS) {
                if (drawingView != null && drawingView.getSelectedFigures().contains(change.getFigure())) {
                    updateListLater();
                }
            }
        }
    };
    private Supplier<Collection<String>> listFactory = FXCollections::observableArrayList;
    @FXML
    private ListView<StyleClassItem> listView;
    private final ChangeListener<DrawingModel> modelListener = (o, oldv, newv) -> {
        if (oldv != null) {
            oldv.removeDrawingModelListener(drawingModelEventListener);
        }
        if (newv != null) {
            newv.addDrawingModelListener(drawingModelEventListener);
        }
    };
    private Node node;

    @FXML
    private Button removeButton;

    @SuppressWarnings("unchecked")
    private Key<Collection<String>> tagsKey = (Key<Collection<String>>) (Key<?>) StyleableFigure.STYLE_CLASS;
    @FXML
    private TextField textField;
    private boolean willUpdateList;

    public StyleClassesInspector() {
        this(StyleClassesInspector.class.getResource("StyleClassesInspector.fxml"));
    }

    public StyleClassesInspector(URL fxmlUrl) {
        init(fxmlUrl);
    }

    public void addTag(String wordList) {
        for (String tagName : wordList.split(" ")) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                tagName = tagName.trim();
                for (Figure f : getSelectedFigures()) {
                    @SuppressWarnings("unchecked")
                    Collection<String> tags = f.get(tagsKey);
                    Collection<String> newTags = listFactory.get();
                    boolean contains = false;
                    for (String t : tags) {
                        if (tagName.equals(t)) {
                            contains = true;
                        }
                        newTags.add(t);
                    }
                    if (!contains) {
                        newTags.add(tagName);
                        getDrawingModel().set(f, tagsKey, new ImmutableObservableList<>(newTags));
                    }
                }
                updateList();
            }
        }
    }

    @Override
    public void setDrawingView(DrawingView newValue) {
        DrawingView oldValue = drawingView;
        super.setDrawingView(newValue);
        if (oldValue != null) {
            oldValue.modelProperty().removeListener(modelListener);
            modelListener.changed(oldValue.modelProperty(), oldValue.getModel(), null);
        }
        this.drawingView = newValue;
        if (newValue != null) {
            newValue.modelProperty().removeListener(modelListener);
            modelListener.changed(newValue.modelProperty(), null, newValue.getModel());
        }
        handleDrawingViewChanged(oldValue, newValue);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    protected void handleSelectionChanged(Set<Figure> newValue) {
        updateList();
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

            listView.setCellFactory(StyleClassCell.forListView(this));
            // selection does not actually have a meaning
            listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            textField.addEventHandler(ActionEvent.ACTION,
                    o -> addTag(textField.getText())
            );

            addButton.addEventHandler(ActionEvent.ACTION,
                    o -> addTag(textField.getText())
            );
            removeButton.addEventHandler(ActionEvent.ACTION,
                    o -> removeTag(textField.getText())
            );
        });
    }

    public void removeTag(String wordList) {
        for (String tagName : wordList.split(" ")) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                tagName = tagName.trim();
                for (Figure f : getSelectedFigures()) {
                    @SuppressWarnings("unchecked")
                    Collection<String> tags = f.get(tagsKey);
                    Collection<String> newTags = listFactory.get();
                    boolean contains = false;
                    for (String t : tags) {
                        if (tagName.equals(t)) {
                            contains = true;
                        } else {
                            newTags.add(t);
                        }
                    }
                    if (contains) {
                        getDrawingModel().set(f, tagsKey, new ImmutableObservableList<>(newTags));
                    }
                }
                updateList();
            }
        }
    }

    protected void updateList() {
        Set<Figure> newValue = getSelectedFigures();
        Set<String> union = new TreeSet<>(CssObservableWordListConverter.NFD_COMPARATOR);
        Set<String> intersection = new HashSet<>();

        boolean first = true;
        for (Figure f : newValue) {
            @SuppressWarnings("unchecked")
            Collection<String> tags = f.get(tagsKey);
            if (first) {
                intersection.addAll(tags);
                first = false;
            } else {
                intersection.retainAll(tags);
            }
            union.addAll(tags);
        }

        ObservableList<StyleClassItem> items = listView.getItems();
        items.clear();
        for (String t : union) {
            items.add(new StyleClassItem(t, intersection.contains(t)));
        }
    }

    protected void updateListLater() {
        if (!willUpdateList) {
            willUpdateList = true;
            Platform.runLater(this::updateListNow);
        }
    }

    protected void updateListNow() {
        willUpdateList = false;
        updateList();
    }

}