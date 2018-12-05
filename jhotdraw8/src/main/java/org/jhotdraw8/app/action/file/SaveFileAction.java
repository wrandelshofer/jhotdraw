/* @(#)SaveFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import java.net.URI;
import javafx.scene.input.DataFormat;
import javax.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentOrientedActivityViewController;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.net.UriUtil;

/**
 * Saves the changes in the active view. If the active view has not an URI, an
 * {@code URIChooser} is presented.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SaveFileAction extends AbstractSaveFileAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.save";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public SaveFileAction(Application app) {
        this(app, null, false);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public SaveFileAction(Application app, DocumentOrientedActivityViewController view) {
        this(app, view, false);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param saveAs whether to force a file dialog
     */
    public SaveFileAction(Application app, DocumentOrientedActivityViewController view, boolean saveAs) {
        this(app, view, ID, saveAs);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param id the id
     * @param saveAs whether to force a file dialog
     */
    public SaveFileAction(Application app, DocumentOrientedActivityViewController view, String id, boolean saveAs) {
        super(app, view, id, saveAs);
    }

    @Override
    protected URIChooser createChooser(DocumentOrientedActivityViewController view) {
        return app.getModel().createSaveChooser();
    }

    @Override
    protected void handleSucceded(@Nonnull DocumentOrientedActivityViewController v, @Nonnull URI uri, DataFormat format) {
        v.setURI(uri);
       v.clearModified();
        v.setTitle(UriUtil.getName(uri));
        app.addRecentURI(uri,format);
    }

}
