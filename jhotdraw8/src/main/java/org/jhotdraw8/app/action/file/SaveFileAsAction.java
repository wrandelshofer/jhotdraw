/* @(#)SaveFileAsAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentOrientedActivityViewController;

/**
 * Presents an {@code URIChooser} and then saves the active view to the
 * specified location.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SaveFileAsAction extends SaveFileAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "file.saveAs";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public SaveFileAsAction(Application app, DocumentOrientedActivityViewController view) {
        super(app, view, ID, true);
    }

    public SaveFileAsAction(Application app) {
        this(app, null);
    }
}
