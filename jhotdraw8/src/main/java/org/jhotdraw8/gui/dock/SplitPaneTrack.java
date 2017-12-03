/* @(#)SplitPaneTrack.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import static javafx.geometry.Orientation.VERTICAL;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

/**
 * SplitPaneTrack.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SplitPaneTrack extends SplitPane implements Track {

    public SplitPaneTrack() {
        this(VERTICAL);
    }

    public SplitPaneTrack(Orientation o) {
        setOrientation(o);
        getStyleClass().add("track");
        setStyle("-fx-background-color:transparent;-fx-border-width:0,0;-fx-padding:0;");
        getItems().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {
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
                            d.setTrack(SplitPaneTrack.this);
                        }
                    }
                }
            }
        });
    }

    public SplitPaneTrack(Node... items) {
        super(items);
    }

    public static SplitPaneTrack createVerticalTrack() {
        return new SplitPaneTrack(VERTICAL);
    }

    public static SplitPaneTrack createHorizontalTrack() {
        return new SplitPaneTrack(Orientation.HORIZONTAL);
    }
}
