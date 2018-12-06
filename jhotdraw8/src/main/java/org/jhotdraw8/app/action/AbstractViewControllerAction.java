/* @(#)AbstractViewControllerAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on the active {@link Activity}, or on a specific {@code Activity}.
 * <p>
 If the active view or the specified view is disabled, the
 AbstractViewControllerAction is disabled as well.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractViewControllerAction<V extends Activity> extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    /**
     * Set this to true if the action may create a new view if none exists.
     */
    private boolean mayCreateView;
    private Class<V> pClass;
    @Nullable
    private final ChangeListener<Activity> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        BooleanBinding binding = Bindings.isNotEmpty(disablers).or(app.disabledProperty()).or(app.activeViewProperty().isNull());
        if (newValue != null && (pClass == null || pClass.isAssignableFrom(newValue.getClass()))) {
            disabled.bind(binding.or(newValue.disabledProperty()));
        } else if (mayCreateView) {
            disabled.bind(binding);
        } else {
            disabled.set(true);
        }
    };
    @Nullable
    private final Activity view;

    /**
     * Creates a new instance which acts on the specified view of the
     * application.
     *
     * @param app The application.
     * @param view The view. If view is null then the action acts on
     * the active view of the application. Otherwise it will act on the
     * specified view.
     * @param viewClass the type of the view. This is used for type checks.
     */
    public AbstractViewControllerAction(@Nonnull Application app, @Nullable V view, Class<V> viewClass) {
        super(app);
        this.pClass = viewClass;
        this.view = view;
        if (view != null) {
            activeViewListener.changed(null, null, view);
        } else {
            app.activeViewProperty().addListener(activeViewListener);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public V getActiveView() {
        Activity p = (view != null) ? view : app.getActiveView();
        return p == null || pClass == null || pClass.isAssignableFrom(p.getClass()) ? (V) p : null;
    }

    @Override
    protected final void handleActionPerformed(ActionEvent event, Application app) {
        handleActionPerformed(event, getActiveView());
    }

    /**
     * This method is invoked when the action is not disabled and the event is
     * not consumed.
     *
     * @param event the action event
     * @param view the view
     */
    protected abstract void handleActionPerformed(ActionEvent event, V view);

    /**
     * Returns to true if the action may create a new view if none exists
     *
     * @return true
     */
    protected boolean isMayCreateView() {
        return mayCreateView;
    }

    /**
     * Set this to true if the action may create a new view if none exists.
     * If this is false, the action will be disabled, if no view is
     * available.
     *
     * @param b the new value
     */
    protected void setMayCreateView(boolean b) {
        mayCreateView = b;
    }
}
