/*
 * @(#)WorkState.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.concurrent;

import javafx.concurrent.Worker;

/**
 * Work state can be used to report about the current state
 * of work, and to provide an ability to cancel work in progress.
 */
public interface WorkState extends Worker<Void> {
    /**
     * Asynchronously updates the current message of the work state.
     * <p>
     * Calls to this method
     * are coalesced and run later on the FX application thread, so calls
     * to this method, even from the FX Application thread, may not
     * necessarily result in immediate updates to the property, and
     * intermediate values may be coalesced to save on event
     * notifications.
     *
     * @param value the new value
     */
    void updateMessage(String value);

    /**
     * Asynchronously updates the current message of the work state.
     * <p>
     * Calls to this method are coalesced as described in {@link #updateMessage(String)}.
     *
     * @param pattern   the message pattern
     * @param arguments the argument values for the message patterns
     */
    void updateMessage(String pattern, Object... arguments);

    /**
     * Asynchronously updates the progress value of the work state.
     * <p>
     * Calls to this method are coalesced as described in {@link #updateMessage(String)}.
     *
     * @param workDone the new workDone value
     * @param max the new max value
     */
    void updateProgress(long workDone, long max);

    /**
     * Asynchronously updates the progress value of the work state.
     * <p>
     * Calls to this method are coalesced as described in {@link #updateMessage(String)}.
     *
     * @param workDone the new workDone value
     * @param max the new max value
     */
    void updateProgress(double workDone, double max);

    /**
     * Asynchronously updates the current title of the work state.
     * <p>
     * Calls to this method are coalesced as described in {@link #updateMessage(String)}.
     *
     * @param title the new value
     */
    void updateTitle(String title);

    /**
     * Asynchronously updates the current value of the work state.
     * <p>
     * Calls to this method are coalesced as described in {@link #updateMessage(String)}.
     *
     * @param value the new value
     */
    void updateValue(Void value);

    /**
     * Returns true if the worker associated to this work state should cancel.
     *
     * @return true if cancelled
     */
    boolean isCancelled();
}
