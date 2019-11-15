package org.jhotdraw8.draw.popup;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.util.Resources;

import java.io.IOException;
import java.util.Optional;

public class CssColorDialog {
    private CssColorChooserController controller;
    private Dialog<ButtonType> dialog;

    public CssColorDialog(Window owner) {
        final Resources labels = ApplicationLabels.getGuiResources();
        dialog = new Dialog<>();
        final DialogPane dialogPane = dialog.getDialogPane();
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CssColorChooser.fxml"));
            loader.setResources(labels.asResourceBundle());
            loader.load();
            final Parent root = loader.getRoot();
            dialogPane.setContent(root);

            controller = loader.getController();
            currentColor.bindBidirectional(controller.colorProperty());
        } catch (IOException ex) {
            dialogPane.setContent(new Label(ex.getMessage()));
            ex.printStackTrace();
        }

        dialog.setResizable(false);
//        dialog.initModality(Modality.WINDOW_MODAL); // FIXME does not work on macOS
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(false);

        //dialog.addEventHandler(KeyEvent.ANY, keyEventListener);

        chooseButtonType = new ButtonType(labels.getTextProperty("FontChooser.choose"), ButtonBar.ButtonData.OK_DONE);
        cancelButtonType = new ButtonType(labels.getTextProperty("FontChooser.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().setAll(chooseButtonType, cancelButtonType);

    }

    @Nullable ButtonType chooseButtonType;
    @Nullable ButtonType cancelButtonType;

    private Runnable onSave;
    private Runnable onUse;
    private Runnable onCancel;
    @Nullable
    private ObjectProperty<CssColor> currentColor = new SimpleObjectProperty<>(CssColor.WHITE);
    @Nullable
    private ObjectProperty<CssColor> customColor = new SimpleObjectProperty<>(CssColor.TRANSPARENT);
    /*
        public CssColorDialog() {
        }

        public CssColorDialog(Window owner) {
            if (owner != null) {
                dialog.initOwner(owner);
            }
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setResizable(false);
            dialog.addEventHandler(KeyEvent.ANY, keyEventListener);
        }
    */
    private final EventHandler<KeyEvent> keyEventListener = e -> {
        switch (e.getCode()) {
            case ESCAPE:
                dialog.close();
                break;
            default:
                break;
        }
    };

    public Runnable getOnSave() {
        return onSave;
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    public Runnable getOnUse() {
        return onUse;
    }

    public void setOnUse(Runnable onUse) {
        this.onUse = onUse;
    }

    public Runnable getOnCancel() {
        return onCancel;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public CssColor getCurrentColor() {
        return currentColor.get();
    }

    @Nullable
    public ObjectProperty<CssColor> currentColorProperty() {
        return currentColor;
    }

    public void setCurrentColor(CssColor currentColor) {
        this.currentColor.set(currentColor);
    }

    public CssColor getCustomColor() {
        return customColor.get();
    }

    @Nullable
    public ObjectProperty<CssColor> customColorProperty() {
        return customColor;
    }

    public void setCustomColor(CssColor customColor) {
        this.customColor.set(customColor);
    }

    public void show() {
        final Optional<ButtonType> value = dialog.showAndWait();
        if (value.orElse(null) == chooseButtonType) {
            final Runnable onSave = getOnSave();
            if (onSave != null) {
                onSave.run();
            }
        }
    }

}
