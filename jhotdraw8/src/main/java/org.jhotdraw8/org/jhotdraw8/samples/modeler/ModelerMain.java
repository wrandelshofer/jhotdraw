/* @(#)ModelerMain.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * ModelerMain.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ModelerMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new ModelerApplication().start(primaryStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());
        ModelerApplication.main(args);
    }

}
