/*
 * @(#)CloseFileAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.FileBasedActivity;
import org.jhotdraw8.app.action.AbstractSaveUnsavedChangesAction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Closes the active view after letting the user save unsaved changes.
 *
 * @author Werner Randelshofer
 */
public class CloseFileAction extends AbstractSaveUnsavedChangesAction {

public static final String ID = "file.close";

    /**
     * Creates a new instance.
     *
     * @param activity the view
     */
    public CloseFileAction(@NonNull FileBasedActivity activity) {
        super(activity);
        ApplicationLabels.getResources().configureAction(this, ID);
    }


    @NonNull
    @Override
    protected CompletionStage<Void> doIt(@Nullable FileBasedActivity view) {
        if (view != null) {
            app.remove(view);
        }
        return CompletableFuture.completedFuture(null);
    }
}
