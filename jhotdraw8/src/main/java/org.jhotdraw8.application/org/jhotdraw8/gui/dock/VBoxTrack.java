/*
 * @(#)VBoxDock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.scene.control.ScrollPane;
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
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        CustomBinding.bindContent(vbox.getChildren(), getDockChildren(),
                DockItem::getNode);
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
