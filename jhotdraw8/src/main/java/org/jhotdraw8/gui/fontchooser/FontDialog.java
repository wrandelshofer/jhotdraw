/* @(#)FontDialog.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleAction;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import org.jhotdraw8.util.Resources;

/**
 * FontDialog for selecting a font name.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class FontDialog extends Dialog<String> {

    private FontChooserController controller;

    public FontDialog() {
        final Resources labels = Resources.getResources("org.jhotdraw8.gui.Labels");
            final DialogPane dialogPane = getDialogPane();
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FontDialog.class.getResource("FontChooser.fxml"));
            loader.setResources(labels);
            loader.load();
            final Parent root = loader.getRoot();
            dialogPane.setContent(root);
            
            root.getStylesheets().add(FontDialog.class.getResource("fontchooser.css").toString());
            controller = loader.<FontChooserController>getController();
        } catch (IOException ex) {
            dialogPane.setContent(new Label(ex.getMessage()));
            ex.printStackTrace();
        }

        setResizable(true);
        
        ButtonType chooseButtonType = new ButtonType(labels.getTextProperty("FontChooser.choose"), ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(labels.getTextProperty("FontChooser.cancel"), ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().setAll(chooseButtonType,cancelButtonType);
        
        setResultConverter(this::handleButton);
        controller.setOnAction(evt->dialogPane.lookupButton(chooseButtonType).executeAccessibleAction(AccessibleAction.FIRE));
        
             controller.setModel(getModel());
        
    }
    /** This model is shared by all font dialogs. */
private static FontChooserModel model=null;
    public static FontChooserModel getModel() {
        if (model==null) {
            model=new PreferencesFontChooserModelFactory().create();
        }
        return model;
    }
    
    private String handleButton(ButtonType buttonType) {
         new PreferencesFontChooserModelFactory().writeModelToPrefs(controller.getModel());
        if (buttonType !=null&&buttonType.getButtonData() == ButtonData.OK_DONE) {
            return controller == null ? null : controller.getSelectedFontName();
        } else {
            return null;
        }
    }
    
    public void selectFontName(String fontName){
        controller.selectFontName(fontName);
    }
    
        public final Optional<String> showAndWait(String fontName) {
            selectFontName(fontName);
            return showAndWait();
        }
    
}
