/*
 * @(#)FontDialog.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;

import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleAction;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.util.Resources;

import java.io.IOException;
import java.util.Optional;

/**
 * FontDialog for selecting a font family and a font size.
 *
 * @author Werner Randelshofer
 */
public class FontDialog extends Dialog<FontFamilySize> {

    private FontChooserController controller;

    public FontDialog() {
        final Resources labels = ApplicationLabels.getGuiResources();
        final DialogPane dialogPane = getDialogPane();
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FontDialog.class.getResource("FontChooser.fxml"));
            loader.setResources(labels.asResourceBundle());
            loader.load();
            final Parent root = loader.getRoot();
            dialogPane.setContent(root);

            root.getStylesheets().add(FontDialog.class.getResource("fontchooser.css").toString());
            controller = loader.getController();
        } catch (IOException ex) {
            dialogPane.setContent(new Label(ex.getMessage()));
            ex.printStackTrace();
        }

        setResizable(true);

        ButtonType chooseButtonType = new ButtonType(labels.getTextProperty("FontChooser.choose"), ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(labels.getTextProperty("FontChooser.cancel"), ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().setAll(chooseButtonType, cancelButtonType);

        setResultConverter(this::onButton);
        controller.setOnAction(evt -> dialogPane.lookupButton(chooseButtonType).executeAccessibleAction(AccessibleAction.FIRE));

        controller.setModel(getModel());

    }

    /**
     * This model is shared by all font dialogs.
     */
    private static @Nullable FontChooserModel model = null;

    public static @Nullable FontChooserModel getModel() {
        if (model == null) {
            model = new PreferencesFontChooserModelFactory().create();
        }
        return model;
    }

    private @Nullable FontFamilySize onButton(@Nullable ButtonType buttonType) {
        new PreferencesFontChooserModelFactory().writeModelToPrefs(controller.getModel());
        if (buttonType != null && buttonType.getButtonData() == ButtonData.OK_DONE) {
            String selectedFontName = controller.getSelectedFontName();
            double fontSize = controller.getFontSize();
            return new FontFamilySize(selectedFontName, fontSize);
        } else {
            return null;
        }
    }

    public void selectFontName(String fontName) {
        controller.setFontName(fontName);
    }

    public final Optional<FontFamilySize> showAndWait(@Nullable FontFamilySize font) {
        if (font != null) {
            selectFontName(font.getFamily());
            controller.setFontSize(font.getSize());
        }
        return showAndWait();
    }

}
