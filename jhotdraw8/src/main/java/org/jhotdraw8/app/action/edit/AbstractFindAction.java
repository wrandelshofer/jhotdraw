/* @(#)AbstractFindAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractViewControllerAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.ViewController;

/**
 * Presents a find dialog to the user and then highlights the found items in the
 * active view.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFindAction<V extends ViewController> extends AbstractViewControllerAction<V> {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.find";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     * @param viewClass the class of the view
     */
    public AbstractFindAction(Application app, V view, Class<V> viewClass) {
        super(app, view,viewClass);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }
}
