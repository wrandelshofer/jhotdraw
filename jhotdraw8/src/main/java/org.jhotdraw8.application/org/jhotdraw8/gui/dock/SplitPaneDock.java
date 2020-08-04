/*
 * @(#)SplitPaneDock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;


public class SplitPaneDock
        extends AbstractDockParent implements Dock {
    private final SplitPane splitPane = new SplitPane();

    public SplitPaneDock(Orientation orientation) {
        splitPane.setOrientation(orientation);
        getChildren().add(splitPane);
        CustomBinding.bindContent(splitPane.getItems(), getDockChildren(),
                DockItem::getNode);
        CustomBinding.bindElements(getDockChildren(), DockChild::showingProperty, showingProperty());
    }


    @NonNull
    @Override
    public DockAxis getDockAxis() {
        return splitPane.getOrientation() == Orientation.HORIZONTAL ? DockAxis.X : DockAxis.Y;
    }


    @Override
    public boolean isResizesDockChildren() {
        return true;
    }
}
