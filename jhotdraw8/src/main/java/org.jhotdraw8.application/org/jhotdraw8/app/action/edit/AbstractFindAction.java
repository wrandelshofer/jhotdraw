/*
 * @(#)AbstractFindAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.action.AbstractActivityAction;

/**
 * Presents a find dialog to the user and then highlights the found items in the
 * active view.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractFindAction<V extends Activity> extends AbstractActivityAction<V> {

public static final String ID = "edit.find";

    /**
     * Creates a new instance.
     *
     * @param app       the application
     * @param view      the view
     * @param viewClass the class of the view
     */
    public AbstractFindAction(@NonNull Application app, V view, Class<V> viewClass) {
        super(view);
        ApplicationLabels.getResources().configureAction(this, ID);
    }
}
