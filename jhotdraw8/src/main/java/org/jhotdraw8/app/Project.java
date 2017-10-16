/* @(#)Project.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import org.jhotdraw8.collection.HierarchicalMap;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.beans.PropertyBean;

/**
 * A {@code Project} provides a user interface for a resource which is
 * identified by an URI.
 * <p>
 * The life-cycle of a project object is managed by an application. See the
 * class comment of {@link Application} on how to launch an application.
 * <p>
 * The lifecycle of a project consists of the following steps:
 * <ol>
 * <li><b>Creation</b><br>
 * The application model instantiates a new project.
 * </li>
 * <li><b>Initialisation</b><br>
 * The application calls the following methods:
 * {@code getActionMap().setParent();setApplication(); init()}. Then it either
 * calls {@code clear()} or {@code read()}.
 * </li>
 * <li><b>Start</b><br>
 * The application adds the component of the project to a container (for example
 * a Stage) and then calls {@code start()}.
 * </li>
 * <li><b>Activation</b><br>
 * When a project becomes the active project of the application, application
 * calls {@code activate()}.
 * </li>
 * <li><b>Deactivation</b><br>
 * When a project is not anymore the active project view of the application,
 * application calls {@code deactivate()}. At a later time, the project view may
 * become activated again.
 * </li>
 * <li><b>Stop</b><br>
 * The application calls {@code stop()} on the project and then removes the
 * component from its container. At a later time, the project may be started
 * again.
 * </li>
 * <li><b>Dispose</b><br>
 * When the project is no longer needed, application calls {@code dispose()} on
 * the project, followed by
 * {@code setApplication(null);}, {@code getActionMap().setParent(null)} and
 * then removes all references to it, so that it can be garbage collected.
 * </li>
 * </ol>
 *
 * @design.pattern Application Framework, KeyAbstraction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Project extends Disableable, PropertyBean {

    /**
     * Activates the view.
     */
    public void activate();

    /**
     * The application property is maintained by the application.
     *
     * @return the property
     */
    @Nonnull
    public ObjectProperty<Application> applicationProperty();

    /**
     * Deactivates the view.
     */
    public void deactivate();

    @Nonnull
    public IntegerProperty disambiguationProperty();

    /**
     * Disposes of the view.
     */
    public void dispose();

    /**
     * The action map of the view.
     *
     * @return the action map
     */
    @Nonnull
    public HierarchicalMap<String, Action> getActionMap();

    @Nullable
    default public Application getApplication() {
        return applicationProperty().get();
    }

    default public void setApplication(@Nullable Application newValue) {
        applicationProperty().set(newValue);
    }

    default public int getDisambiguation() {
        return disambiguationProperty().get();
    }

    default public void setDisambiguation(int newValue) {
        disambiguationProperty().set(newValue);
    }

    /**
     * Returns the scene node which renders the view.
     *
     * @return The node.
     */
    @Nonnull
    public Node getNode();

    // convenience method
    @Nullable
    default public String getTitle() {
        return titleProperty().get();
    }

    default public void setTitle(@Nullable String newValue) {
        titleProperty().set(newValue);
    }

    /**
     * Initializes the view. This method must be called before the view can be
     * used.
     */
    public void init();

    /**
     * Starts the view.
     */
    public void start();

    /**
     * Stops the view.
     */
    public void stop();

    /**
     * Provides a title for the view
     *
     * @return The title property.
     */
    @Nonnull
    public StringProperty titleProperty();

}
