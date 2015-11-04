/* @(#)PlatformUtil.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.gui;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;

/**
 * PlatformUtil.
 *
 * @author Werner Randelshofer
 */
public class PlatformUtil {

    public static void invokeAndWait(Runnable r) {
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
