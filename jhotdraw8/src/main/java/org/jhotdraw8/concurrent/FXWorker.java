/* @(#)FXWorker.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import javafx.application.Platform;

/**
 * FXWorker.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FXWorker {

    /**
     * Calls the runnable on a new Thread. The completion stage is
     * completed on the FX Application Thread.
     *
     * @param runnable the runnable
     * @return the CompletableFuture
     */
    public static CompletableFuture<Void> run(CheckedRunnable runnable) {
        return run(Executors.newSingleThreadExecutor(), runnable);
    }

    /**
     * Calls the runnable on the executor thread. The completion stage is
     * completed on the FX Application Thread.
     *
     * @param runnable the runnable
     * @param executor the executor, if null then a new thread is created
     * @return the CompletableFuture
     */
    public static CompletableFuture<Void> run(Executor executor, CheckedRunnable runnable) {
        CompletableFuture<Void> f = new CompletableFuture<>();
        Runnable worker = () -> {
            try {
                runnable.run();
                Platform.runLater(() -> f.complete(null));
            } catch (Exception e) {
                Platform.runLater(() -> f.completeExceptionally(e));
            }
        };
        executor.execute(worker);
        return f;
    }

    /**
     * Calls the supplier on a new Thread. The completion stage is
     * completed on the FX Application Thread.
     *
     * @param <T> the value type
     * @param supplier the supplier
     * @return the CompletableFuture
     */
    public static <T> CompletableFuture<T> supply(CheckedSupplier<T> supplier) {
        return supply(Executors.newSingleThreadExecutor(), supplier);
    }

    /**
     * Calls the supplier on the executor thread. The completion stage is
     * completed on the FX Application Thread.
     *
     * @param <T> the value type
     * @param supplier the supplier
     * @param executor the executor
     * @return the CompletableFuture
     */
    public static <T> CompletableFuture<T> supply(Executor executor, CheckedSupplier<T> supplier) {
        CompletableFuture<T> f = new CompletableFuture<>();
        executor.execute(() -> {
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
