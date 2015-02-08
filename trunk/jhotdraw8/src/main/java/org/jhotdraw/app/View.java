/* @(#)View.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app;

import org.jhotdraw.collection.HierarchicalMap;
import java.net.URI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.beans.PropertyBean;
import org.jhotdraw.concurrent.TaskCompletionEvent;
import org.jhotdraw.gui.URIChooser;

/**
 * View.
 * @author Werner Randelshofer
 */
public interface View extends Disableable, PropertyBean {

    /** Initializes the view. This method must be called before the view can be used. */
    public void init();

    /** Returns the scene node which renders the view.
     * @return  The node. */
    public Node getNode();

    /** Provides a title for the view
     * @return The title property. */
    public StringProperty titleProperty();

    // convenience method
    default public String getTitle() {
        return titleProperty().get();
    }

    default public void setTitle(String newValue) {
        titleProperty().set(newValue);
    }

    /** The modified property is set to true by the view.
     * @return the property */
    public ReadOnlyBooleanProperty modifiedProperty();

    default public boolean isModified() {
        return modifiedProperty().get();
    }

    /** Clears the modified property. */
    public void clearModified();

    public ObjectProperty<URI> uriProperty();

    default public URI getURI() {
        return uriProperty().get();
    }

    default public void setURI(URI newValue) {
        uriProperty().set(newValue);
    }

    /** The application property is maintained by the application.
     * @return  the property */
    public ObjectProperty<Application> applicationProperty();

    default public Application getApplication() {
        return applicationProperty().get();
    }

    default public void setApplication(Application newValue) {
        applicationProperty().set(newValue);
    }

    /** Asynchronously reads data from the specified URI and appends it to the
     * content of the view. This method does not change the current document in
     * case of a read failure.
     *
     * @param uri the URI
     * @param append whether to append to the current document or to replace it.
     * @param callback Receives events from the worker */
    public void read(URI uri, boolean append, EventHandler<TaskCompletionEvent> callback);

    /** Asynchronously writes the content data of view to the specified URI using a Worker.
     * @param uri the URI
     * @param callback Receives events from the worker */
    public void write(URI uri, EventHandler<TaskCompletionEvent> callback);

    /** Clears the view. */
    public void clear();

    /** The action map of the view.
     * @return the action map  */
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
}
