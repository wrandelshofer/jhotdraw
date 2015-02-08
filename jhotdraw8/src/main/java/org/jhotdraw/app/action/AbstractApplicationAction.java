/*
 * @(#)AbstractApplicationAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javax.annotation.Nullable;
import org.jhotdraw.app.Application;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on an {@link Application}.
 *
 * @author Werner Randelshofer.
 * @version $Id: AbstractApplicationAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public abstract class AbstractApplicationAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    @Nullable protected Application app;

    /** Creates a new instance.
     * @param app the application */
    public AbstractApplicationAction(Application app) {
        this.app = app;
        disabled.unbind();
        disabled.bind(app.disabledProperty().or(disablers.emptyProperty().not()));
    }

    public Application getApplication() {
        return app;
    }
}
