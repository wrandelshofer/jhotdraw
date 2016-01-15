/* @(#)NewFXMain.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.samples.grapher;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * NewFXMain.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NewFXMain extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        new GrapherApplication().start(primaryStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       GrapherApplication.main(args);
    }
    
}
