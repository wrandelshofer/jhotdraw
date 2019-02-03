/* @(#)TeddyApplication.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.teddy;

import org.jhotdraw8.app.DocumentBasedApplication;
import org.jhotdraw8.app.SimpleApplicationModel;

/**
 * TeddyApplication.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TeddyApplication extends DocumentBasedApplication {

    public TeddyApplication() {
        super();
        setModel(new SimpleApplicationModel(
                "Teddy",
                TeddyApplication.class.getResource("TeddyActivity.fxml"),
                DocumentBasedApplication.class.getResource("DocumentOrientedMenu.fxml"),
                "Text Files", null, "*.txt"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
