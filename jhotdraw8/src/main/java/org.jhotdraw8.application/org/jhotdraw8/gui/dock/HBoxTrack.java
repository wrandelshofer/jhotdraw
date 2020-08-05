/*
 * @(#)HBoxDock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
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
        CustomBinding.bindContent(hbox.getChildren(), getDockChildren(),
                DockItem::getNode);
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
