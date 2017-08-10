/* @(#)AbstractFindAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractProjectAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Project;

/**
 * Presents a find dialog to the user and then highlights the found items in the
 * active view.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFindAction<P extends Project> extends AbstractProjectAction<P> {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.find";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param p the project
     * @param pClass the class of the project
     */
    public AbstractFindAction(Application app, P p, Class<P> pClass) {
        super(app, p,pClass);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }
}
