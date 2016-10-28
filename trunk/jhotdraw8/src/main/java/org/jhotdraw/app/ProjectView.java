/* @(#)ProjectView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

import org.jhotdraw.collection.HierarchicalMap;
import java.net.URI;
import java.util.concurrent.CompletionStage;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.input.DataFormat;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.beans.PropertyBean;

/**
 * A {@code ProjectView} provides a user interface for a project which is identified
 * by an URI.
 * <p>
 * The life-cycle of project view objects is managed by an application. See the class
 * comment of {@link Application} on how to launch an application.
 * <p>
 * The lifecycle of a project view consists of the following steps:
 * <ol>
 * <li><b>Creation</b><br>
 * The application instantiates the project view object by calling {@code newInstance()}
 * on the class of the view.
 * </li>
 * <li><b>Initialisation</b><br>
 * The application calls the following methods:
 * {@code getActionMap().setParent();setApplication(); init()}. Then it either
 * calls {@code clear()} or {@code read()}.
 * </li>
 * <li><b>Start</b><br>
 * The application adds the component of the project view to a container (for example a
 * JFrame) and then calls {@code start()}.
 * </li>
 * <li><b>Activation</b><br>
 * When a view becomes the active project view of the application, application calls
 * {@code activate()}.
 * </li>
 * <li><b>Deactivation</b><br>
 * When a view is not anymore the active project view of the application, application
 * calls {@code deactivate()}. At a later time, the project view may become activated
 * again.
 * </li>
 * <li><b>Stop</b><br>
 * The application calls {@code stop()} on the project view and then removes the
 * component from its container. At a later time, the project view may be started again.
 * </li>
 * <li><b>Dispose</b><br>
 * When the view is no longer needed, application calls {@code dispose()} on the
 * view, followed by
 * {@code setApplication(null);}, {@code getActionMap().setParent(null)} and then
 * removes all references to it, so that it can be garbage collected.
 * </li>
 * </ol>
 *
 * @design.pattern Application Framework, KeyAbstraction.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ProjectView extends Disableable, PropertyBean {

    /**
     * Initializes the view. This method must be called before the view can be
     * used.
     */
    public void init();

    /**
     * Returns the scene node which renders the view.
     *
     * @return The node.
     */
    public Node getNode();

    /**
     * Provides a title for the view
     *
     * @return The title property.
     */
    public StringProperty titleProperty();

    // convenience method
    default public String getTitle() {
        return titleProperty().get();
    }

    default public void setTitle(String newValue) {
        titleProperty().set(newValue);
    }

    /**
     * The modified property is set to true by the view.
     *
     * @return the property
     */
    public ReadOnlyBooleanProperty modifiedProperty();

    default public boolean isModified() {
        return modifiedProperty().get();
    }

    /**
     * Clears the modified property.
     */
    public void clearModified();

    public ObjectProperty<URI> uriProperty();

    default public URI getURI() {
        return uriProperty().get();
    }

    default public void setURI(URI newValue) {
        uriProperty().set(newValue);
    }

    /**
     * The application property is maintained by the application.
     *
     * @return the property
     */
    public ObjectProperty<Application> applicationProperty();

    default public Application getApplication() {
        return applicationProperty().get();
    }

    default public void setApplication(Application newValue) {
        applicationProperty().set(newValue);
    }

    /**
     * Asynchronously reads data from the specified URI and appends it to the
     * content of the view. This method must not change the current document
     * in case of a read failure.
     * <p>
     * The application typically installs a disabler on the view during a read
     * operation. The disabler is removed when the callback is invoked.
     * </p>
     *
     * @param uri the URI
     * @param format the desired data format, null means default data format should be used
     * @param append whether to append to the current document or to replace it.
     * @return Returns a CompletionStage which is completed when the read 
     * operation has finished.
     */
    public CompletionStage<Void> read(URI uri, DataFormat format, boolean append);

    /**
     * Asynchronously writes the content data of view to the specified URI using
     * a Worker.
     * <p>
     * The application typically installs a disabler on the view during a read
     * operation. The disabler is removed when the callback is invoked.
     *
     * @param uri the URI
     * @param format the desired data format, null means default data format should be used
     * @return Returns a CompletionStage which is completed when the write 
     * operation has finished.
     */
    public CompletionStage<Void> write(URI uri, DataFormat format);

    /**
     * Clears the view.
     *
     * @return Returns a CompletionStage which is completed when the clear 
     * operation has finished.
     */
    public CompletionStage<Void> clear();

    /**
     * The action map of the view.
     *
     * @return the action map
     */
    public HierarchicalMap<String, Action> getActionMap();

    public IntegerProperty disambiguationProperty();

    default public int getDisambiguation() {
        return disambiguationProperty().get();
    }

    default public void setDisambiguation(int newValue) {
        disambiguationProperty().set(newValue);
    }

    default public boolean isEmpty() {
        return !isModified() && getURI() == null;
    }

    /**
     * Starts the view.
     */
    public void start();

    /**
     * Activates the view.
     */
    public void activate();

    /**
     * Deactivates the view.
     */
    public void deactivate();

    /**
     * Stops the view.
     */
    public void stop();

    /**
     * Disposes of the view.
     */
    public void dispose();
}
