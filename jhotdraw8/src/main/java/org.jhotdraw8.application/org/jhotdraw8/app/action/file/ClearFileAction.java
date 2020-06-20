/*
 * @(#)ClearFileAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.FileBasedActivity;
import org.jhotdraw8.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw8.util.Resources;

import java.util.concurrent.CompletionStage;

/**
 * Clears (empties) the contents of the active view.
 * <p>
 *
 * @author Werner Randelshofer
 */
public class ClearFileAction extends AbstractSaveUnsavedChangesAction {

public static final String ID = "file.clear";

    /**
     * Creates a new instance.
     *
     * @param app  the application
     * @param view the view
     */
    public ClearFileAction(@NonNull Application app, FileBasedActivity view) {
        super(app, view);
        Resources labels = ApplicationLabels.getResources();
        labels.configureAction(this, "file.clear");
    }

    @Override
    public CompletionStage<Void> doIt(@NonNull final FileBasedActivity view) {
        return view.clear();
    }
}
