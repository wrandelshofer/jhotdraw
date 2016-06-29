/* @(#)FXWorker.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import javafx.application.Platform;

/**
 * FXWorker.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class FXWorker {

    /**
     * Calls the runnable on the global thread pool. The completion stage is
     * completed on the FX Application Thread.
     *
     * @param runnable the runnable
     * @return the completion stage
     */
    public static CompletionStage<Void> run(CheckedRunnable runnable) {
        return run(runnable, null);
    }

    /**
     * Calls the runnable on the executor thread. The completion stage is
     * completed on the FX Application Thread.
     *
     * @param runnable the runnable
     * @param executor the executor, if null then ForkJoinPool#commonPool is
     * used
     * @return the completion stage
     */
    public static CompletionStage<Void> run(CheckedRunnable runnable, Executor executor) {
        CompletableFuture<Void> f = new CompletableFuture<>();
        (executor == null ? ForkJoinPool.commonPool() : executor).execute(() -> {
            try {
                runnable.run();
                Platform.runLater(() -> f.complete(null));
            } catch (Exception e) {
                Platform.runLater(() -> f.completeExceptionally(e));
            }
        });
        return f;
    }

    /**
     * Calls the supplier on the global thread pool. The completion stage is
     * completed on the FX Application Thread.
     *
     * @param <T> the value type
     * @param supplier the supplier
     * @return the completion stage
     */
    public static <T> CompletionStage<T> supply(CheckedSupplier<T> supplier) {
        return supply(supplier, null);
    }

    /**
     * Calls the supplier on the executor thread. The completion stage is
     * completed on the FX Application Thread.
     *
     * @param <T> the value type
     * @param supplier the supplier
     * @param executor the executor, if null then ForkJoinPool#commonPool is
     * used
     * @return the completion stage
     */
    public static <T> CompletionStage<T> supply(CheckedSupplier<T> supplier, Executor executor) {
        CompletableFuture<T> f = new CompletableFuture<>();
        (executor == null ? ForkJoinPool.commonPool() : executor).execute(() -> {
            try {
                T result = supplier.supply();
                Platform.runLater(() -> {
                    try {
                        f.complete(result);
                    } catch (Throwable e) {
                        f.completeExceptionally(e);
                    }
                });
            } catch (Throwable e) {
                Platform.runLater(() -> f.completeExceptionally(e));
            }
        });
        return f;
    }
}
