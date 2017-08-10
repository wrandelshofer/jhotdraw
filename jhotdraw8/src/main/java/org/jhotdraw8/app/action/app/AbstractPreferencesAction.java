/* @(#)AbstractPreferencesAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.app;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Project;

/**
 * Displays a preferences dialog for the application.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: AbstractPreferencesAction.java 1169 2016-12-11 12:51:19Z
 * rawcoder $
 */
public abstract class AbstractPreferencesAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "application.preferences";

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public AbstractPreferencesAction(Application app) {
        super(app);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }
}
