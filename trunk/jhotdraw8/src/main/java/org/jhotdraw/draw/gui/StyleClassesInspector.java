/* @(#)StyleClassesInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
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
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.StyleableFigure;
import org.jhotdraw.gui.PlatformUtil;
import org.jhotdraw.util.Resources;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class StyleClassesInspector extends AbstractSelectionInspector {

    private Node node;
    @FXML
    private ListView<StyleClassItem> listView;
    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;
    @FXML
    private TextField textField;

    private Supplier<Collection<String>> listFactory = FXCollections::observableArrayList;

    @SuppressWarnings("unchecked")
    private Key<Collection<String>> tagsKey = (Key<Collection<String>>) (Key<?>) StyleableFigure.STYLE_CLASS;

    public StyleClassesInspector() {
        this(StyleClassesInspector.class.getResource("StyleClassesInspector.fxml"));
    }

    public StyleClassesInspector(URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void init(URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene. 
        PlatformUtil.invokeAndWait(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(Resources.getBundle("org.jhotdraw.draw.gui.Labels"));
            loader.setController(this);

            try (InputStream in = fxmlUrl.openStream()) {
                node = loader.load(in);
            } catch (IOException ex) {
                throw new InternalError(ex);
            }

            listView.setCellFactory(StyleClassCell.forListView(this));
            // selection does not actually have a meaning
            listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            addButton.addEventHandler(ActionEvent.ACTION, o -> 
                addTag( textField.getText())
            );
            removeButton.addEventHandler(ActionEvent.ACTION, o -> 
                removeTag( textField.getText())
            );
        });
    }

    @Override
    protected void handleSelectionChanged(Set<Figure> newValue) {
        updateList();
    }

    protected void updateList() {
        Set<Figure> newValue = getSelectedFigures();
        Set<String> union = new TreeSet<>();
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

    @Override
    public Node getNode() {
        return node;
    }

   public void addTag(String wordList) {
       for (String tagName:wordList.split(" ")){
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
                            getDrawingModel().set(f, tagsKey, newTags);
                        }
                    }
                    updateList();}
   }}
   public void removeTag(String wordList) {
       for (String tagName:wordList.split(" ")){
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
                            getDrawingModel().set(f, tagsKey, newTags);
                        }
                    }
                    updateList();
                }
            }
   }
}
