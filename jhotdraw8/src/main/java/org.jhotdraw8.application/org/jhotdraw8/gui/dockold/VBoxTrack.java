/*
 * @(#)VBoxTrack.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dockold;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.jhotdraw8.annotation.NonNull;

/**
 * VBoxTrack.
 *
 * @author Werner Randelshofer
 */
public class VBoxTrack extends VBox implements Track {

    public VBoxTrack() {
        getItems().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                for (Node remitem : c.getRemoved()) {
                    if (remitem instanceof Dock) {
                        Dock d = (Dock) remitem;
                        d.setTrack(null);
                    }
                }
                for (Node additem : c.getAddedSubList()) {
                    if (additem instanceof Dock) {
                        Dock d = (Dock) additem;
                        d.setTrack(VBoxTrack.this);
                    }
                }
            }

            //updateResizableWithParent();
        });
    }

    @Override
    public ObservableList<Node> getItems() {
        return getChildren();
    }

    @NonNull
    @Override
    public Orientation getOrientation() {
        return Orientation.VERTICAL;
    }

}
