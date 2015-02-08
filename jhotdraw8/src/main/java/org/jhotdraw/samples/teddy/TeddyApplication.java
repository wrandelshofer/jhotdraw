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

/**
 *
 * @author werni
 */
public class TeddyApplication extends DocumentOrientedApplication {

    public TeddyApplication() {
        super();
    }
    
   
    public void startOFF(Stage stage) throws Exception {
        Parent view = FXMLLoader.load(getClass().getResource("TeddyView.fxml"));

MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem newNotebookMenuItem = new MenuItem("New Notebook...");
        newNotebookMenuItem.setAccelerator(KeyCombination.keyCombination("Meta+N"));
        newNotebookMenuItem.setOnAction(event -> { System.out.println("Action fired"); });
        fileMenu.getItems().add(newNotebookMenuItem);
        menuBar.getMenus().add(fileMenu);
        menuBar.setUseSystemMenuBar(true);        
        
        VBox root = new VBox();
       // root.getChildren().add(menuBar);
        root.getChildren().add(view);
        
        Scene scene = new Scene(root);
        
        List<MenuBase> menus = new ArrayList<>();
menus.add(GlobalMenuAdapter.adapt(fileMenu));

Toolkit.getToolkit().getSystemMenu().setMenus(menus);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
