package org.jhotdraw8.concurrent;

import javafx.concurrent.Worker;

/**
 * Work state can be used to report about the current state
 * of work, and to provide an ability to cancel work in progress.
 */
public interface WorkState extends Worker<Void> {
    void updateMessage(String value);

    void updateMessage(String pattern, Object... arguments);

    void updateProgress(long workDone, long max);

    void updateProgress(double workDone, double max);

    void updateTitle(String title);

    void updateValue(Void value);

    /**
     * Returns true if the worker associated to this work state should cancel.
     *
     * @return true if cancelled
     */
    boolean isCancelled();
}
