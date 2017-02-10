/* @(#)VBoxTrack.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.gui.dock;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * VBoxTrack.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class VBoxTrack extends VBox implements Track {

    public VBoxTrack() {
       /* getItems().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {
                while (c.next()) {
                    for (Node remitem : c.getRemoved()) {
                    }
                    for (Node additem : c.getAddedSubList()) {
                        VBox.setVgrow(additem, Priority.SOMETIMES);
                    }
                }
            }
        });*/
    }

    @Override
    public ObservableList<Node> getItems() {
        return getChildren();
    }

    @Override
    public Orientation getOrientation() {
        return Orientation.VERTICAL;
    }

}
