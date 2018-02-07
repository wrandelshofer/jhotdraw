/* @(#)LoadFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.Optional;
import java.util.function.BiFunction;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractProjectAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.DocumentOrientedActivity;

/**
 * Lets the user write unsaved changes of the active view, then presents an
 * {@code URIChooser} and loads the selected URI into the active view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RevertFileAction extends AbstractProjectAction<DocumentOrientedActivity> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.revert";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param project the view
     */
    public RevertFileAction(Application app, DocumentOrientedActivity project) {
        super(app, project, DocumentOrientedActivity.class);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, DocumentOrientedActivity project) {
        if (isDisabled()) {
            return;
        }
        final URI uri = project.getURI();
        final DataFormat dataFormat = project.getDataFormat();
        if (project.isModified()) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Do you want to revert?\nYou will lose your changes when you revert.", ButtonType.YES, ButtonType.CANCEL);
            alert.getDialogPane().setMaxWidth(640.0);
            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.isPresent() && answer.get() == ButtonType.YES) {
                doIt(project, uri, dataFormat);
            }
        } else {
            doIt(project, uri, dataFormat);
        }
    }

    private void doIt(DocumentOrientedActivity view, URI uri, DataFormat dataFormat) {
        view.addDisabler(this);

        final BiFunction<Void, Throwable, Void> handler = (ignore, throwable) -> {
            if (throwable != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, createErrorMessage(throwable));
                alert.getDialogPane().setMaxWidth(640.0);
                alert.showAndWait();
                throwable.printStackTrace();
            }
            view.clearModified();
            view.removeDisabler(this);
            return null;
        };

        if (uri == null) {
            view.clear().handle(handler);
        } else {
            view.read(uri, dataFormat, null, false).handle(handler);
        }
    }

}
