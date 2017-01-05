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
import org.jhotdraw8.app.action.AbstractViewAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.DocumentProject;

/**
 * Lets the user write unsaved changes of the active view, then presents an
 * {@code URIChooser} and loads the selected URI into the active view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RevertAction extends AbstractViewAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.revert";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public RevertAction(Application app, DocumentProject view) {
        super(app, view);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(ActionEvent event) {
        if (isDisabled()) {
            return;
        }
        DocumentProject view = (DocumentProject)getActiveView();
        URI uri = view.getURI();
        if (view.isModified()) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Do you want to revert?\nYou will lose your changes when you revert.", ButtonType.YES, ButtonType.CANCEL);
            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.isPresent() && answer.get() == ButtonType.YES) {
                doIt(view, uri);
            }
        } else {
            doIt(view, uri);
        }
    }

    private void doIt(DocumentProject view, URI uri) {
        view.addDisabler(this);

        final BiFunction<Void, Throwable, Void> handler = (ignore, throwable) -> {
            if (throwable != null) {
                new Alert(Alert.AlertType.ERROR, throwable.getLocalizedMessage() == null ? throwable.toString() : throwable.getLocalizedMessage()).showAndWait();
                throwable.printStackTrace();
            }
            view.clearModified();
            view.removeDisabler(this);
            return null;
        };

        if (uri == null) {
            view.clear().handle(handler);
        } else {
            view.read(uri, null, false).handle(handler);
        }
    }

}
