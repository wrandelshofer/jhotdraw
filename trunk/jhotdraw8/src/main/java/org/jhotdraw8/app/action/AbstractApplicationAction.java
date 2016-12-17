/*
 * @(#)AbstractApplicationAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action;

import javafx.beans.binding.Bindings;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ProjectView;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on an {@link Application}.
 * <p>
 * An AbstractApplicationAction is disabled when it has disablers {@link org.jhotdraw8.app.Disableable}
 * or when its application is disabled.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public abstract class AbstractApplicationAction<V extends ProjectView<V>> extends AbstractAction {

    private static final long serialVersionUID = 1L;
    protected Application<V> app;

    /** Creates a new instance.
     * @param app the application */
    public AbstractApplicationAction(Application<V> app) {
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        this.app = app;
        disabled.unbind();
        disabled.bind(Bindings.isNotEmpty(disablers).or(app.disabledProperty()));
    }

    public final Application<V> getApplication() {
        return app;
    }
}