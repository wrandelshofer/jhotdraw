/*
 * @(#)FontFamilyDialog.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
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
 * FontDialog for selecting a font family.
 *
 * @author Werner Randelshofer
 */
public class FontFamilyDialog extends Dialog<String> {

    private FontFamilyChooserController controller;

    public FontFamilyDialog() {
        final Resources labels = ApplicationLabels.getGuiResources();
        final DialogPane dialogPane = getDialogPane();
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FontFamilyDialog.class.getResource("FontFamilyChooser.fxml"));
            loader.setResources(labels.asResourceBundle());
            loader.load();
            final Parent root = loader.getRoot();
            dialogPane.setContent(root);

            root.getStylesheets().add(FontFamilyDialog.class.getResource("fontchooser.css").toString());
            controller = loader.<FontChooserController>getController();
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
    @Nullable
    private static FontChooserModel model = null;

    @Nullable
    public static FontChooserModel getModel() {
        if (model == null) {
            model = new PreferencesFontChooserModelFactory().create();
        }
        return model;
    }

    @Nullable
    private String onButton(@Nullable ButtonType buttonType) {
        new PreferencesFontChooserModelFactory().writeModelToPrefs(controller.getModel());
        if (buttonType != null && buttonType.getButtonData() == ButtonData.OK_DONE) {
            return controller.getSelectedFontName();
        } else {
            return null;
        }
    }

    public void selectFontName(String fontName) {
        controller.setFontName(fontName);
    }

    public final Optional<String> showAndWait(@Nullable String fontFamily) {
        if (fontFamily != null) {
            selectFontName(fontFamily);
        }
        return showAndWait();
    }

}
