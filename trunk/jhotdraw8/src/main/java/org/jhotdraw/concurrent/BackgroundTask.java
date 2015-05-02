/* @(#)BackgroundTask.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.concurrent;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;

/**
 * BackgroundTask implements a Task which invokes the result methods
 * failed/succeeded/aborted/finished instead of failed/succeeded/canceled.
 * <p>
 * The finished method is invoked after failed, succeeded or aborted has been invoked.
 * <p>
 * After finished has invoked, the background sends a {@link TaskCompletionEvent}
 * to all handlers who have registered for this event type.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the type of the return value
 */
public abstract class BackgroundTask<V> extends Task<V> {

    /** Adds a listener for completion events.
     * @param handler the handler */
    public void addCompletionHandler(EventHandler<TaskCompletionEvent> handler) {
        addEventHandler(TaskCompletionEvent.ANY, handler);
    }
    /** Removes a listener for completion events. 
     * @param handler the handler */
    public void removeCompletionHandler(EventHandler<TaskCompletionEvent> handler) {
        removeEventHandler(TaskCompletionEvent.ANY, handler);
    }
    
    /** Calls the construct method.
     * @throws java.lang.Exception the exception */
    @Override
    protected V call() throws Exception {
        construct();
        return null;
    }

    /** Override this method for background tasks with a {@code Void} return type.
     * @throws java.lang.Exception the exception */
    protected void construct() throws Exception {
        
    }
     /** This method is called when the task has failed.
     * @param e the exception describing the failure */
     protected void failed(Throwable e) {
         e.printStackTrace();
     }
     /** This method is called after failed/aborted/succeeded has been called. */
     protected void finished() {}
     /** This method is called when the task has been cancelled. */
     protected void aborted() {}
     /** This method is called when the task has succeeded.
     * @param value the result */
     protected void succeeded(V value) {}

    @Override
    final protected void failed() {
       failed(getException());
       finished();
        fireEvent(new TaskCompletionEvent<V>(this, TaskCompletionEvent.FAILED, null, getException()));
    }

    @Override
   final protected void cancelled() {
       aborted();
       finished();
        fireEvent(new TaskCompletionEvent<V>(this, TaskCompletionEvent.CANCELLED, null, null));
    }

    @Override
   final protected void succeeded() {
       succeeded(getValue());
       finished();
        fireEvent(new TaskCompletionEvent<V>(this, TaskCompletionEvent.SUCCEEDED, getValue(), null));
    }
}
