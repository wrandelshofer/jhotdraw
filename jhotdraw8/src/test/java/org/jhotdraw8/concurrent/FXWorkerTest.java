/* @(#)FXWorkerTest
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.concurrent;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Werner Randelshofer
 */
public class FXWorkerTest {

    public FXWorkerTest() {
    }

    /**
     * Test of run method, of class FXWorker.
     */
    @Test
    @Disabled
    public void testRunAndGet() throws Exception {
        System.out.println("testRunAndCancel");
        CheckedRunnable runnable = () -> {
            Thread.sleep(100);
            System.out.println("done on Worker Thread");
        };
        CompletableFuture<Void> resultOnWorkerThread = FXWorker.run(runnable);
        CompletableFuture<Void> resultOnApplicationThread = resultOnWorkerThread.thenRun(() -> System.out.println("done on Application Thread"));
        resultOnApplicationThread.get(500L, TimeUnit.MILLISECONDS);
    }

}
