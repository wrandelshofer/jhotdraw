/*
 * @(#)OpenFileAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.FileBasedApplication;
import org.jhotdraw8.collection.Key;

import java.util.Collections;
import java.util.Map;

/**
 * Presents an {@code URIChooser} and loads the selected URI into an empty view.
 * If no empty view is available, a new view is created.
 *
 * @author Werner Randelshofer
 */
public class OpenFileAction extends AbstractOpenFileAction {

public static final String ID = "file.open";
    private boolean reuseEmptyViews = true;

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public OpenFileAction(FileBasedApplication app) {
        super(app);
        ApplicationLabels.getResources().configureAction(this, ID);
    }


    @Override
    protected boolean isReuseEmptyViews() {
        return reuseEmptyViews;
    }

    @Override
    protected @NonNull Map<Key<?>, Object> getReadOptions() {
        return Collections.emptyMap();
    }
}
