/* @(#)DiagrammerMain.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.diagrammer;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * DiagrammerMain.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DiagrammerMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new DiagrammerApplication().start(primaryStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());
        DiagrammerApplication.main(args);
    }

}
