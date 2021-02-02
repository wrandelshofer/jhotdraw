/*
 * @(#)SaveFileAction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.FileBasedActivity;

import java.net.URI;

/**
 * Saves the changes in the active view. If the active view has not an URI, an
 * {@code URIChooser} is presented.
 * <p>
 *
 * @author Werner Randelshofer
 */
public class SaveFileAction extends AbstractSaveFileAction {


    public static final String ID = "file.save";

     /**
      * Creates a new instance.
      *
      * @param view the view
      */
     public SaveFileAction(@NonNull FileBasedActivity view) {
         this(view, false);
     }

    /**
     * Creates a new instance.
     *
     * @param view   the view
     * @param saveAs whether to force a file dialog
     */
    public SaveFileAction(@NonNull FileBasedActivity view, boolean saveAs) {
        this(view, ID, saveAs);
    }

    /**
     * Creates a new instance.
     *
     * @param view   the view
     * @param id     the id
     * @param saveAs whether to force a file dialog
     */
    public SaveFileAction(@NonNull FileBasedActivity view, String id, boolean saveAs) {
        super(view, id, saveAs);
    }


    @Override
    protected void onSaveSucceeded(@NonNull FileBasedActivity v, @NonNull URI uri, DataFormat format) {
        v.setURI(uri);
        v.clearModified();
        v.setDataFormat(format);
        app.getRecentUris().put(uri, format);
    }

}
