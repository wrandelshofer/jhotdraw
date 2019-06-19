/* @(#)HandlesInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Disableable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.gui.PlatformUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class MessagesInspector implements Inspector {
    private final static int MAX_LENGTH = 100 * 1024;
    @FXML
    private TextArea messagesField;
    private Node node;
    /**
     * Holds the disablers.
     * <p>
     * This field is protected, so that it can be accessed by subclasses.
     */
    protected final SetProperty<Object> disablers = new SimpleSetProperty<>();

    @FXML
    void initialize() {
        assert messagesField != null : "fx:id=\"messagesField\" was not injected: check your FXML file 'MessagesInspector.fxml'.";

        ChangeListener<String> changeListener = (o, oldv, newv) -> {
            String text = messagesField.getText();
            if (text != null && text.length() > MAX_LENGTH) {
                int p = text.indexOf('\n', text.length() - MAX_LENGTH);
                text = text.substring(p + 1);
                messagesField.setText(text);
            }
            messagesField.appendText(newv + '\n');
        };

        disablers.addListener((SetChangeListener.Change<? extends Object> change) -> {
            if (change.getElementAdded() instanceof Worker) {
                Worker<?> workState = (Worker<?>) change.getElementAdded();
                workState.messageProperty().addListener(changeListener);
            }
            if (change.getElementRemoved() instanceof Worker) {
                Worker<?> workState = (Worker<?>) change.getElementRemoved();
                workState.messageProperty().removeListener(changeListener);
            }

        });
    }

    public MessagesInspector() {
        this(GridInspector.class.getResource("MessagesInspector.fxml"));
    }

    public MessagesInspector(Disableable disableable) {
        this(GridInspector.class.getResource("MessagesInspector.fxml"));
        disablers.set(disableable.disablers());
    }

    public MessagesInspector(@Nonnull URL fxmlUrl) {
        init(fxmlUrl);
    }


    private void init(@Nonnull URL fxmlUrl) {
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

    @Override
    public void setDrawingView(@Nullable DrawingView view) {
        //throw new UnsupportedOperationException();
    }

    @Override
    public Node getNode() {
        return node;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    void onClear(ActionEvent event) {
        messagesField.setText(null);
    }

    /**
     * Bind this with a {@link org.jhotdraw8.app.Disableable}.
     */
    public SetProperty<Object> getDisablersProperty() {
        return disablers;
    }
}
