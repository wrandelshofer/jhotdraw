/*
 * @(#)AbstractPreferencesAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.app;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.action.AbstractApplicationAction;

/**
 * Displays a preferences dialog for the application.
 * <p>
 *
 * @author Werner Randelshofer
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
        ApplicationLabels.getResources().configureAction(this, ID);
    }
}
