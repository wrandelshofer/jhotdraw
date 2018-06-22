/* @(#)FontChooserController.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;


import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.util.Resources;

public class FontChooserController {

    @FXML
    private Button addCollectionButton;
    @FXML
    private ListView<FontCollection> collectionList;
    @FXML
    private ListView<FontFamily> familyList;
    @FXML
    private Label fontNameLabel;
    @FXML
    private URL location;

    private final ObjectProperty<FontChooserModel> model = new SimpleObjectProperty<>();

    private final ObjectProperty<EventHandler<ActionEvent>> onAction = new SimpleObjectProperty<>();

    @FXML
    private TextArea previewTextArea;
    @FXML
    private Button removeCollectionButton;
    @FXML
    private Button removeFamilyButton;
    @FXML
    private ResourceBundle resources;

    @FXML
    private ListView<FontTypeface> typefaceList;

    private void addDroppedFamiliesToCollection(@Nullable FontCollection collection, String[] familyNames) {
        final FontChooserModel model = getModel();
        FontCollection allFonts = model.getAllFonts();
        if (collection == null) {
            collection = createFontCollection();
            getModel().getFontCollections().add(collection);
        }
        if (collection.isSmartCollection()) {
            return;
        }
        final ObservableList<FontFamily> existing = collection.getFamilies();
        final ArrayList<FontFamily> collected = DefaultFontChooserModelFactory.collectFamiliesNamed(allFonts.getFamilies(), familyNames);
        for (FontFamily family : collected) {
            if (!existing.contains(family)) {
                existing.add(family);
            }
        }
        existing.sort(Comparator.comparing(FontFamily::getName));
    }

    @NonNull
    private FontCollection createFontCollection()  {
        final Resources labels = Resources.getResources("org.jhotdraw8.gui.Labels");
        FontCollection collection = new FontCollection(labels.getString("FontCollection.unnamed"), Collections.emptyList());
        return collection;
    }

    public FontChooserModel getModel() {
        return model.get();
    }

    public void setModel(FontChooserModel value) {
        model.set(value);
    }

    public EventHandler<ActionEvent> getOnAction() {
        return onAction.get();
    }

    public void setOnAction(EventHandler<ActionEvent> value) {
        onAction.set(value);
    }

    @Nullable
    public String getSelectedFontName() {
        FontTypeface typeface = typefaceList == null ? null : typefaceList.getSelectionModel().getSelectedItem();
        return typeface == null ? null : typeface.getName();
    }

    private void initButtonDisableBehavior() {
        removeCollectionButton.disableProperty().bind( Bindings.createBooleanBinding(()->{
            FontCollection newv=collectionList.getSelectionModel().getSelectedItem();
            return newv == null || newv.isSmartCollection();
        },collectionList.getSelectionModel().selectedItemProperty()));
        
        removeFamilyButton.disableProperty().bind(
                Bindings.createBooleanBinding(() -> {
                    FontCollection selectedCollection = collectionList.getSelectionModel().getSelectedItem();
                    FontFamily selectedFamily = familyList.getSelectionModel().getSelectedItem();
                    return selectedFamily == null || selectedCollection == null || selectedCollection.isSmartCollection();
                }, collectionList.getSelectionModel().selectedItemProperty(), familyList.getSelectionModel().selectedItemProperty())
        );
    }

    private void initDeleteKeyBehavior() {
        familyList.setOnKeyReleased(evt -> {
            if (evt.getCode() == KeyCode.DELETE) {
                FontCollection fontCollection = collectionList.getSelectionModel().getSelectedItem();
                if (!fontCollection.isSmartCollection()) {
                    fontCollection.getFamilies().remove(familyList.getSelectionModel().getSelectedItem());
                }
                evt.consume();
            }
        });
        collectionList.setOnKeyReleased(evt -> {
            if (evt.getCode() == KeyCode.DELETE) {
                FontCollection fontCollection = collectionList.getSelectionModel().getSelectedItem();
                if (!fontCollection.isSmartCollection()) {
                    collectionList.getItems().remove(collectionList.getSelectionModel().getSelectedIndex());
                }
                evt.consume();
            }
        });
    }

    private void initDoubleClickBehavior() {
        final EventHandler<MouseEvent> onMouseHandler = evt -> {
            if (evt.getClickCount() == 2 && getOnAction() != null && getSelectedFontName() != null) {
                getOnAction().handle(new ActionEvent(evt.getSource(), evt.getTarget()));
            }
        };
        typefaceList.setOnMousePressed(onMouseHandler);
        familyList.setOnMousePressed(onMouseHandler);
    }

    private void initListCellsWithDragAndDropBehavior() {
        familyList.setCellFactory(lv -> {
            final TextFieldListCell<FontFamily> listCell = new TextFieldListCell<FontFamily>();
            listCell.setOnDragDetected(evt -> {
                Dragboard dragBoard = familyList.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                String familyNames
                        = familyList.getSelectionModel().getSelectedItems().stream().map(FontFamily::getName).collect(Collectors.joining("\n"));
                content.put(DataFormat.PLAIN_TEXT, familyNames);
                dragBoard.setDragView(listCell.snapshot(null, null));
                dragBoard.setContent(content);
                evt.consume();
            });
            return listCell;
        });
        
        collectionList.setCellFactory(lv -> {
            final TextFieldListCell<FontCollection> listCell = new TextFieldListCell<FontCollection>() {
                @Override
                public void updateItem(@Nullable FontCollection item, boolean empty) {
                    super.updateItem(item, empty);
                    setEditable(item != null && !item.isSmartCollection());
                }
                
            };
            listCell.setConverter(new StringConverter<FontCollection>() {
                @Override
                public FontCollection fromString(String string) {
                    final FontCollection item = listCell.getItem();
                    item.setName(string);
                    return item;
                }
                
                @NonNull
                @Override
                public String toString(@NonNull FontCollection item) {
                    return (item.isSmartCollection()) ? item.getName() + "•" : item.getName();
                }
                
            });
            listCell.setOnDragOver(evt -> {
                if ((listCell.getItem() == null || !listCell.getItem().isSmartCollection())
                        && evt.getDragboard().hasString()) {
                    evt.acceptTransferModes(TransferMode.COPY);
                }
                evt.consume();
            });
            
            listCell.setOnDragDropped(evt -> {
                boolean success = false;
                if ((listCell.getItem() == null || !listCell.getItem().isSmartCollection())
                        && evt.getDragboard().hasString()) {
                    String droppedString = evt.getDragboard().getString();
                    addDroppedFamiliesToCollection(listCell.getItem(),
                            droppedString.split("\n")
                    );
                    success = true;
                }
                evt.setDropCompleted(success);
                evt.consume();
            });
            return listCell;
        });
    }

    private void initListSelectionBehavior() throws MissingResourceException {
        final Resources labels = Resources.getResources("org.jhotdraw8.gui.Labels");

        collectionList.getSelectionModel().selectedItemProperty().addListener((o, oldv, newv) -> {
            familyList.setItems(newv == null ? null : newv.getFamilies());
                if (!familyList.getItems().isEmpty()) {
                    familyList.getSelectionModel().select(0);
                }
        });
        familyList.getSelectionModel().selectedItemProperty().addListener((o, oldv, newv) -> {
            typefaceList.setItems(newv == null ? null : newv.getTypefaces());
            if (newv != null && !newv.getTypefaces().isEmpty()) {
                final ObservableList<FontTypeface> items = typefaceList.getItems();
                boolean found = false;
                for (int i = 0, n = items.size(); i < n; i++) {
                    if (items.get(i).isRegular()) {
                        typefaceList.getSelectionModel().select(i);
                        found = true;
                        break;
                    }
                }
                if (!found&&!typefaceList.getItems().isEmpty()) {
                    typefaceList.getSelectionModel().select(0);
                }
            }
        });
        typefaceList.getSelectionModel().selectedItemProperty().addListener((o, oldv, newv) -> {
            if (newv == null) {
                fontNameLabel.setText(labels.getString("FontChooser.nothingSelected"));
                previewTextArea.setFont(new Font("System Regular", 24));

            } else {
                fontNameLabel.setText(newv.getName());
                previewTextArea.setFont(new Font(newv.getName(), 24));
            }
        });
    }

    private void initPreferencesBehavior() {
        Preferences prefs = Preferences.userNodeForPackage(FontChooserController.class);
        previewTextArea.setText(prefs.get("fillerText", "Now is the time for all good men."));
        previewTextArea.textProperty().addListener((o, oldv, newv) -> {
            prefs.put("fillerText", newv);
        });
    }

    private void initUpdateViewFromModelBehavior() {
        model.addListener((o, oldv, newv) -> {
            if (newv != null) {
                collectionList.setItems(newv.getFontCollections());
            }
        });
    }

    @FXML
    void initialize() {
        assert previewTextArea != null : "fx:id=\"previewTextArea\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert fontNameLabel != null : "fx:id=\"fontNameLabel\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert addCollectionButton != null : "fx:id=\"addCollectionButton\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert removeCollectionButton != null : "fx:id=\"removeCollectionButton\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert removeFamilyButton != null : "fx:id=\"removeFamilyButton\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert collectionList != null : "fx:id=\"collectionList\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert familyList != null : "fx:id=\"familyList\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert typefaceList != null : "fx:id=\"typefaceList\" was not injected: check your FXML file 'FontChooser.fxml'.";

        initUpdateViewFromModelBehavior();
        initListSelectionBehavior();
        initButtonDisableBehavior();
        initDoubleClickBehavior();
        initPreferencesBehavior();
        initListCellsWithDragAndDropBehavior();

        initDeleteKeyBehavior();

    }

    @NonNull
    public ObjectProperty<FontChooserModel> modelProperty() {
        return model;
    }

    @NonNull
    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    @FXML
    void onAddCollectionPerformed(ActionEvent event) {
        FontCollection collection=createFontCollection();
            getModel().getFontCollections().add(collection);
            collectionList.getSelectionModel().select(collection);
    }

    @FXML
    void onRemoveCollectionPerformed(ActionEvent event) {
          FontCollection collection=collectionList.getSelectionModel().getSelectedItem();
          if (collection!=null&&!collection.isSmartCollection()) {
            getModel().getFontCollections().remove(collection);
          }
    }

    @FXML
    void onRemoveFamllyPerformed(ActionEvent event) {
          FontCollection collection=collectionList.getSelectionModel().getSelectedItem();
          FontFamily family=familyList.getSelectionModel().getSelectedItem();
          if (collection!=null&&!collection.isSmartCollection()&&family!=null) {
            collection.getFamilies().remove(family);
          }

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

}
