/*
 * @(#)ModelerMain.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * ModelerMain.
 *
 * @author Werner Randelshofer
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

        // The following settings improve font rendering quality (less color fringes around characters).
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "true");

        ModelerApplication.main(args);
    }

}
