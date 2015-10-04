/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.samples.grapher;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import org.jhotdraw.app.DocumentOrientedApplication;
import org.jhotdraw.app.View;
import org.jhotdraw.gui.FileURIChooser;
import org.jhotdraw.gui.URIChooser;

/**
 *
 * @author werni
 */
public class GrapherApplication extends DocumentOrientedApplication {

    public GrapherApplication() {
        super();
    }
    
   
    @Override
    public View instantiateView() {
        GrapherView v = new GrapherView();
        return v;
    }

   @Override
    public URIChooser createOpenChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.getFileChooser().getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        return c;
    }

    @Override
    public URIChooser createSaveChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.SAVE);
        c.getFileChooser().getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        return c;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public MenuBar createMenuBar() {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(getModel().getResources());
        try {
            return loader.load(GrapherApplication.class.getResourceAsStream("GrapherMenuBar.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
    }
    
}
