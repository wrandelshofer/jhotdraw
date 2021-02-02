/*
 * @(#)HBoxTrack.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;

public class HBoxTrack extends AbstractDockParent implements Track {
    private ScrollPane scrollPane = new ScrollPane();
    private final HBox hbox = new HBox();

    public HBoxTrack() {
        getChildren().add(scrollPane);
        scrollPane.setContent(hbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("track");
        scrollPane.setStyle("-fx-background-color:transparent;-fx-border:none-fx-border-width:0,0;-fx-padding:0;");

        CustomBinding.bindContent(hbox.getChildren(), getDockChildren(),
                DockNode::getNode);
        CustomBinding.bindElements(getDockChildren(), DockChild::showingProperty, showingProperty());
    }

    @Override
    public @NonNull TrackAxis getDockAxis() {
        return TrackAxis.X;
    }

    public boolean isResizesDockChildren() {
        return false;
    }
}
