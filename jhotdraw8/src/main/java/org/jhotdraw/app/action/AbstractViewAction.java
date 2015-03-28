/*
 * @(#)AbstractViewAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on behalf of a {@link View}.
 * <p>
 * If the current View object is disabled or is null, the
 * AbstractViewAction is disabled as well.
 * <p>
 * A property name can be specified. When the specified property 
 * changes or when the current view changes, method updateView
 * is invoked.
 * 
 * @author Werner Randelshofer
 * @version $Id: AbstractViewAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public abstract class AbstractViewAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;

    private final Optional<View> view;
    /** Set this to true if the action may create a new view if none exists.*/
    private boolean mayCreateView;
    private final ChangeListener<View> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        if (newValue == null) {
            disabled.bind(Bindings.or(app.disabledProperty(), disablers.emptyProperty().not()));
        } else {
            disabled.bind(Bindings.or(app.disabledProperty(), Bindings.or(newValue.disabledProperty(), disablers.emptyProperty().not())));
        }
    };

    /** Creates a new instance which acts on the specified view of the application.
     * @param app The application.
     * @param view The view. If view is null then the action acts on the active view
     *  of the application. Otherwise it will act on the specified view.
     */
    public AbstractViewAction(Application app, Optional< View> view) {
        super(app);
        if (view == null) {
            throw new IllegalArgumentException("view is null");
        }
        this.view = view;
        if (view.isPresent()) {
            activeViewListener.changed(null, null, view.get());
        } else {
            app.activeViewProperty().addListener(activeViewListener);
        }
    }

    public Optional<View> getActiveView() {
        return (view.isPresent()) ? view : app.getActiveView();
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
