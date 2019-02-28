/* @(#)AbstractFindAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.Labels;
import org.jhotdraw8.app.action.AbstractViewControllerAction;

/**
 * Presents a find dialog to the user and then highlights the found items in the
 * active view.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFindAction<V extends Activity> extends AbstractViewControllerAction<V> {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.find";

    /**
     * Creates a new instance.
     *
     * @param app       the application
     * @param view      the view
     * @param viewClass the class of the view
     */
    public AbstractFindAction(Application app, V view, Class<V> viewClass) {
        super(app, view, viewClass);
        Labels.getLabels().configureAction(this, ID);
    }
}
