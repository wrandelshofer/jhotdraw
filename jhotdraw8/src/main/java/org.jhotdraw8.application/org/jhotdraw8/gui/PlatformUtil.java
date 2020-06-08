/*
 * @(#)PlatformUtil.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import javafx.application.Platform;
import org.jhotdraw8.annotation.NonNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * PlatformUtil.
 *
 * @author Werner Randelshofer
 */
public class PlatformUtil {

    public static void invokeAndWait(@NonNull Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            FutureTask<Boolean> task = new FutureTask<>(r, true);
            Platform.runLater(task);
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new InternalError(ex);
            }
        }
    }
}
