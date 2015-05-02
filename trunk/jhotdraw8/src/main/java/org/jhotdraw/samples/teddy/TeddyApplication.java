/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.samples.teddy;

import com.sun.javafx.menu.MenuBase;
import com.sun.javafx.scene.control.GlobalMenuAdapter;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jhotdraw.app.ApplicationModel;
import org.jhotdraw.app.DocumentOrientedApplication;
import org.jhotdraw.app.TextAreaView;
import org.jhotdraw.app.View;

/**
 *
 * @author werni
 */
public class TeddyApplication extends DocumentOrientedApplication {

    public TeddyApplication() {
        super();
    }
     @Override
    public View instantiateView() {
        TextAreaView v = new TextAreaView();
        return v;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
