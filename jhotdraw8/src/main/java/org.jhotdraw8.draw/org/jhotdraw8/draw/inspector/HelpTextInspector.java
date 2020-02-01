/*
 * @(#)HelpTextInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.gui.PlatformUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The help inspector displays the current help text of the drawing view.
 *
 * @author werni
 */
public class HelpTextInspector extends AbstractDrawingViewInspector {

    @FXML
    private TextArea textArea;

    private Node node;

    public HelpTextInspector() {
        this(HelpTextInspector.class.getResource("HelpTextInspector.fxml"));
    }

    public HelpTextInspector(@NonNull URL fxmlUrl) {
        init(fxmlUrl);
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
        });
    }

    protected void onDrawingViewChanged(ObservableValue<? extends DrawingView> observable, @Nullable DrawingView oldValue, @Nullable DrawingView newValue) {

        if (oldValue != null) {
            textArea.textProperty().unbind();
        }
        if (newValue != null) {
            textArea.textProperty().bind(newValue.getEditor().helpTextProperty());
        }
    }

    @Override
    public Node getNode() {
        return node;
    }

}
