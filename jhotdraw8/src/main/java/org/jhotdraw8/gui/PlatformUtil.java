/* @(#)PlatformUtil.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import org.jhotdraw8.annotation.Nonnull;

/**
 * PlatformUtil.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PlatformUtil {

    public static void invokeAndWait(@Nonnull Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            FutureTask<Boolean> task = new FutureTask<>(r, true);
            Platform.runLater(task);
            try {
                task.get();
            } catch (@Nonnull InterruptedException | ExecutionException ex) {
                throw new InternalError(ex);
            }
        }
    }
}
