/*
 * @(#)AbstractActivityAction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

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

    @Nullable
    private final A activity;

    /**
     * Creates a new instance which acts on the specified activity of the
     * application.
     *
     * @param activity The activity. If activity is null then the action acts on
     *                 the active activity of the application. Otherwise it will act on the
     *                 specified activity.
     */
    public AbstractActivityAction(@NonNull A activity) {
        super(activity.getApplication());
        this.activity = activity;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public A getActivity() {
        return activity;
    }

    @Override
    protected final void onActionPerformed(@NonNull ActionEvent event, @NonNull Application app) {
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


}
