/*
 * @(#)AbstractViewAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ProjectView;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on behalf of a {@link ProjectView}.
 * <p>
 If the current ProjectView object is disabled or is null, the
 AbstractViewAction is disabled as well.
 <p>
 * A property name can be specified. When the specified property 
 * changes or when the current view changes, method updateView
 * is invoked.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractViewAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;

    private final ProjectView view;
    /** Set this to true if the action may create a new view if none exists.*/
    private boolean mayCreateView;
    private final ChangeListener<ProjectView> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
            BooleanBinding binding = Bindings.isNotEmpty(disablers).or(app.disabledProperty());
        if (newValue == null) {
            disabled.bind(binding);
        } else {
            disabled.bind(binding.or(newValue.disabledProperty()));
        }
    };

    /** Creates a new instance which acts on the specified view of the application.
     * @param app The application.
     * @param view The view. If view is null then the action acts on the active view
     *  of the application. Otherwise it will act on the specified view.
     */
    public AbstractViewAction(Application app,  ProjectView view) {
        super(app);
        this.view = view;
        if (view!=null) {
            activeViewListener.changed(null, null, view);
        } else {
            app.activeViewProperty().addListener(activeViewListener);
        }
    }

    public ProjectView getActiveView() {
        return (view!=null) ? view : app.getActiveView();
    }

    /** Set this to true if the action may create a new view if none exists.
     * If this is false, the action will be disabled, if no view is available.
     * @param b the new value
     */
    protected void setMayCreateView(boolean b) {
        mayCreateView = b;
    }

    /** Returns to true if the action may create a new view if none exists
     * @return true */
    protected boolean isMayCreateView() {
        return mayCreateView;
    }
}
