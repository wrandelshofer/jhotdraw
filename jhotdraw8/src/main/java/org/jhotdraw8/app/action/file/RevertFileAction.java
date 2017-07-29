/*
 * @(#)LoadFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import java.util.Optional;
import java.util.function.BiFunction;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractProjectAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.DocumentProject;
import org.jhotdraw8.app.Project;

/**
 * Lets the user write unsaved changes of the active view, then presents an
 * {@code URIChooser} and loads the selected URI into the active view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RevertFileAction extends AbstractProjectAction<DocumentProject> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.revert";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param project the view
     */
    public RevertFileAction(Application app, DocumentProject project) {
        super(app, project, DocumentProject.class);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, DocumentProject project) {
        if (isDisabled()) {
            return;
        }
        URI uri = project.getURI();
        if (project.isModified()) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Do you want to revert?\nYou will lose your changes when you revert.", ButtonType.YES, ButtonType.CANCEL);
            alert.getDialogPane().setMaxWidth(640.0);
            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.isPresent() && answer.get() == ButtonType.YES) {
                doIt(project, uri);
            }
        } else {
            doIt(project, uri);
        }
    }

    private void doIt(DocumentProject view, URI uri) {
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
            view.read(uri, null, null, false).handle(handler);
        }
    }

}
