/* @(#)TeddyViewController.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.teddy;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.jhotdraw.collection.HierarchicalMap;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.collection.Key;
import org.jhotdraw.gui.URIChooser;

/**
 *
 * @author werni
 */
public class TeddyViewController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        //label.setText("Hello World!");
        title.set("New Title");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       // label.textProperty().bind(title);
    }    

    private final ObjectProperty<Node> node = new SimpleObjectProperty<>();
    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper("Untitled");
    
    public Node getNode() {
        return node.get();
    }

    public void setNode(Node newValue) {
        node.set(newValue);
    }

   
}
