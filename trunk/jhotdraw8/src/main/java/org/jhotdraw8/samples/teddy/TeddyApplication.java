/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.samples.teddy;

import org.jhotdraw8.app.DocumentOrientedApplication;
import org.jhotdraw8.app.SimpleApplicationModel;

/**
 * TeddyApplication.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TeddyApplication extends DocumentOrientedApplication {

    public TeddyApplication() {
        super();
        setModel(new SimpleApplicationModel(
                "Teddy", TeddyView::new,
                DocumentOrientedApplication.class.getResource("DocumentOrientedMenu.fxml"),
                "Text Files",null,"*.txt"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
