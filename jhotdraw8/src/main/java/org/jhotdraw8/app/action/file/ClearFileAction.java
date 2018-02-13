/* @(#)ClearFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.util.concurrent.CompletionStage;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.DocumentOrientedViewModel;

/**
 * Clears (empties) the contents of the active view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ClearFileAction extends AbstractSaveUnsavedChangesAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "file.clear";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public ClearFileAction(Application app, DocumentOrientedViewModel view) {
        super(app, view);
        Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
        labels.configureAction(this, "file.clear");
    }

    @Override
    public CompletionStage<Void> doIt(final DocumentOrientedViewModel view) {
        return view.clear();
    }
}
