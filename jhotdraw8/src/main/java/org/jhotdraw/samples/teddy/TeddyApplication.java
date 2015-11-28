/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.samples.teddy;

import org.jhotdraw.app.DocumentOrientedApplication;
import org.jhotdraw.app.SimpleApplicationModel;

/**
 *
 * @author werni
 */
public class TeddyApplication extends DocumentOrientedApplication {

    public TeddyApplication() {
        super();
        setModel(new SimpleApplicationModel(
                "Teddy",()->new TeddyView(),
                DocumentOrientedApplication.class.getResource("DocumentOrientedMenu.fxml"),
                "Text Files","*.txt"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
