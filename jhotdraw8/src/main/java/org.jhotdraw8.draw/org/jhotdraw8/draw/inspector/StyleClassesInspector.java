/*
 * @(#)StyleClassesInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.key.ObservableWordListKey;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.model.DrawingModelEvent;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.gui.PlatformUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 */
public class StyleClassesInspector extends AbstractSelectionInspector {

    @FXML
    private Button addButton;
    private final Listener<DrawingModelEvent> drawingModelEventListener = change -> {
        DrawingView drawingView = getSubject();
        if (change.getEventType() == DrawingModelEvent.EventType.PROPERTY_VALUE_CHANGED) {
            if ((Key<?>) change.getKey() == StyleableFigure.STYLE_CLASS) {
                if (drawingView != null && drawingView.getSelectedFigures().contains(change.getNode())) {
                    updateListLater();
                }
            }
        }
    };
    private Supplier<Collection<String>> listFactory = FXCollections::observableArrayList;
    @FXML
    private ListView<StyleClassItem> listView;
    @Nullable
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

    @NonNull
    @SuppressWarnings("unchecked")
    private ObservableWordListKey tagsKey = StyleableFigure.STYLE_CLASS;
    @FXML
    private TextField textField;
    private boolean willUpdateList;

    public StyleClassesInspector() {
        this(StyleClassesInspector.class.getResource("StyleClassesInspector.fxml"));
    }

    public StyleClassesInspector(@NonNull URL fxmlUrl) {
        init(fxmlUrl);
    }

    public void addTag(@NonNull String wordList) {
        for (String tagName : wordList.split(" ")) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                tagName = tagName.trim();
                for (Figure f : getSelectedFigures()) {
                    @SuppressWarnings("unchecked")
                    ImmutableList<String> tags = f.get(tagsKey);
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
                        getModel().set(f, tagsKey, ImmutableLists.ofCollection(newTags));
                    }
                }
                updateList();
            }
        }
    }

    @Override
    protected void handleDrawingViewChanged(ObservableValue<? extends DrawingView> observable, @Nullable DrawingView oldValue, @Nullable DrawingView newValue) {
        super.handleDrawingViewChanged(observable, oldValue, newValue);
        if (oldValue != null) {
            oldValue.modelProperty().removeListener(modelListener);
            modelListener.changed(oldValue.modelProperty(), oldValue.getModel(), null);
        }
        if (newValue != null) {
            newValue.modelProperty().removeListener(modelListener);
            modelListener.changed(newValue.modelProperty(), null, newValue.getModel());
        }
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    protected void handleSelectionChanged(Set<Figure> newValue) {
        updateListLater();
    }

    private void init(@NonNull URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene. 
        PlatformUtil.invokeAndWait(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(InspectorLabels.getResources().asResourceBundle());
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

    public void removeTag(@NonNull String wordList) {
        for (String tagName : wordList.split(" ")) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                tagName = tagName.trim();
                for (Figure f : getSelectedFigures()) {
                    @SuppressWarnings("unchecked")
                    ImmutableList<String> tags = f.get(tagsKey);
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
                        getModel().set(f, tagsKey, ImmutableLists.ofCollection(newTags));
                    }
                }
                updateList();
            }
        }
    }

    protected void updateList() {
        Set<Figure> newValue = getSelectedFigures();
        Set<String> union = new HashSet<>();
        Set<String> intersection = new HashSet<>();

        boolean first = true;
        for (Figure f : newValue) {
            ImmutableList<String> tags = f.getNonNull(tagsKey);
            if (first) {
                intersection.addAll(tags.asList());
                first = false;
            } else {
                if (!intersection.isEmpty()) {
                    intersection.retainAll(tags.asList());
                }
            }
            if (!tags.isEmpty()) {
                union.addAll(tags.asList());
            }
        }

        ObservableList<StyleClassItem> items = listView.getItems();
        items.clear();
        List<String> unionList = new ArrayList<>(union);
        Collections.sort(unionList);
        for (String t : unionList) {
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
