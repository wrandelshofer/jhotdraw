/*
 * @(#)AbstractViewControllerAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on the active {@link Activity}, or on a specific {@code Activity}.
 * <p>
 * If the active view or the specified view is disabled, the
 * AbstractViewControllerAction is disabled as well.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractActivityAction<A extends Activity> extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    /**
     * Set this to true if the action may create a new view if none exists.
     */
    private boolean mayCreateActivity;
    private Class<A> pClass;
    @Nullable
    private final ChangeListener<Activity> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        BooleanBinding binding = Bindings.isNotEmpty(disablers).or(app.disabledProperty()).or(app.activeActivityProperty().isNull());
        if (newValue != null && (pClass == null || pClass.isAssignableFrom(newValue.getClass()))) {
            disabled.bind(binding.or(newValue.disabledProperty()));
        } else if (mayCreateActivity) {
            disabled.bind(binding);
        } else {
            disabled.set(true);
        }
    };
    @Nullable
    private final Activity activity;

    /**
     * Creates a new instance which acts on the specified activity of the
     * application.
     *
     * @param app       The application.
     * @param activity      The activity. If activity is null then the action acts on
     *                  the active activity of the application. Otherwise it will act on the
     *                  specified activity.
     * @param activityClass the type of the activity. This is used for type checks.
     */
    public AbstractActivityAction(@NonNull Application app, @Nullable A activity, Class<A> activityClass) {
        super(app);
        this.pClass = activityClass;
        this.activity = activity;
        if (activity != null) {
            activeViewListener.changed(null, null, activity);
        } else {
            app.activeActivityProperty().addListener(activeViewListener);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public A getActivity() {
        Activity p = (activity != null) ? activity : app.getActiveActivity();
        return p == null || pClass == null || pClass.isAssignableFrom(p.getClass()) ? (A) p : null;
    }

    @Override
    protected final void onActionPerformed(ActionEvent event, Application app) {
        onActionPerformed(event, getActivity());
    }

    /**
     * This method is invoked when the action is not disabled and the event is
     * not consumed.
     *
     * @param event    the action event
     * @param activity the activity
     */
    protected abstract void onActionPerformed(ActionEvent event, A activity);

    /**
     * Returns to true if the action may create a new activity if none exists
     *
     * @return true
     */
    protected boolean isMayCreateActivity() {
        return mayCreateActivity;
    }

    /**
     * Set this to true if the action may create a new activity if none exists.
     * If this is false, the action will be disabled, if no activity is
     * available.
     *
     * @param b the new value
     */
    protected void setMayCreateActivity(boolean b) {
        mayCreateActivity = b;
    }
}
