/* @(#)AbstractProjectAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.Project;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on the active {@link Project}, or on a specific {@code Project}.
 * <p>
 * If the active project or the specified project is disabled, the
 * AbstractProjectAction is disabled as well.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractProjectAction<P extends Project> extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    /**
     * Set this to true if the action may create a new view if none exists.
     */
    private boolean mayCreateView;
    private Class<P> pClass;
    private final ChangeListener<Project> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        BooleanBinding binding = Bindings.isNotEmpty(disablers).or(app.disabledProperty()).or(app.activeProjectProperty().isNull());
        if (newValue != null && (pClass == null || pClass.isAssignableFrom(newValue.getClass()))) {
            disabled.bind(binding.or(newValue.disabledProperty()));
        } else if (mayCreateView) {
            disabled.bind(binding);
        } else {
            disabled.set(true);
        }
    };
    private final Project project;

    /**
     * Creates a new instance which acts on the specified project of the
     * application.
     *
     * @param app The application.
     * @param project The project. If project is null then the action acts on
     * the active project of the application. Otherwise it will act on the
     * specified project.
     * @param pClass the type of the project. This is used for type checks.
     */
    public AbstractProjectAction(Application app, P project, Class<P> pClass) {
        super(app);
        this.pClass = pClass;
        this.project = project;
        if (project != null) {
            activeViewListener.changed(null, null, project);
        } else {
            app.activeProjectProperty().addListener(activeViewListener);
        }
    }

    @SuppressWarnings("unchecked")
    public P getActiveProject() {
        Project p = (project != null) ? project : app.getActiveProject();
        return p == null || pClass == null || pClass.isAssignableFrom(p.getClass()) ? (P) p : null;
    }

    @Override
    protected final void handleActionPerformed(ActionEvent event, Application app) {
        handleActionPerformed(event, getActiveProject());
    }

    /**
     * This method is invoked when the action is not disabled and the event is
     * not consumed.
     *
     * @param event the action event
     * @param project the project
     */
    protected abstract void handleActionPerformed(ActionEvent event, P project);

    /**
     * Returns to true if the action may create a new view if none exists
     *
     * @return true
     */
    protected boolean isMayCreateProject() {
        return mayCreateView;
    }

    /**
     * Set this to true if the action may create a new project if none exists.
     * If this is false, the action will be disabled, if no project is
     * available.
     *
     * @param b the new value
     */
    protected void setMayCreateProject(boolean b) {
        mayCreateView = b;
    }
}
