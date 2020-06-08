/*
 * @(#)TeddyApplication.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.teddy;

import javafx.stage.Screen;
import org.jhotdraw8.app.FileBasedApplication;
import org.jhotdraw8.app.SimpleApplicationModel;

/**
 * TeddyApplication.
 *
 * @author Werner Randelshofer
 */
public class TeddyApplication extends FileBasedApplication {

    public TeddyApplication() {
        super();
        SimpleApplicationModel model = new SimpleApplicationModel(
                "Teddy",
                TeddyApplication.class.getResource("TeddyActivity.fxml"),
                FileBasedApplication.getDocumentOrientedMenu(),
                "Text Files", null, "*.txt");
        model.setResources(TeddyLabels.getResources().asResourceBundle());
        model.setMenuFxml(TeddyApplication.class.getResource("TeddyMenuBar.fxml"));
        setModel(model);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (Screen.getPrimary().getOutputScaleX() >= 2.0) {
            // The following settings improve font rendering quality on
            // retina displays (no color fringes around characters).
            System.setProperty("prism.subpixeltext", "on");
            System.setProperty("prism.lcdtext", "false");
        } else {
            // The following settings improve font rendering on
            // low-res lcd displays (less color fringes around characters).
            System.setProperty("prism.text", "t2k");
            System.setProperty("prism.lcdtext", "true");
        }

        launch(args);
    }

}
