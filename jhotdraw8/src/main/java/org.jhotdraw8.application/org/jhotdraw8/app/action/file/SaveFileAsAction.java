/*
 * @(#)SaveFileAsAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.FileBasedActivity;

/**
 * Presents an {@code URIChooser} and then saves the active view to the
 * specified location.
 * <p>
 *
 * @author Werner Randelshofer
 */
public class SaveFileAsAction extends SaveFileAction {

public static final String ID = "file.saveAs";

    /**
     * Creates a new instance.
     *
     * @param view the view
     */
    public SaveFileAsAction(@NonNull FileBasedActivity view) {
        super(view, ID, true);
    }

}
