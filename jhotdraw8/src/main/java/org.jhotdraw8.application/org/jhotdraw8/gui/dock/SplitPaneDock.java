package org.jhotdraw8.gui.dock;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;


public class SplitPaneDock
        extends AbstractDock {
    private final SplitPane splitPane = new SplitPane();

    public SplitPaneDock(Orientation orientation) {
        splitPane.setOrientation(orientation);
        getChildren().add(splitPane);
        CustomBinding.bindContent(splitPane.getItems(), getDockChildren(),
                DockNode::getNode);
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
