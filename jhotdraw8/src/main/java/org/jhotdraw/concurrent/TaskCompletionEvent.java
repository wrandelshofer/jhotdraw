/* @(#)TaskCompletionEvent.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.concurrent;

import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javax.annotation.Nullable;

/**
 * TaskCompletionEvent.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TaskCompletionEvent<V> extends Event {
     /**
     * Common supertype for all result event types.
     */
    public static final EventType<TaskCompletionEvent> ANY =
            new EventType<TaskCompletionEvent>(Event.ANY, "WORK_RESULT");

    /**
     * This event occurs when the state of a Worker implementation has
     * transitioned to the SUCCEEDED state.
     */
    public static final EventType<TaskCompletionEvent> SUCCEEDED =
            new EventType<TaskCompletionEvent>(TaskCompletionEvent.ANY, "WORKER_SUCCEEDED");

    /**
     * This event occurs when the state of a Worker implementation has
     * transitioned to the CANCELLED state.
     */
    public static final EventType<TaskCompletionEvent> CANCELLED =
            new EventType<TaskCompletionEvent>(TaskCompletionEvent.ANY, "WORKER_CANCELLED");

    /**
     * This event occurs when the state of a Worker implementation has
     * transitioned to the FAILED state.
     */
    public static final EventType<TaskCompletionEvent> FAILED =
            new EventType<TaskCompletionEvent>(TaskCompletionEvent.ANY, "WORKER_FAILED");
    /** The exception. */
    @Nullable
    private final Throwable exception;
    /** The value. */
    @Nullable
    private final V value;

    /**
     * Create a new TaskCompletionEvent. Specify the worker and the event type.
     *
     * @param worker The Worker which is firing the event. The Worker really
     *               should be an EventTarget, otherwise the EventTarget
     *               for the event will be null.
     * @param eventType The type of event. This should not be null.
     * @param value The result value in case of success.
     * @param exception The exception in case of failure.
     */
    public TaskCompletionEvent(Worker<V> worker, EventType<? extends Event> eventType, @Nullable V value,@Nullable Throwable exception) {
        super(worker, worker instanceof EventTarget ? (EventTarget) worker : null, eventType);
        if (eventType==ANY) {
            throw new IllegalArgumentException("eventType==ANY");
        }
        this.value = value;
        this.exception = exception;
    }

    /* Create a new TaskCompletionEvent with event type {@code SUCCEEDED}
     * and all other values set to null.
     */
    public TaskCompletionEvent() {
        this(null,SUCCEEDED,null,null);
    }
    
   /**
     * The Worker on which the Event initially occurred.
     *
     * @return The Worker on which the Event initially occurred.
     */
    @Override public Worker<V> getSource() {
        return (Worker<V>) super.getSource();
    }
   /**
     * The result state.
     *
     * @return The Worker on which the Event initially occurred.
     */
    public Worker.State getState() {
        switch (getEventType().getName()) {
            case "WORKER_SUCCEEDED":
                return Worker.State.SUCCEEDED;
            case "WORKER_CANCELLED":
                return Worker.State.CANCELLED;
            case "WORKER_FAILED":
                return Worker.State.FAILED;
            default:
                throw new InternalError("Bad event name:"+getEventType().getName());
        }
    }
    
    /** Returns the exception in case of a failure.
    *
    * @return exception
    */
    @Nullable
    public Throwable getException() {
        return exception;
    }

    /** Returns the value in case of a success.
    *
    * @return exception
    */
    @Nullable
    public V getValue() {
        return value;
    }
    
    

}
