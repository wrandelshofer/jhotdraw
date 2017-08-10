/* @(#)NewFileAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.file;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentProject;

/**
 * Creates a new view.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NewWindowAction extends NewFileAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "file.newWindow";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public NewWindowAction(Application app) {
        super(app, ID);
    }
}
