/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.gui.fontchooser;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author werni
 */
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;
import java.util.prefs.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import org.jhotdraw8.util.Resources;

public class FontChooserController {

    private final ObjectProperty<EventHandler<ActionEvent>> onAction = new SimpleObjectProperty<>();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea previewTextArea;

    @FXML
    private Label fontNameLabel;

    @FXML
    private ListView<FontCollection> collectionList;

    @FXML
    private ListView<FontFamily> familyList;

    @FXML
    private ListView<FontTypeface> typefaceList;

    public EventHandler<ActionEvent> getOnAction() {
        return onAction.get();
    }

    public void setOnAction(EventHandler<ActionEvent> value) {
        onAction.set(value);
    }

    @FXML
    void initialize() {
        assert previewTextArea != null : "fx:id=\"previewTextArea\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert fontNameLabel != null : "fx:id=\"fontNameLabel\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert collectionList != null : "fx:id=\"collectionList\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert familyList != null : "fx:id=\"familyList\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert typefaceList != null : "fx:id=\"typefaceList\" was not injected: check your FXML file 'FontChooser.fxml'.";
        loadFontsAsync();

        collectionList.getSelectionModel().selectedItemProperty().addListener((o, oldv, newv) -> {
            familyList.setItems(newv == null ? null : newv.getFamilies());
        });
        familyList.getSelectionModel().selectedItemProperty().addListener((o, oldv, newv) -> {
            typefaceList.setItems(newv == null ? null : newv.getTypefaces());
            if (!newv.getTypefaces().isEmpty()) {
                typefaceList.getSelectionModel().select(0);
            }
        });
        typefaceList.getSelectionModel().selectedItemProperty().addListener((o, oldv, newv) -> {
            if (newv == null) {
                fontNameLabel.setText(null);
                previewTextArea.setFont(Font.getDefault());
            } else {
                fontNameLabel.setText(newv.getName());
                previewTextArea.setFont(new Font(newv.getName(), 24));
            }
        });

        final EventHandler<MouseEvent> onMouseHandler = evt -> {
            if (evt.getClickCount() == 2&&getOnAction() != null) {
                    getOnAction().handle(new ActionEvent(evt.getSource(), evt.getTarget()));
            }
        };
        collectionList.setOnMousePressed(onMouseHandler);
        familyList.setOnMousePressed(onMouseHandler);
        typefaceList.setOnMousePressed(onMouseHandler);
        
        Preferences prefs=Preferences.userNodeForPackage(FontChooserController.class);
        previewTextArea.setText(prefs.get("fillerText", "Now is the time for all good men."));
        previewTextArea.textProperty().addListener((o,oldv,newv)->{
            prefs.put("fillerText", newv);
        });
    }

    private void loadFontsAsync() {
        Task<ObservableList<FontCollection>> task = new Task<ObservableList<FontCollection>>() {
            @Override
            protected ObservableList<FontCollection> call() throws Exception {
                ObservableList<FontCollection> list = loadFonts();
                return list;
            }
            @Override
            protected void succeeded() {
                collectionList.setItems(getValue());
            }
        };
        ForkJoinPool.commonPool().execute(task);
    }

    private ObservableList<FontCollection> loadFonts() {
        ObservableList<FontCollection> list = FXCollections.observableArrayList();

        FontCollection fontCollection = new FontCollection();

        final ResourceBundle labels = Resources.getBundle("org.jhotdraw8.gui.Labels");
        fontCollection.setName(labels.getString("FontCollection.allFonts"));
        list.add(fontCollection);
        List<String> familyNames = Font.getFamilies();
        for (String familyName : familyNames) {
            FontFamily fontFamily = new FontFamily();
            fontFamily.setName(familyName);
            fontCollection.getFamilies().add(fontFamily);
            final List<String> fontNames = Font.getFontNames(familyName);
            for (String fontName : fontNames) {
                FontTypeface fontTypeface = new FontTypeface();
                String shortName = fontName.startsWith(familyName) ? fontName.substring(familyName.length()).trim() : fontName;
                if (shortName.isEmpty()) {
                    shortName = "Regular";
                }
                fontTypeface.setName(fontName);
                fontTypeface.setShortName(shortName);
                fontFamily.getTypefaces().add(fontTypeface);
            }
        }

        return list;

    }

    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    public void selectFontName(String fontName) {
        final ObservableList<FontCollection> collections = collectionList.getItems();
        for (int i = 0, n = collections.size(); i < n; i++) {
            final FontCollection fontCollection = collections.get(i);
            final ObservableList<FontFamily> families = fontCollection.getFamilies();
            for (int j = 0, m = families.size(); j < m; j++) {
                final FontFamily fontFamily = families.get(j);
                final ObservableList<FontTypeface> typefaces = fontFamily.getTypefaces();
                for (int k = 0, p = typefaces.size(); k < p; k++) {
                    final FontTypeface fontTypeface = typefaces.get(k);
                    if (fontTypeface.getName().equals(fontName)) {
                        collectionList.getSelectionModel().select(i);
                        familyList.getSelectionModel().select(j);
                        typefaceList.getSelectionModel().select(k);
                        break;
                    }
                }
            }
        }
    }

    public String getSelectedFontName() {
        FontTypeface typeface = typefaceList == null ? null : typefaceList.getSelectionModel().getSelectedItem();
        return typeface == null ? null : typeface.getName();
    }
}
