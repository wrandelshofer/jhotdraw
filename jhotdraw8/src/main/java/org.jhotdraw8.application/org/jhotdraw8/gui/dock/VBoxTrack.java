/*
 * @(#)VBoxTrack.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;

public class VBoxTrack extends AbstractDockParent implements Track {
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox vbox = new VBox();

    public VBoxTrack() {
        getChildren().add(scrollPane);
        scrollPane.setContent(vbox);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(true);
        getStyleClass().add("track");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setBackground(null);
        scrollPane.setPadding(Insets.EMPTY);
        scrollPane.setBorder(Border.EMPTY);
        scrollPane.borderProperty().addListener((o, u, v) -> System.err.println("VBoxTrack.scrollPane " + v));
        CustomBinding.bindContent(vbox.getChildren(), getDockChildren(),
                DockNode::getNode);
        CustomBinding.bindElements(getDockChildren(), DockChild::showingProperty, showingProperty());
    }

    @Override
    public @NonNull TrackAxis getDockAxis() {
        return TrackAxis.Y;
    }

    public boolean isResizesDockChildren() {
        return false;
    }
}
