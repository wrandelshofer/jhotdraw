/* @(#)CloseFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.DocumentOrientedViewController;

/**
 * Closes the active view after letting the user save unsaved changes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CloseFileAction extends AbstractSaveUnsavedChangesAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.close";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public CloseFileAction(Application app, DocumentOrientedViewController view) {
        super(app, view);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    public CloseFileAction(Application app) {
        this(app, null);
    }

    @Override
    protected CompletionStage<Void> doIt(DocumentOrientedViewController view) {
        if (view != null) {
            app.remove(view);
        }
        return CompletableFuture.completedFuture(null);
    }
}
