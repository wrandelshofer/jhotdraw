/* @(#)AbstractApplicationAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import java.util.concurrent.CompletionException;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Application;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on an {@link Application}.
 * <p>
 * An AbstractApplicationAction is disabled when it has disablers
 * {@link org.jhotdraw8.app.Disableable} or when its application is disabled.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public abstract class AbstractApplicationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    @Nullable
    protected Application app;

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public AbstractApplicationAction(@Nullable Application app) {
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        this.app = app;
        disabled.unbind();
        disabled.bind(Bindings.isNotEmpty(disablers).or(app.disabledProperty()));
    }

    @Nonnull
    protected String createErrorMessage(@Nullable Throwable t) {
        StringBuilder buf = new StringBuilder();
        for (; t != null; t = t.getCause()) {
            if ((t instanceof CompletionException) && t.getCause() != null) {
                continue;
            }
            if ((t instanceof RuntimeException) && t.getCause() != null) {
                continue;
            }

            final String msg = t.getLocalizedMessage();
            if (buf.length() != 0) {
                buf.append('\n');
            }
            buf.append(msg == null ? t.toString() : msg);
        }
        return buf.toString();
    }

    @Nullable
    public final Application getApplication() {
        return app;
    }

    @Override
    protected final void handleActionPerformed(ActionEvent event) {
        handleActionPerformed(event, app);
    }

    /**
     * This method is invoked when the action is not disabled and the event is
     * not consumed.
     *
     * @param event the action event
     * @param app the applicatoin
     */
    protected abstract void handleActionPerformed(ActionEvent event, Application app);

    @Nonnull
    protected Alert createAlert(Alert.AlertType alertType, String message, String headerText) {
        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        GridPane gridPane = new GridPane();
        gridPane.add(textArea, 0, 0);

        Alert alert = new Alert(alertType);
        alert.getDialogPane().setContent(gridPane);
        alert.setHeaderText(headerText);
        alert.getDialogPane().setMaxWidth(640.0);
        return alert;
    }
}
