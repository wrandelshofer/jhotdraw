/* @(#)Activity.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.HierarchicalMap;

/**
 * Represents an activity that the user performs with help of the computer.
 * <p>
 * The life-cycle of an {@code Activity} is managed by an {@link Application},
 * it consists of the following steps:
 * <ol>
 * <li><b>Creation</b><br>
 * The user invokes an {@code Action} that instantiates a new {@code Activity}
 * using the abstract factory provided by the {@code ApplicationModel}.
 * </li>
 * <li><b>Initialisation</b><br>
 * The {@code Action} adds the {@code Activity}  to the {@code Application}.<br>
 * The {@code Application} sets itself in the {@link #applicationProperty()} .<br>
 * The {@code Application} invokes {@link #init}.<br>
 * The {@code Application} retrieves {@link Action}s from the {@code Activity}
 * and creates user interface elements for them.
 * The {@code Application} retrieves the {@link Node} from the {@code Activity}
 * and adds it to one of its scene graphs.
 * </li>
 * <li><b>Start</b><br>
 * The {@code Application} invokes {@link #start()}, to
 * inform the activity that it can start (or resume) execution.
 * </li>
 * <li><b>Stop</b><br>
 * The {@code Application} invokes {@link #stop()}, to
 * inform the activity that it should stop (or suspend) execution.<br>
 * The {@code Application} can invoke {@link #start()} again, to
 * resume execution.
 * </li>
 * <li><b>Destroy</b><br>
 * When the view is no longer needed, the {@code Application} ensures that the
 * activity is stopped.<br>
 * The {@code Application} invokes {@link #destroy}.<br>
 * The {@code Application} removes the {@code Node} of the {@code Activity}
 * from its scene graph.<br>
 * The {@code Application} sets the {@link #applicationProperty()} to null.<br>
 * </li>
 * </ol>
 * <p>
 * An activity can be disabled. See {@link Disableable}.
 * <p>
 * {@link Action}s can store arbitrary transient data in the activity.
 * This facility is provided by extending the interface {@link PropertyBean}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Activity extends Disableable, PropertyBean {
    String APPLICATION_PROPERTY = "application";
    String DISAMBIGUATION_PROPERTY = "disambiguation";
    String TITLE_PROPERTY = "title";

    /**
     * The application property is maintained by the {@code Application}
     * that manages this activity.
     * <p>
     * The value is set to the application before {@link #init} is called.
     * <p>
     * The value is set to null after {@link #destroy} has been called.
     *
     * @return the property
     */
    ObjectProperty<Application> applicationProperty();

    /**
     * Used by the application to display unique titles if multiple
     * activities have the same title.
     *
     * @return the property
     */
    IntegerProperty disambiguationProperty();

    /**
     * Returns actions available for this activity.
     *
     * @return the action map
     */
    HierarchicalMap<String, Action> getActionMap();


    // getter and setter methods for properties

    @Nullable
    default Application getApplication() {
        return applicationProperty().get();
    }

    default void setApplication(@Nullable Application newValue) {
        applicationProperty().set(newValue);
    }

    default int getDisambiguation() {
        return disambiguationProperty().get();
    }

    default void setDisambiguation(int newValue) {
        disambiguationProperty().set(newValue);
    }

    Node getNode();

    @Nullable
    default String getTitle() {
        return titleProperty().get();
    }

    default void setTitle(@Nullable String newValue) {
        titleProperty().set(newValue);
    }

    /**
     * Initializes the activity.
     * <p>
     * See life-cycle in {@link Activity}.
     */
    void init();

    /**
     * Starts the activity.
     * <p>
     * See life-cycle in {@link Activity}.
     */
    void start();

    /**
     * Stops the activity.
     * <p>
     * See life-cycle in {@link Activity}.
     */
    void stop();

    /**
     * Destroys the activity.
     * <p>
     * See life-cycle in {@link Activity}.
     */
    void destroy();

    /**
     * The title of the activity.
     * <p>
     * FIXME should be read-only because it is managed by the activity itself
     *
     * @return the titel property
     */
    StringProperty titleProperty();

}
