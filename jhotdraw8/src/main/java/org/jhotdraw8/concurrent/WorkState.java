package org.jhotdraw8.concurrent;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import java.text.MessageFormat;

public class WorkState implements Worker<Void> {
    private class CompletionTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateMessage(String newValue) {
            super.updateMessage(newValue);
        }

        @Override
        protected void updateProgress(long workDone, long max) {
            super.updateProgress(workDone, max);
        }

        @Override
        protected void updateProgress(double workDone, double max) {
            super.updateProgress(workDone, max);
        }

        @Override
        protected void updateTitle(String title) {
            super.updateTitle(title);
        }

        @Override
        protected void updateValue(Void value) {
            super.updateValue(value);
        }
    };
    private final CompletionTask task = new CompletionTask();

    public WorkState() {
    }
    public WorkState(String title) {
        updateTitle(title);
        updateMessage("...");
    }

    @Override
    public State getState() {
        return task.getState();
    }

    @Override
    public ReadOnlyObjectProperty<State> stateProperty() {
        return task.stateProperty();
    }

    @Override
    public Void getValue() {
        return task.getValue();
    }

    @Override
    public ReadOnlyObjectProperty<Void> valueProperty() {
        return task.valueProperty();
    }

    @Override
    public Throwable getException() {
        return task.getException();
    }

    @Override
    public ReadOnlyObjectProperty<Throwable> exceptionProperty() {
        return task.exceptionProperty();
    }

    @Override
    public double getWorkDone() {
        return task.getWorkDone();
    }

    @Override
    public ReadOnlyDoubleProperty workDoneProperty() {
        return task.workDoneProperty();
    }

    @Override
    public double getTotalWork() {
        return task.getTotalWork();
    }

    @Override
    public ReadOnlyDoubleProperty totalWorkProperty() {
        return task.totalWorkProperty();
    }

    @Override
    public double getProgress() {
        return task.getProgress();
    }

    @Override
    public ReadOnlyDoubleProperty progressProperty() {
        return task.progressProperty();
    }

    @Override
    public boolean isRunning() {
        return task.isRunning();
    }

    @Override
    public ReadOnlyBooleanProperty runningProperty() {
        return task.runningProperty();
    }

    @Override
    public String getMessage() {
        return task.getMessage();
    }

    @Override
    public ReadOnlyStringProperty messageProperty() {
        return task.messageProperty();
    }

    @Override
    public String getTitle() {
        return task.getTitle();
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return task.titleProperty();
    }

    @Override
    public boolean cancel() {
        return task.cancel();
    }

    /**
     * Updates the message of the work state.
     * This method is safe to be called from any thread.
     * @param value the new value
     */
    public void updateMessage(String value) {
        task.updateMessage(value);
    }

    public void updateMessage(String pattern, Object... arguments) {
        task.updateMessage(MessageFormat.format(pattern,arguments));
    }

    /**
     * Updates the message of the work state.
     * This method is safe to be called from any thread.
     * @param workDone the new value
     * @param max the maximally expected work
     */
    public void updateProgress(long workDone, long max) {
        task.updateProgress(workDone, max);
    }

    /**
     * Updates the message of the work state.
     * This method is safe to be called from any thread.
     * @param workDone the new value
     * @param max the maximally expected work
     */
    public void updateProgress(double workDone, double max) {
        task.updateProgress(workDone, max);
    }

    /**
     * Updates the message of the work state.
     * This method is safe to be called from any thread.
     * @param title the new value
     */
    public void updateTitle(String title) {
        task.updateTitle(title);
    }

    /**
     * Updates the message of the work state.
     * This method is safe to be called from any thread.
     * @param value the new value
     */
    public void updateValue(Void value) {
        task.updateValue(value);
    }
}
