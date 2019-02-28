/* @(#)ClearFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentBasedActivity;
import org.jhotdraw8.app.Labels;
import org.jhotdraw8.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw8.util.Resources;

import java.util.concurrent.CompletionStage;

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
     * @param app  the application
     * @param view the view
     */
    public ClearFileAction(Application app, DocumentBasedActivity view) {
        super(app, view);
        Resources labels = Labels.getLabels();
        labels.configureAction(this, "file.clear");
    }

    @Override
    public CompletionStage<Void> doIt(@Nonnull final DocumentBasedActivity view) {
        return view.clear();
    }
}
